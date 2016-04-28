package ru.lipetsk.camera.cmagent.net.rtmp.message;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Created by Ivan on 20.02.2016.
 */
public class Abort extends Message {
    private int value;

    public Abort(Header header, IoBuffer ioBuffer) {
        super(header, ioBuffer);
    }

    public Abort(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.ABORT;
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