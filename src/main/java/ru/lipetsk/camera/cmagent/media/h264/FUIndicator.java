package ru.lipetsk.camera.cmagent.media.h264;

/**
 * Created by Ivan on 15.02.2016.
 */
public class FUIndicator {
    private byte b;

    private byte f;

    private byte nri;

    private byte type;

    public FUIndicator(byte b) {
        this.b = b;

        this.f = (byte) (b & 0x80);

        this.nri = (byte) ((b >>> 5) & 0x3);

        this.type = (byte) (b & 0x1f);
    }

    public byte getF() {
        return f;
    }

    public void setF(byte f) {
        this.f = f;
    }

    public byte getNRI() {
        return nri;
    }

    public void setNRI(byte nri) {
        this.nri = nri;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte get() {
        return b;
    }
}
