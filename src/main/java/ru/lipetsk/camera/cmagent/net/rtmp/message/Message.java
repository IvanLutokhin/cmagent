package ru.lipetsk.camera.cmagent.net.rtmp.message;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Created by Ivan on 17.02.2016.
 */
public abstract class Message {
    protected final Header header;

    public Message() {
        this.header = new Header(this.getMessageType());
    }

    public Message(Header header) {
        this.header = header;
    }

    public Message(Header header, IoBuffer ioBuffer) {
        this(header);

        this.decode(ioBuffer);
    }

    public Header getHeader() {
        return this.header;
    }

    public abstract MessageType getMessageType();

    public abstract void decode(IoBuffer ioBuffer);

    public abstract IoBuffer encode();
}