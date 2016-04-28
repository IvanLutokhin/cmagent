package ru.lipetsk.camera.cmagent.media.mpeg2ts;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Created by Ivan on 21.03.2016.
 */
public class PCR {
    public static PCR parse(IoBuffer ioBuffer) {
        byte[] data = new byte[6];

        ioBuffer.get(data);

        long bits =((data[0] & 0xffL) << 40) | ((data[1] & 0xffL) << 32) | ((data[2] & 0xffL) << 24) | ((data[3] & 0xffL) << 16) | ((data[4] & 0xffL) << 8) | (data[5] & 0xffL);

        long base = (bits & 0xFFFFFFFF8000L) >> 15;

        byte reserved = (byte) ((bits & 0x7E00) >> 9);

        int extension = (int) (bits & 0x1FFL);

        return new PCR(null, base, extension, reserved);
    }

    private AdaptationField adaptationField;

    private long base;

    private int extension;

    private byte reserved;

    public PCR(AdaptationField adaptationField, long base, int extension, byte reserved) {
        this.adaptationField = adaptationField;

        this.base = base;

        this.extension = extension;

        this.reserved = reserved;
    }

    public long getValue() {
        return this.base * 300 + this.extension;
    }

    public void setValue(long value) {
        this.base = value / 300;

        this.extension = (int) (value % 300);

        this.adaptationField.setDirty(true);
    }

    public void write(IoBuffer ioBuffer) {
        ioBuffer.putInt((int) ((this.base & 0x1FFFFFFFFL) >> 1));

        int value = 0;
        value |= ((this.base & 0x1) << 7);
        value |= ((this.reserved & 0x3F) << 1);
        value |= ((this.extension & 0x1FF) >> 8);

        ioBuffer.put((byte) value);

        ioBuffer.put((byte) (this.extension & 0xff));
    }
}
