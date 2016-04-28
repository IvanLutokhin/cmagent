package ru.lipetsk.camera.cmagent.media.mpeg2ts;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Created by Ivan on 21.03.2016.
 */
public class PSI {
    public static PSI parse(IoBuffer ioBuffer) {
        int tableId = ioBuffer.get();

        int value = ioBuffer.getShort();

        if ((value & 0xc000) != 0x8000) {
            return null;
        }

        int sectionLength = value & 0x0fff;

        ioBuffer.limit(ioBuffer.position() + sectionLength);

        int tableIdExtension = ioBuffer.getShort();

        value = ioBuffer.get();

        byte versionNumber = (byte) ((value >> 1) & 0x1f);

        boolean currentNextIndicator = (value & 1) != 0;

        byte sectionNumber = ioBuffer.get();

        byte lastSectionNumber = ioBuffer.get();

        return new PSI(tableId, tableIdExtension, versionNumber, currentNextIndicator, sectionNumber, lastSectionNumber);
    }

    private int tableId;

    private int tableIdExtension;

    private byte versionNumber;

    private boolean currentNextIndicator;

    private byte sectionNumber;

    private byte lastSectionNumber;

    public PSI(PSI psi) {
        this(psi.tableId, psi.tableIdExtension, psi.versionNumber, psi.currentNextIndicator, psi.sectionNumber, psi.lastSectionNumber);
    }

    public PSI(int tableId, int tableIdExtension, byte versionNumber, boolean currentNextIndicator, byte sectionNumber, byte lastSectionNumber) {
        this.tableId = tableId;

        this.tableIdExtension = tableIdExtension;

        this.versionNumber = versionNumber;

        this.currentNextIndicator = currentNextIndicator;

        this.sectionNumber = sectionNumber;

        this.lastSectionNumber = lastSectionNumber;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public int getTableIdExtension() {
        return tableIdExtension;
    }

    public void setTableIdExtension(int tableIdExtension) {
        this.tableIdExtension = tableIdExtension;
    }

    public byte getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(byte versionNumber) {
        this.versionNumber = versionNumber;
    }

    public boolean isCurrentNextIndicator() {
        return currentNextIndicator;
    }

    public void setCurrentNextIndicator(boolean currentNextIndicator) {
        this.currentNextIndicator = currentNextIndicator;
    }

    public byte getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(byte sectionNumber) {
        this.sectionNumber = sectionNumber;
    }

    public byte getLastSectionNumber() {
        return lastSectionNumber;
    }

    public void setLastSectionNumber(byte lastSectionNumber) {
        this.lastSectionNumber = lastSectionNumber;
    }
}