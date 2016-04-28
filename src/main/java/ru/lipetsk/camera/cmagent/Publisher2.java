package ru.lipetsk.camera.cmagent;

import org.apache.mina.core.buffer.IoBuffer;
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
 * Created by Ivan on 20.02.2016.
 */
public class Publisher2 extends Thread implements IEventListener {
    private RTSPClient rtspClient;

    private RTMPClient rtmpClient;

    private byte[] sps;

    private byte[] pps;

    private boolean readyMetaFrame = false;

    private long startTS;

    private int currentTS = 0;

    private int lastTS = 0;

    private int refreshTS = 0;

    private Runtime runtime = Runtime.getRuntime();

    private IoBuffer ioBuffer;

    public Publisher2() {
        this.startTS = System.currentTimeMillis();

        this.ioBuffer = IoBuffer.allocate(1);

        this.ioBuffer.setAutoExpand(true);
    }

    public void run() {
        this.rtmpClient = new RTMPClient();

        rtmpClient.connect("10.49.12.197", 1935, "live");

        rtmpClient.addEventListener(Events.RTMP_CREATE_STREAM, this);

        rtmpClient.addEventListener(Events.RTMP_PUBLISH_STREAM, this);

        this.rtspClient = new RTSPClient();

        this.rtspClient.connect("10.37.46.220", 554, "h264", new Credentials("admin", "admin"));

        this.rtspClient.addEventListener(Events.RTP_PACKET, this);
    }

    @Override
    public void onEventHandle(IEvent event) {
        switch (event.getName()) {
            case Events.RTMP_CREATE_STREAM:
                this.rtmpClient.publishStream("TestStream2");

                break;
            case Events.RTMP_PUBLISH_STREAM:
                this.rtspClient.play();

                break;
            case Events.RTP_PACKET:
                this.onRtpPacketHandle((RTPPacket) event.getContext());

                this.refreshTS += 40;

                if (this.refreshTS > 300000) {
                    this.rtspClient.getParameter();

                    this.refreshTS = 0;
                }

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
            IoBuffer buffer = this.buildVideoFrame(IoBuffer.wrap(nalUnit.getPayload()), this.currentTS - this.lastTS, nalUnit.getFuIndicator().getType() == 5);

            VideoData videoData = new VideoData(buffer);

            videoData.getHeader().setTimestamp(this.currentTS);

            this.rtmpClient.writeStreamData(videoData);

            this.lastTS = this.currentTS;

            this.currentTS = (int) ((System.currentTimeMillis() - this.startTS));
        }

        if (!this.readyMetaFrame && this.sps != null && this.pps != null) {
            this.readyMetaFrame = true;

            IoBuffer buffer = this.buildVideoConfigurationFrame();

            VideoData videoData = new VideoData(buffer);

            videoData.getHeader().setTimestamp(0);

            this.rtmpClient.writeStreamData(videoData);
        }
    }

    public void onRTPPacketTest(NALUnit nalUnit) {
        System.out.println("NAL Unit type: " + nalUnit.getFuIndicator().getType() + "; NAL Unit size: " + nalUnit.getSize());

        System.out.println("##### Heap utilization statistics [MB] #####");

        //Print used memory
        System.out.println("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024));

        //Print free memory
        System.out.println("Free Memory:" + runtime.freeMemory() / (1024 * 1024));

        //Print total available memory
        System.out.println("Total Memory:" + runtime.totalMemory() / (1024 * 1024));

        //Print Maximum available memory
        System.out.println("Max Memory:" + runtime.maxMemory() / (1024 * 1024));

        nalUnit = null;
    }
/*
    public void onRTPPacket2(NALUnit nalUnit) {
        if (nalUnit.getFuIndicator().getType() == 7) {
            this.currentCount++;
        }

        if (this.currentCount == 1) {
            System.exit(0);
        }

        try {
            this.file = new RandomAccessFile("d:\\stream.h264", "rw");

            this.file.seek(this.file.length());

            this.file.write(RTP.NON_IDR_PICTURE);

            this.file.write(nalUnit.getPayload());

            this.file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onRTPPacketW(NALUnit nalUnit) {
        if (this.currentCount == 5) {
            System.exit(0);
        }

        if (nalUnit.getFuIndicator().getType() == 7) {
            System.out.println("Current frame: " + this.currentCount);

            this.currentCount++;
        }

        switch (nalUnit.getFuIndicator().getType()) {
            case 6:
                return;
            case 7:
                this.sps = nalUnit.getPayload();

                this.readyKeyFrame = false;

                break;
            case 8:
                this.pps = nalUnit.getPayload();

                break;
        }

        if (this.sps != null && this.pps != null) {
            this.muxer.sps = this.sps;

            this.muxer.pps = this.pps;

            this.sps = null; this.pps = null;

            this.readyKeyFrame = true;

            return;
        }

        if (this.readyKeyFrame) {
            this.muxer.write(nalUnit.getPayload(), nalUnit.getFuIndicator().getType() == 5);
        }
    }*/

/*
    public void onRTPPacketRtmp(NALUnit nalUnit) {
        //System.out.println("NAL Unit type: " + nalUnit.getFuIndicator().getType() + "; NAL Unit size: " + nalUnit.get().limit() + "; Current TS: " + this.currentTS);

        System.out.println("NAL Unit type: " + nalUnit.getFuIndicator().getType() + "; NAL Unit size: " + nalUnit.getSize());

        this.currentTS = (int) (System.currentTimeMillis() - this.startTS);

        //this.currentTS += 40;

        //System.out.println("Current TS: " + this.currentTS + "; Delta TS: " + (this.currentTS - this.lastTS));

        this.lastTS = this.currentTS;

        switch (nalUnit.getFuIndicator().getType()) {
            //case 1:
            case 6:
                return;
            case 7:
                if (this.currentCount == 5) {
                    System.exit(0);
                }

                this.currentCount++;

                if (!stop) {
                    this.readyKeyFrame = false;

                    this.sps = nalUnit.getPayload();
                } else {
                    return;
                }

                break;
            case 8:
                if (!stop) {
                    this.readyKeyFrame = false;

                    this.pps = nalUnit.getPayload();
                } else {
                    return;
                }

                break;
        }

        if (this.sps != null && this.pps != null && !this.readyKeyFrame) {
            // TODO: build videoData configuration frame
            IoBuffer buffer = this.buildVideoConfigurationFrame();

            // TODO: send videoData configuration frame
            VideoData videoData = new VideoData(buffer);

            videoData.getHeader().setTimestamp(this.currentTS);

            this.rtmpClient.publishStreamData(videoData);

            // TODO: clear configuration
            this.sps = null; this.pps = null;

            this.readyKeyFrame = true;

            this.stop = true;

            return;
        }

        if (this.readyKeyFrame) {
            //this.currentTS = (int) (System.currentTimeMillis() - this.startTS);

            // TODO: build video frame
            IoBuffer buffer = this.buildVideoFrame(IoBuffer.wrap(nalUnit.getPayload()), (this.currentTS - this.lastTS), nalUnit.getFuIndicator().getType() == 5);

            // TODO: send video frame

            VideoData videoData = new VideoData(buffer);

            videoData.getHeader().setTimestamp(this.currentTS);

            this.rtmpClient.publishStreamData(videoData);
        }
    }
*/

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