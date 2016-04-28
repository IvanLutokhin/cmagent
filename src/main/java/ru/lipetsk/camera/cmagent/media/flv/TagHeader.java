package ru.lipetsk.camera.cmagent.media.flv;

/**
 * Created by Ivan on 09.03.2016.
 */
public abstract class TagHeader {
    private int codecId;

    public TagHeader(int codecId) {
        this.codecId = codecId;
    }

    int getCodecId() {
        return this.codecId;
    }
}