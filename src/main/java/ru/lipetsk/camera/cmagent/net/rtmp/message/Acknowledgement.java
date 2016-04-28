package ru.lipetsk.camera.cmagent.net.rtmp.message;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Created by Ivan on 20.02.2016.
 */
public class Acknowledgement extends Message {
    private int value;

    public Acknowledgement(Header header, IoBuffer ioBuffer) {
        super(header, ioBuffer);
    }

    public Acknowledgement(int value) {
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
        return MessageType.ACKNOWLEDGEMENT;
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
