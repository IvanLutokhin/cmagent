package ru.lipetsk.camera.cmagent.net.rtmp.message;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Created by Ivan on 19.02.2016.
 */
public class ChunkSize extends Message {
    private int value;

    public ChunkSize(Header header, IoBuffer ioBuffer) {
        super(header, ioBuffer);
    }

    public ChunkSize(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.CHUNK_SIZE;
    }

    @Override
    public void decode(IoBuffer ioBuffer) {
        this.value = ioBuffer.getInt();
    }

    @Override
    public IoBuffer encode() {
        return IoBuffer.allocate(4).putInt(this.value).flip();
    }
}
