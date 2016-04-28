package ru.lipetsk.camera.cmagent.net.common;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Created by Ivan on 15.02.2016.
 */
public class InterleavedFrame {
    private byte magic;

    private byte channel;

    private short length;

    public InterleavedFrame(IoBuffer ioBuffer) {
        this.magic = ioBuffer.get();

        this.channel = ioBuffer.get();

        this.length = ioBuffer.getShort();

        ioBuffer.free();
    }

    public InterleavedFrame(byte magic, byte channel, short length) {
        this.magic = magic;
        this.channel = channel;
        this.length = length;
    }

    public byte getMagic() {
        return magic;
    }

    public void setMagic(byte magic) {
        this.magic = magic;
    }

    public byte getChannel() {
        return channel;
    }

    public void setChannel(byte channel) {
        this.channel = channel;
    }

    public short getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public boolean validate() {
        return this.magic == 0x24 && this.length > 0;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Magic: ").append(this.magic).append("\r\n");
        stringBuilder.append("Channel: ").append(this.channel).append("\r\n");
        stringBuilder.append("Length: ").append(this.length).append("\r\n");

        return stringBuilder.toString();
    }
}