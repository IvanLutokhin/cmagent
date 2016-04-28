package ru.lipetsk.camera.cmagent.core.service;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lipetsk.camera.cmagent.core.proxy.ConfigurationProxy;
import ru.lipetsk.camera.cmagent.event.Events;
import ru.lipetsk.camera.cmagent.event.IEvent;
import ru.lipetsk.camera.cmagent.event.IEventListener;
import ru.lipetsk.camera.cmagent.media.h264.NALUnit;
import ru.lipetsk.camera.cmagent.net.rtmp.RTMPClient;
import ru.lipetsk.camera.cmagent.net.rtmp.message.VideoData;

/**
 * Created by Ivan on 28.03.2016.
 */
public class RTMPService extends AbstractService implements IEventListener {
    private final static Logger logger = LoggerFactory.getLogger(RTMPService.class);

    private final static int TIMESTAMP = 40;

    private final String fmsHost;

    private final int fmsPort;

    private final String fmsApp;

    private final String streamName;

    private RTMPClient rtmpClient;

    private byte[] sps;

    private byte[] pps;

    private boolean readyMetaFrame = false;

    private boolean readyKeyFrame = false;

    private int currentTS;

    private int lastTS;

    public RTMPService(String fmsHost, int fmsPort, String fmsApp, String streamName) {
        this.fmsHost = fmsHost;

        this.fmsPort = fmsPort;

        this.fmsApp = fmsApp;

        this.streamName = streamName;
    }

    @Override
    protected void initialize() {
        this.rtmpClient = new RTMPClient();

        this.currentTS = 0;

        this.lastTS = 0;
    }

    public void run() {
        if (!this.rtmpClient.isConnected()) {
            this.rtmpClient.connect(this.fmsHost, this.fmsPort, this.fmsApp);

            this.rtmpClient.publishStream(this.streamName);
        }
    }

    public boolean isRunning() {
        return this.rtmpClient.isConnected();
    }

    @Override
    public void onEventHandle(IEvent event) {
        switch (event.getName()) {
            case Events.H264_VIDEO_FRAME:
                this.onH264VideoFrameHandle((NALUnit) event.getContext());

                break;
            default:
                logger.warn("Ignore event [{}]", event.getName());
        }
    }

    private void onH264VideoFrameHandle(NALUnit nalUnit) {
        switch (nalUnit.getFuIndicator().getType()) {
            case 5:
                this.readyKeyFrame = true;

                break;
            case 6:
                return;
            case 7:
                this.sps = nalUnit.getPayload();

                this.readyKeyFrame = false;

                break;
            case 8:
                this.pps = nalUnit.getPayload();

                this.readyKeyFrame = false;

                break;
        }

        if (!this.readyMetaFrame && this.sps != null && this.pps != null) {
            this.readyMetaFrame = true;

            IoBuffer buffer = this.buildVideoConfigurationFrame();

            VideoData videoData = new VideoData(buffer);

            videoData.getHeader().setTimestamp(0);

            //this.rtmpClient.publishStreamData(videoData);
        }

        if (this.readyMetaFrame && this.readyKeyFrame) {
            IoBuffer buffer = this.buildVideoFrame(IoBuffer.wrap(nalUnit.getPayload()), this.currentTS - this.lastTS, nalUnit.getFuIndicator().getType() == 5);

            VideoData videoData = new VideoData(buffer);

            videoData.getHeader().setTimestamp(this.currentTS);

            //this.rtmpClient.publishStreamData(videoData);

            this.lastTS = this.currentTS;

            this.currentTS += TIMESTAMP;
        }
    }

    private IoBuffer buildVideoConfigurationFrame() {
        IoBuffer ioBuffer = IoBuffer.allocate(1);

        ioBuffer.setAutoExpand(true);

        ioBuffer.put((byte) 0x17); // 0x10 - keyframe; 0x07 - H264_CODEC_ID

        ioBuffer.put((byte) 0x00); // 0: AVC sequence header; 1: AVC NALU; 2: AVC end of sequence

        ioBuffer.putMediumInt(0); // composition time

        ioBuffer.put((byte) 0x01); // configurationVersion

        ioBuffer.put(this.sps[1]); // profile

        ioBuffer.put(this.sps[2]); // profile compat

        ioBuffer.put(this.sps[3]); // level

        ioBuffer.put((byte) 0xff); // 6 bits reserved (111111) + 2 bits nal size length - 1 (11), lengthSizeMinusOne

        ioBuffer.put((byte) 0xe1); // 3 bits reserved (111) + 5 bits number of sps (00001), numOfSequenceParameterSets

        ioBuffer.putShort((short) this.sps.length);

        ioBuffer.put(this.sps);

        ioBuffer.put((byte) 0x01);// numOfPictureParameterSets

        ioBuffer.putShort((short) this.pps.length);

        ioBuffer.put(this.pps);

        ioBuffer.flip();

        return ioBuffer;
    }

    private IoBuffer buildVideoFrame(IoBuffer data, int timestamp, boolean isIDR) {
        IoBuffer ioBuffer = IoBuffer.allocate(1);

        ioBuffer.setAutoExpand(true);

        ioBuffer.put((byte) (isIDR ? 0x17 : 0x27)); // 0x10 - keyframe / 0x20 - inter frame; 0x07 - H264_CODEC_ID

        ioBuffer.put((byte) 0x01); // 0: AVC sequence header; 1: AVC NALU; 2: AVC end of sequence

        ioBuffer.putMediumInt(timestamp); // composition time

        ioBuffer.putInt(data.limit());

        ioBuffer.put(data);

        ioBuffer.flip();

        return ioBuffer;
    }
}
