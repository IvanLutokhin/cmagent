package ru.lipetsk.camera.cmagent.media.h264;

import org.apache.mina.core.buffer.IoBuffer;
import ru.lipetsk.camera.cmagent.media.IVideoFrame;

/**
 * Created by Ivan on 09.03.2016.
 */
public class NALUnit implements IVideoFrame {
    private int timestamp;

    private byte[] payload;

    private final FUIndicator fuIndicator;

    public NALUnit(IoBuffer ioBuffer) {
        this.payload = new byte[ioBuffer.limit()];

        ioBuffer.get(this.payload);

        ioBuffer.free();

        this.fuIndicator = new FUIndicator(this.payload[0]);
    }

    @Override
    public int getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int getSize() {
        return this.payload.length;
    }

    @Override
    public byte[] getPayload() {
        return this.payload;
    }

    public FUIndicator getFuIndicator() {
        return this.fuIndicator;
    }
}