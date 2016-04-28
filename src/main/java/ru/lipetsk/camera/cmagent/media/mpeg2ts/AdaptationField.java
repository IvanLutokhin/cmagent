package ru.lipetsk.camera.cmagent.media.mpeg2ts;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Created by Ivan on 21.03.2016.
 */
public class AdaptationField {
    private Packet packet;

    private boolean discontinuityIndicator;

    private boolean randomAccessIndicator;

    private boolean elementaryStreamPriorityIndicator;

    private boolean pcrFlag;

    private boolean opcrFlag;

    private boolean splicingPointFlag;

    private boolean transportPrivateDataFlag;

    private boolean adaptationFieldExtensionFlag;

    private PCR prc;

    private PCR opcr;

    private byte spliceCountdown;

    private byte[] privateData;

    private byte[] adaptationExtension;

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public boolean isDiscontinuityIndicator() {
        return discontinuityIndicator;
    }

    public void setDiscontinuityIndicator(boolean discontinuityIndicator) {
        this.discontinuityIndicator = discontinuityIndicator;
    }

    public boolean isRandomAccessIndicator() {
        return randomAccessIndicator;
    }

    public void setRandomAccessIndicator(boolean randomAccessIndicator) {
        this.randomAccessIndicator = randomAccessIndicator;
    }

    public boolean isElementaryStreamPriorityIndicator() {
        return elementaryStreamPriorityIndicator;
    }

    public void setElementaryStreamPriorityIndicator(boolean elementaryStreamPriorityIndicator) {
        this.elementaryStreamPriorityIndicator = elementaryStreamPriorityIndicator;
    }

    public boolean isPCRFlag() {
        return pcrFlag;
    }

    public void setPCRFlag(boolean pcrFlag) {
        this.pcrFlag = pcrFlag;
    }

    public boolean isOPCRFlag() {
        return opcrFlag;
    }

    public void setOPCRFlag(boolean opcrFlag) {
        this.opcrFlag = opcrFlag;
    }

    public boolean isSplicingPointFlag() {
        return splicingPointFlag;
    }

    public void setSplicingPointFlag(boolean splicingPointFlag) {
        this.splicingPointFlag = splicingPointFlag;
    }

    public boolean isTransportPrivateDataFlag() {
        return transportPrivateDataFlag;
    }

    public void setTransportPrivateDataFlag(boolean transportPrivateDataFlag) {
        this.transportPrivateDataFlag = transportPrivateDataFlag;
    }

    public boolean isAdaptationFieldExtensionFlag() {
        return adaptationFieldExtensionFlag;
    }

    public void setAdaptationFieldExtensionFlag(boolean adaptationFieldExtensionFlag) {
        this.adaptationFieldExtensionFlag = adaptationFieldExtensionFlag;
    }

    public PCR getPCR() {
        return prc;
    }

    public void setPCR(PCR prc) {
        this.prc = prc;
    }

    public PCR getOPCR() {
        return opcr;
    }

    public void setOPCR(PCR opcr) {
        this.opcr = opcr;
    }

    public byte getSpliceCountdown() {
        return spliceCountdown;
    }

    public void setSpliceCountdown(byte spliceCountdown) {
        this.spliceCountdown = spliceCountdown;
    }

    public byte[] getPrivateData() {
        return privateData;
    }

    public void setPrivateData(byte[] privateData) {
        this.privateData = privateData;
    }

    public byte[] getAdaptationExtension() {
        return adaptationExtension;
    }

    public void setAdaptationExtension(byte[] adaptationExtension) {
        this.adaptationExtension = adaptationExtension;
    }

    public void setDirty(boolean dirty) {
        this.packet.setDirty(dirty);
    }

    public void write(IoBuffer ioBuffer, int payloadLength) {
        int length = 183 - payloadLength;

        int remaining = length;

        ioBuffer.put((byte) (length & 0xff));

        byte value = 0;

        if (this.discontinuityIndicator) {
            value |= 0x80;
        }

        if (this.randomAccessIndicator) {
            value |= 0x40;
        }

        if (this.elementaryStreamPriorityIndicator) {
            value |= 0x20;
        }

        if (this.pcrFlag) {
            value |= 0x10;
        }

        if (this.opcrFlag) {
            value |= 0x08;
        }

        if (this.splicingPointFlag) {
            value |= 0x04;
        }

        if (this.transportPrivateDataFlag) {
            value |= 0x02;
        }

        if (this.adaptationFieldExtensionFlag) {
            value |= 0x01;
        }

        ioBuffer.put(value);

        remaining--;

        if (this.pcrFlag && this.prc != null) {
            this.prc.write(ioBuffer);

            remaining -= 6;
        }

        if (this.opcrFlag && this.opcr != null) {
            this.opcr.write(ioBuffer);

            remaining -= 6;
        }

        if (this.splicingPointFlag) {
            ioBuffer.put(this.spliceCountdown);
            remaining--;
        }

        if (this.transportPrivateDataFlag && this.privateData != null) {
            ioBuffer.put(this.privateData);

            remaining -= this.privateData.length;
        }

        if (this.adaptationFieldExtensionFlag && this.adaptationExtension != null) {
            ioBuffer.put(this.adaptationExtension);

            remaining -= this.adaptationExtension.length;
        }

        if (remaining < 0) {
            throw new IllegalStateException("Adaptation field too big!");
        }

        while (remaining-- > 0) {
            ioBuffer.put((byte) 0x00);
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("discontinuityIndicator").append(this.discontinuityIndicator).append("\r\n");

        stringBuilder.append("Random Access Indicator: ").append(this.randomAccessIndicator).append("\r\n");
        stringBuilder.append("Elementary Stream Priority Indicator: ").append(this.elementaryStreamPriorityIndicator).append("\r\n");
        stringBuilder.append("PCR Flag").append(this.pcrFlag).append("\r\n");
        stringBuilder.append("OPCR Flag").append(this.opcrFlag).append("\r\n");
        stringBuilder.append("Splicing Point Flag: ").append(this.splicingPointFlag).append("\r\n");
        stringBuilder.append("Transport Private Data Flag: ").append(this.transportPrivateDataFlag).append("\r\n");
        stringBuilder.append("Adaptation Field Extension Flag: ").append(this.adaptationFieldExtensionFlag).append("\r\n");
        stringBuilder.append("PCR").append(this.prc).append("\r\n");
        stringBuilder.append("OPCR").append(this.opcr).append("\r\n");
        stringBuilder.append("Discontinuity Indicator:").append(this.discontinuityIndicator).append("\r\n");

        stringBuilder.append("Splice Countdown: ").append(this.spliceCountdown).append("\r\n");
        stringBuilder.append("Private Data:").append(this.privateData).append("\r\n");
        stringBuilder.append("Adaptation Extension:").append(this.adaptationExtension).append("\r\n");

        return stringBuilder.toString();
    }
}