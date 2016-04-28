package ru.lipetsk.camera.cmagent.media.h264;

/**
 * Created by Ivan on 15.02.2016.
 */
public class FUHeader {
    private byte b;

    private byte s;
    
    private byte e;

    private byte r;

    private byte type;

    public FUHeader(byte b) {
        this.b = b;

        this.s = (byte)(b & 0x80);

        this.e = (byte)(b & 0x40);

        this.r = (byte)(b & 0x20);

        this.type = (byte)(b & 0x1f);
    }

    public byte getS() {
        return s;
    }

    public boolean isS() {
        return this.s != 0;
    }

    public void setS(byte s) {
        this.s = s;
    }

    public byte getE() {
        return e;
    }

    public boolean isE() {
        return this.e != 0;
    }

    public void setE(byte e) {
        this.e = e;
    }

    public byte getR() {
        return r;
    }

    public boolean isR() {
        return this.r != 0;
    }

    public void setR(byte r) {
        this.r = r;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte get() {
        return this.b;
    }
}
