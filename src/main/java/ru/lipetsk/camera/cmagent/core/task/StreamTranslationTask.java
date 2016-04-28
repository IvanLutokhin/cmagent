package ru.lipetsk.camera.cmagent.core.task;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lipetsk.camera.cmagent.core.proxy.CameraProxy;
import ru.lipetsk.camera.cmagent.core.proxy.ConfigurationProxy;
import ru.lipetsk.camera.cmagent.core.proxy.StreamProxy;
import ru.lipetsk.camera.cmagent.core.proxy.vo.StreamVO;
import ru.lipetsk.camera.cmagent.event.Event;
import ru.lipetsk.camera.cmagent.event.Events;
import ru.lipetsk.camera.cmagent.event.IEvent;
import ru.lipetsk.camera.cmagent.event.IEventListener;
import ru.lipetsk.camera.cmagent.media.h264.FUHeader;
import ru.lipetsk.camera.cmagent.media.h264.FUIndicator;
import ru.lipetsk.camera.cmagent.media.h264.NALUnit;
import ru.lipetsk.camera.cmagent.net.rtmp.RTMPClient;
import ru.lipetsk.camera.cmagent.net.rtmp.message.VideoData;
import ru.lipetsk.camera.cmagent.net.rtp.RTPPacket;
import ru.lipetsk.camera.cmagent.net.rtsp.Credentials;
import ru.lipetsk.camera.cmagent.net.rtsp.RTSPClient;


/**
 * Created by Ivan on 28.03.2016.
 */
public class StreamTranslationTask extends Task implements IEventListener {
    private final static Logger logger = LoggerFactory.getLogger(StreamTranslationTask.class);

    private final int streamId;

    private String streamName;

    private RTSPClient rtspClient;

    private RTMPClient rtmpClient;

    private byte[] sps;

    private byte[] pps;

    private boolean readyMetaFrame;

    private boolean keyFrame;

    private long startTS;

    private int currentTS;

    private int lastTS;

    private IoBuffer ioBuffer;

    public StreamTranslationTask(String name, int streamId) {
        super(name);

        this.streamId = streamId;
    }

    @Override
    public void execute() {
        logger.debug("Stream [{}] translation task has been running", this.streamId);

        ConfigurationProxy configurationProxy = (ConfigurationProxy) this.facade.retrieveProxy("ConfigurationProxy");

        CameraProxy cameraProxy = (CameraProxy) this.facade.retrieveProxy("CameraProxy");

        StreamProxy streamProxy = (StreamProxy) this.facade.retrieveProxy("StreamProxy");

        StreamVO streamVO = streamProxy.retrieveStream(this.streamId);

        this.streamName = streamVO.getEnterpriseName();

        boolean isRTSPConnected = this.rtspClient.connect(cameraProxy.data().getIpAddress(), streamVO.getRtspPort(), streamVO.getUrl(), new Credentials(cameraProxy.data().getRtspLogin(), cameraProxy.data().getRtspPassword()));

        boolean isRTMPConnected = isRTSPConnected && this.rtmpClient.connect(configurationProxy.data().getFmsHost(), configurationProxy.data().getFmsPort(), streamVO.getFmsApp());

        if (!isRTSPConnected || !isRTMPConnected) {
            if (this.rtspClient.isConnected()) {
                this.rtspClient.disconnect();

                this.rtspClient = null;
            }

            if (this.rtmpClient.isConnected()) {
                this.rtmpClient.disconnect();

                this.rtmpClient = null;
            }
        }
    }

    public boolean isActive() {
        return this.rtspClient.isConnected() && this.rtmpClient.isConnected();
    }

    @Override
    public void onRegister() {
        this.readyMetaFrame = false;

        this.keyFrame = false;

        this.startTS = System.currentTimeMillis();

        this.ioBuffer = IoBuffer.allocate(1);

        this.ioBuffer.setAutoExpand(true);

        this.rtspClient = new RTSPClient();

        this.rtspClient.addEventListener(Events.RTP_PACKET, this);

        this.rtmpClient = new RTMPClient();

        this.rtmpClient.addEventListener(Events.RTMP_CREATE_STREAM, this);

        this.rtmpClient.addEventListener(Events.RTMP_PUBLISH_STREAM, this);

        this.rtmpClient.addEventListener(Events.RTMP_NO_DATA, this);
    }

    @Override
    public void onUnregister() {
        if (this.rtspClient.isConnected()) {
            this.rtspClient.disconnect();

            this.rtspClient = null;
        }

        if (this.rtmpClient.isConnected()) {
            this.rtmpClient.disconnect();

            this.rtmpClient = null;
        }
    }

    @Override
    public void onEventHandle(IEvent event) {
        switch (event.getName()) {
            case Events.RTMP_CREATE_STREAM:
                this.rtmpClient.publishStream(this.streamName);

                break;
            case Events.RTMP_PUBLISH_STREAM:
                this.rtspClient.play();

                break;

            case Events.RTMP_NO_DATA:
                this.rtmpClient.disconnect();

                break;
            case Events.RTP_PACKET:
                this.onRtpPacketHandle((RTPPacket) event.getContext());

                break;
        }
    }

    private void onRtpPacketHandle(RTPPacket rtpPacket) {
        byte payloadType = rtpPacket.getPayloadType();

        if (payloadType >= 96) {
            this.onH264FrameHandle(rtpPacket);
        }
    }

    private void onH264FrameHandle(RTPPacket rtpPacket) {
        if (rtpPacket.getPayloadLength() <= 0) {
            return;
        }

        FUIndicator fuIndicator = new FUIndicator(rtpPacket.getPayload()[0]);

        NALUnit nalUnit;

        switch (fuIndicator.getType()) {
            case 1:
            case 5:
            case 6:
            case 7:
            case 8:
                this.ioBuffer.clear();

                this.ioBuffer.put(rtpPacket.getPayload());

                this.ioBuffer.flip();

                nalUnit = new NALUnit(this.ioBuffer);

                nalUnit.setTimestamp(rtpPacket.getTimestamp());

                this.onH264VideoFrameHandle(nalUnit);

                break;
            case 28:
                FUHeader fuHeader = new FUHeader(rtpPacket.getPayload()[1]);

                byte reconstructedNAL = (byte) ((fuIndicator.get() & 0xe0) | (fuHeader.get() & 0x1f));

                if (fuHeader.isS()) {
                    this.ioBuffer.clear();

                    this.ioBuffer.put(reconstructedNAL);
                }

                this.ioBuffer.put(rtpPacket.getPayload(), 2, rtpPacket.getPayload().length - 2);

                if (fuHeader.isE()) {
                    this.ioBuffer.flip();

                    nalUnit = new NALUnit(this.ioBuffer);

                    nalUnit.setTimestamp(rtpPacket.getTimestamp());

                    this.onH264VideoFrameHandle(nalUnit);
                }

                break;
        }
    }

    private void onH264VideoFrameHandle(NALUnit nalUnit) {
        switch (nalUnit.getFuIndicator().getType()) {
            case 5:
                this.keyFrame = true;

                break;
            case 6:
                return;
            case 7:
                this.sps = nalUnit.getPayload();

                return;
            case 8:
                this.pps = nalUnit.getPayload();

                return;
        }

        this.currentTS = (int) (System.currentTimeMillis() - this.startTS);

        if (this.keyFrame && this.sps != null && this.pps != null) {
            IoBuffer buffer = this.buildVideoConfigurationFrame();

            VideoData videoData = new VideoData(buffer);

            videoData.getHeader().setTimestamp(this.currentTS);

            this.rtmpClient.writeStreamData(videoData);

            this.keyFrame = false;
        }

        IoBuffer buffer = this.buildVideoFrame(IoBuffer.wrap(nalUnit.getPayload()), this.currentTS - this.lastTS, nalUnit.getFuIndicator().getType() == 5);

        VideoData videoData = new VideoData(buffer);

        videoData.getHeader().setTimestamp(this.currentTS);

        this.rtmpClient.writeStreamData(videoData);

        this.lastTS = this.currentTS;


        /*switch (nalUnit.getFuIndicator().getType()) {
            case 6:
                return;
            case 7:
                this.sps = nalUnit.getPayload();

                break;
            case 8:
                this.pps = nalUnit.getPayload();

                break;
        }

        if (this.readyMetaFrame) {
            this.currentTS = (int) (System.currentTimeMillis() - this.startTS);

            IoBuffer buffer = this.buildVideoFrame(IoBuffer.wrap(nalUnit.getPayload()), this.currentTS - this.lastTS, nalUnit.getFuIndicator().getType() == 5);

            VideoData videoData = new VideoData(buffer);

            videoData.getHeader().setTimestamp(this.currentTS);

            this.rtmpClient.writeStreamData(videoData);

            this.lastTS = this.currentTS;
        }

        if (!this.readyMetaFrame && this.sps != null && this.pps != null) {
            this.currentTS = (int) (System.currentTimeMillis() - this.startTS);

            IoBuffer buffer = this.buildVideoConfigurationFrame();

            VideoData videoData = new VideoData(buffer);

            videoData.getHeader().setTimestamp(this.currentTS);

            this.rtmpClient.writeStreamData(videoData);

            this.lastTS = this.currentTS;

            this.readyMetaFrame = true;

            this.sps = null; this.pps = null;
        }*/
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