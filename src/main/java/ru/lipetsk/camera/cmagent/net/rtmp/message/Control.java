package ru.lipetsk.camera.cmagent.net.rtmp.message;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Created by Ivan on 17.02.2016.
 */
public class Control extends Message {
    public enum Type {
        STREAM_BEGIN(0),
        STREAM_EOF(1),
        STREAM_DRY(2),
        SET_BUFFER_LENGTH(3),
        STREAM_IS_RECORDED(4),
        PING_REQUEST(6),
        PING_RESPONSE(7);

        public static Type valueOf(int value) {
            for (Type type : Type.values()) {
                if (type.getValue() == value) {
                    return type;
                }
            }

            return null;
        }

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    private Type type;

    private int streamId;

    private int bufferLength;

    private int timestamp;

    public Control(Header header, IoBuffer ioBuffer) {
        super(header, ioBuffer);
    }

    public Control(Type type, int timestamp) {
        this.type = type;

        this.timestamp = timestamp;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getStreamId() {
        return streamId;
    }

    public void setStreamId(int streamId) {
        this.streamId = streamId;
    }

    public int getBufferLength() {
        return bufferLength;
    }

    public void setBufferLength(int bufferLength) {
        this.bufferLength = bufferLength;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.CONTROL;
    }

    @Override
    public void decode(IoBuffer ioBuffer) {
        this.type = Type.valueOf(ioBuffer.getShort());

        if (this.type == null) {
            throw new IllegalArgumentException("Illegal type value");
        }

        switch (this.type) {
            case STREAM_BEGIN:
            case STREAM_EOF:
            case STREAM_DRY:
            case STREAM_IS_RECORDED:
                this.streamId = ioBuffer.getInt();
                break;
            case SET_BUFFER_LENGTH:
                this.streamId = ioBuffer.getInt();
                this.bufferLength = ioBuffer.getInt();
                break;
            case PING_REQUEST:
            case PING_RESPONSE:
                this.timestamp = ioBuffer.getInt();
                break;
        }
    }

    @Override
    public IoBuffer encode() {
        int size;

        switch (this.type)
        {
            case SET_BUFFER_LENGTH:
                size = 10; break;
            default:
                size = 6;
        }

        IoBuffer ioBuffer = IoBuffer.allocate(size);

        ioBuffer.setAutoExpand(true);

        ioBuffer.putShort((short) this.type.value);

        switch (this.type)
        {
            case STREAM_BEGIN:
            case STREAM_EOF:
            case STREAM_DRY:
            case STREAM_IS_RECORDED:
                ioBuffer.putInt(this.streamId);

                break;
            case SET_BUFFER_LENGTH:
                ioBuffer.putInt(this.streamId);

                ioBuffer.putInt(this.bufferLength);

                break;
            case PING_REQUEST:
            case PING_RESPONSE:
                ioBuffer.putInt(this.timestamp);

                break;
        }

        ioBuffer.flip();

        return ioBuffer;
    }
}
