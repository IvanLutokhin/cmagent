package ru.lipetsk.camera.cmagent.net.rtcp;

import org.apache.mina.core.buffer.IoBuffer;
import ru.lipetsk.camera.cmagent.net.common.IPacket;

/**
 * Created by Ivan on 15.02.2016.
 */
public class RTCPPacket implements IPacket {
    protected byte version;

    protected boolean padding;

    protected byte reportCount;

    protected byte type;

    protected short length;

    protected byte[] payload;

    public RTCPPacket(IoBuffer ioBuffer) {
        byte c;

        // |V=2|P=1|RC=5|
        c = ioBuffer.get();

        this.version = (byte) ((c & 0xC0) >> 6);

        this.padding = ((c & 0x20) >> 5) == 1;

        this.reportCount = (byte) (c & 0x1F);

        // type
        this.type = ioBuffer.get();

        // length
        this.length = ioBuffer.getShort();

        this.payload = new byte[this.length];

        ioBuffer.get(payload);

        ioBuffer.free();
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public boolean isPadding() {
        return padding;
    }

    public void setPadding(boolean padding) {
        this.padding = padding;
    }

    public byte getReportCount() {
        return reportCount;
    }

    public void setReportCount(byte reportCount) {
        this.reportCount = reportCount;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public short getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}