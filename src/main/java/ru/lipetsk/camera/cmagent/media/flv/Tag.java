package ru.lipetsk.camera.cmagent.media.flv;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Created by Ivan on 09.03.2016.
 */
public class Tag {
    private byte type;

    private int dataSize;

    private int timestamp;

    private IoBuffer data;

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public IoBuffer getData() {
        return data;
    }

    public void setData(IoBuffer data) {
        this.data = data;
    }

    public IoBuffer get() {
        IoBuffer ioBuffer = IoBuffer.allocate(1);

        ioBuffer.setAutoExpand(true);

        ioBuffer.put(this.type);

        ioBuffer.putMediumInt(this.dataSize);

        ioBuffer.putInt(this.timestamp);

        ioBuffer.putMediumInt(0);

        ioBuffer.flip();

        return ioBuffer;

    }
}