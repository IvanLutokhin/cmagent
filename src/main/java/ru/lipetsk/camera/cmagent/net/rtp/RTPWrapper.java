package ru.lipetsk.camera.cmagent.net.rtp;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lipetsk.camera.cmagent.event.*;
import ru.lipetsk.camera.cmagent.media.h264.FUHeader;
import ru.lipetsk.camera.cmagent.media.h264.FUIndicator;
import ru.lipetsk.camera.cmagent.media.h264.NALUnit;

/**
 * Created by Ivan on 24.03.2016.
 */
public class RTPWrapper extends EventDispatcher implements IEventListener {
    private final static Logger logger = LoggerFactory.getLogger(RTPWrapper.class);

    private IoBuffer ioBuffer;

    public RTPWrapper() {
        this.ioBuffer = IoBuffer.allocate(1);

        this.ioBuffer.setAutoExpand(true);
    }

    @Override
    public void onEventHandle(IEvent event) {
        switch (event.getName()) {
            case Events.RTP_PACKET:
                this.onRtpPacketHandle((RTPPacket) event.getContext());

                break;
            default:
                logger.warn("Ignore event [{}]", event.getName());
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

                this.dispatchEvent(new Event(Events.H264_VIDEO_FRAME, nalUnit));

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

                    this.dispatchEvent(new Event(Events.H264_VIDEO_FRAME, nalUnit));
                }

                break;
        }
    }
}