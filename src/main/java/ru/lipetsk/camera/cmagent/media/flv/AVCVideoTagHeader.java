package ru.lipetsk.camera.cmagent.media.flv;

/**
 * Created by Ivan on 09.03.2016.
 */
public class AVCVideoTagHeader extends VideoTagHeader {
    private int avcPacketType;

    private int compositionTime;

    public AVCVideoTagHeader(int codecId, int frameType, int avcPacketType, int compositionTime) {
        super(codecId, frameType);

        this.avcPacketType = avcPacketType;

        this.compositionTime = compositionTime;
    }

    public int getAVCPacketType() {
        return avcPacketType;
    }

    public int getCompositionTime() {
        return compositionTime;
    }
}
