package ru.lipetsk.camera.cmagent.media.mpeg2ts;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Created by Ivan on 21.03.2016.
 */
public class Packet extends AbstractPacket {
    private boolean transportErrorIndicator;

    private boolean payloadUnitStartIndicator;

    private boolean transportPriority;

    private int pid;

    private int scramblingControl;

    private boolean adaptationFieldFlag;

    private boolean payloadFlag;

    private byte continuityCounter;

    private AdaptationField adaptationField;

    private IoBuffer payloadData;

    public Packet(IoBuffer ioBuffer) {
        super(ioBuffer);
    }

    public boolean isTransportErrorIndicator() {
        return transportErrorIndicator;
    }

    public void setTransportErrorIndicator(boolean transportErrorIndicator) {
        this.transportErrorIndicator = transportErrorIndicator;
    }

    public boolean isPayloadUnitStartIndicator() {
        return payloadUnitStartIndicator;
    }

    public void setPayloadUnitStartIndicator(boolean payloadUnitStartIndicator) {
        this.payloadUnitStartIndicator = payloadUnitStartIndicator;
    }

    public boolean isTransportPriority() {
        return transportPriority;
    }

    public void setTransportPriority(boolean transportPriority) {
        this.transportPriority = transportPriority;
    }

    public int getPID() {
        return pid;
    }

    public void setPID(int pid) {
        this.pid = pid;
    }

    public int getScramblingControl() {
        return scramblingControl;
    }

    public void setScramblingControl(int scramblingControl) {
        this.scramblingControl = scramblingControl;
    }

    public boolean isAdaptationFieldFlag() {
        return adaptationFieldFlag;
    }

    public void setAdaptationFieldFlag(boolean adaptationFieldFlag) {
        this.adaptationFieldFlag = adaptationFieldFlag;
    }

    public boolean isPayloadFlag() {
        return payloadFlag;
    }

    public void setPayloadFlag(boolean payloadFlag) {
        this.payloadFlag = payloadFlag;
    }

    public byte getContinuityCounter() {
        return continuityCounter;
    }

    public void setContinuityCounter(byte continuityCounter) {
        this.continuityCounter = continuityCounter;
    }

    public AdaptationField getAdaptationField() {
        return adaptationField;
    }

    public void setAdaptationField(AdaptationField adaptationField) {
        this.adaptationField = adaptationField;
    }

    public IoBuffer getPayloadData() {
        return payloadData;
    }

    public void setPayloadData(IoBuffer payloadData) {
        this.payloadData = payloadData;
    }

    @Override
    protected void parse() {
        byte sync = this.buffer.get();

        if (sync != MPEG2TS.SYNC) {
            throw new IllegalStateException("Sync byte not equals 0x47");
        }

        int value = this.buffer.getShort();

        this.setTransportErrorIndicator((value & 0x8000) != 0);

        this.setPayloadUnitStartIndicator((value & 0x4000) != 0);

        this.setTransportPriority((value & 0x2000) != 0);

        this.setPID(value & 0x1fff);

        value = this.buffer.get();

        this.setScramblingControl(value & 0xc0);

        this.setAdaptationFieldFlag((value & 0x20) != 0);

        this.setPayloadFlag((value & 0x10) != 0);

        this.setContinuityCounter((byte) (value & 0x0f));

        if (this.adaptationFieldFlag) {
            int adaptationFieldLength = this.buffer.get();

            if (adaptationFieldLength != 0) {
                this.adaptationField = new AdaptationField();

                int remaining = adaptationFieldLength;

                value = this.buffer.get();

                remaining--;

                this.adaptationField.setDiscontinuityIndicator((value & 0x80) != 0);

                this.adaptationField.setRandomAccessIndicator((value & 0x40) != 0);

                this.adaptationField.setElementaryStreamPriorityIndicator((value & 0x20) != 0);

                this.adaptationField.setPCRFlag((value & 0x10) != 0);

                this.adaptationField.setOPCRFlag((value & 0x08) != 0);

                this.adaptationField.setSplicingPointFlag((value & 0x04) != 0);

                this.adaptationField.setTransportPrivateDataFlag((value & 0x02) != 0);

                this.adaptationField.setAdaptationFieldExtensionFlag((value & 0x01) != 0);

                if (this.adaptationField.isPCRFlag()) {
                    this.adaptationField.setPCR(PCR.parse(this.buffer));

                    remaining -= 6;
                }

                if (this.adaptationField.isOPCRFlag()) {
                    this.adaptationField.setOPCR(PCR.parse(this.buffer));

                    remaining -= 6;
                }

                if (this.adaptationField.isSplicingPointFlag()) {
                    int spliceCountdown = this.buffer.get();

                    this.adaptationField.setSpliceCountdown((byte) spliceCountdown);

                    remaining--;
                }

                if (this.adaptationField.isTransportPrivateDataFlag()) {
                    int transportPrivateDataLength = buffer.get();

                    byte[] privateData = new byte[transportPrivateDataLength];

                    buffer.get(privateData);

                    this.adaptationField.setPrivateData(privateData);

                    remaining -= transportPrivateDataLength;
                }

                if (this.adaptationField.isAdaptationFieldExtensionFlag()) {
                    int adaptationExtensionLength = buffer.get();

                    byte[] adaptationExtension = new byte[adaptationExtensionLength];

                    buffer.get(adaptationExtension);

                    this.adaptationField.setAdaptationExtension(adaptationExtension);

                    remaining -= adaptationExtensionLength;
                }

                this.adaptationField.setPacket(this);

//                this.buffer.skip(remaining);
            }
        }

        if (this.payloadFlag) {
            this.setPayloadData(this.buffer.slice());
        }
    }

    @Override
    protected void write() {
        int payloadLength = 0;

        if (this.payloadFlag && this.payloadData != null) {
            this.payloadData.clear();

            payloadLength = this.payloadData.capacity();

            this.buffer.position(MPEG2TS.PACKET_SIZE - payloadLength);

            this.buffer.put(this.payloadData);
        }

        this.buffer.rewind();

        this.buffer.put(MPEG2TS.SYNC);

        int value = 0;

        if (this.transportErrorIndicator) {
            value |= 0x8000;
        }

        if (this.payloadUnitStartIndicator) {
            value |= 0x4000;
        }

        if (this.transportPriority) {
            value |= 0x2000;
        }

        value |= (this.pid & 0x1fff);

        this.buffer.putShort((short) value);

        value = 0;

        value |= (this.scramblingControl & 0xc0);

        if (this.adaptationFieldFlag) {
            value |= 0x20;
        }

        if (this.payloadFlag) {
            value |= 0x10;
        }

        value |= (this.continuityCounter & 0x0f);

        buffer.put((byte) value);

        if (this.adaptationFieldFlag) {
            if (this.adaptationField == null) {
                buffer.put((byte) 0x00);
            } else {
                this.adaptationField.write(this.buffer, payloadLength);
            }
        }

        if (this.payloadFlag && this.payloadData != null) {
            this.payloadData.rewind();

            buffer.put(this.payloadData);
        }

        if (buffer.remaining() != 0) {
            throw new IllegalStateException("buffer.remaining() = " + buffer.remaining() + ", should be zero!");
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Transport Error Indicator: ").append(this.transportErrorIndicator).append("\r\n");

        stringBuilder.append("payloadUnitStartIndicator: ").append(this.payloadUnitStartIndicator).append("\r\n");
        stringBuilder.append("Transport Priority: ").append(this.transportPriority).append("\r\n");
        stringBuilder.append("PID: ").append(this.pid).append("\r\n");
        stringBuilder.append("Scrambling Control: ").append(this.scramblingControl).append("\r\n");
        stringBuilder.append("Adaptation Field Flag: ").append(this.adaptationFieldFlag).append("\r\n");
        stringBuilder.append("Payload Flag: ").append(this.payloadFlag).append("\r\n");
        stringBuilder.append("Continuity Counter: ").append(this.continuityCounter).append("\r\n");
        stringBuilder.append("Adaptation Field: ").append(this.adaptationField).append("\r\n");
        stringBuilder.append("Payload Data: ").append(this.payloadData).append("\r\n");

        return stringBuilder.toString();
    }
}