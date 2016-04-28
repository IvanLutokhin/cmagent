package ru.lipetsk.camera.cmagent.media.flv;

/**
 * Created by Ivan on 09.03.2016.
 */
public class VideoTagHeader extends TagHeader {
    private int frameType;

    public VideoTagHeader(int codecId, int frameType) {
        super(codecId);

        this.frameType = frameType;
    }

    public int getFrameType() {
        return frameType;
    }
}