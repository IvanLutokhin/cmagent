package ru.lipetsk.camera.cmagent.net.rtmp.message;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Created by Ivan on 19.02.2016.
 */
public class SetPeerBandwidth extends Message {
    public enum LimitType {
        HARD,
        SOFT,
        DYNAMIC;
    }

    private int value;

    private LimitType limitType;

    public SetPeerBandwidth(Header header, IoBuffer ioBuffer) {
        super(header, ioBuffer);
    }

    public SetPeerBandwidth(int value, LimitType limitType) {
        this.value = value;

        this.limitType = limitType;
    }

    public int getValue() {
        return this.value;
    }

    public LimitType getLimitType() {
        return this.limitType;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.SET_PEER_BANDWIDTH;
    }

    @Override
    public void decode(IoBuffer ioBuffer) {
        this.value = ioBuffer.getInt();

        this.limitType = LimitType.values()[ioBuffer.get()];
    }

    @Override
    public IoBuffer encode() {
        IoBuffer ioBuffer = IoBuffer.allocate(5);

        ioBuffer.putInt(this.value);

        ioBuffer.put((byte) this.limitType.ordinal());

        ioBuffer.flip();

        return ioBuffer;
    }
}
