package ru.lipetsk.camera.cmagent.net.rtp;

import org.apache.mina.core.buffer.IoBuffer;
import ru.lipetsk.camera.cmagent.net.common.IPacket;

/**
 * Created by Ivan on 14.02.2016.
 */
public class RTPPacket implements IPacket {
    private byte version;

    private boolean padding;

    private boolean extension;

    private byte cc;

    private boolean marker;

    private byte payloadType;

    private short sequenceNumber;

    private int timestamp;

    private int ssrc;

    private int[] csrc;

    private short profileExtension;

    private byte[] headerExtension;

    private byte[] payload;

    public RTPPacket() { }

    public RTPPacket(IoBuffer ioBuffer) {
        byte c;

        // |V=2|P=1|X=1|CC=4|
        c = ioBuffer.get();

        this.version = (byte) ((c & 0xC0) >> 6);

        this.padding = ((c & 0x20) >> 5) == 1;

        this.extension = ((c & 0x10) >> 4) == 1;

        this.cc = (byte) (c & 0x0F);

        // |M=1|PT=7|
        c = ioBuffer.get();

        this.marker = ((c & 0x80) >> 7) == 1;

        this.payloadType = (byte) (c & 0x7F);

        this.sequenceNumber = ioBuffer.getShort();

        this.timestamp = ioBuffer.getInt();

        this.ssrc = ioBuffer.getInt();

        // CSRC list
        int[] csrc = new int[this.cc];

        for (int i = 0; i < this.cc; i++) {
            csrc[i] = ioBuffer.getInt();
        }

        // Read the extension header if present
        if (this.extension) {
            this.profileExtension = ioBuffer.getShort();

            int headerSize = ioBuffer.getShort();

            this.headerExtension = new byte[headerSize];

            ioBuffer.get(this.headerExtension);
        }

        // Read the payload
        int payloadSize = ioBuffer.remaining();

        this.payload = new byte[payloadSize];

        ioBuffer.get(this.payload);

        ioBuffer.free();
    }

    public byte getVersion() {
        return this.version;
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

    public boolean isExtension() {
        return extension;
    }

    public void setExtension(boolean extension) {
        this.extension = extension;
    }

    public byte getCC() {
        return cc;
    }

    public void setCC(byte cc) {
        this.cc = cc;
    }

    public boolean isMarker() {
        return marker;
    }

    public void setMarker(boolean marker) {
        this.marker = marker;
    }

    public byte getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(byte payloadType) {
        this.payloadType = payloadType;
    }

    public short getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(short sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getSSRC() {
        return ssrc;
    }

    public void setSSRC(int ssrc) {
        this.ssrc = ssrc;
    }

    public int[] getCSRC() {
        return csrc;
    }

    public void setCSRC(int[] csrc) {
        this.csrc = csrc;
    }

    public short getProfileExtension() {
        return profileExtension;
    }

    public void setProfileExtension(short profileExtension) {
        this.profileExtension = profileExtension;
    }

    public byte[] getHeaderExtension() {
        return headerExtension;
    }

    public void setHeaderExtension(byte[] headerExtension) {
        this.headerExtension = headerExtension;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public int getPayloadLength() {
        return this.payload != null ? this.payload.length : -1;
    }

    public int getHeaderLength() {
        int length = 12 + 4 * this.cc;

        if (this.extension) {
            length += 2 + this.headerExtension.length;
        }

        return length;
    }
}
