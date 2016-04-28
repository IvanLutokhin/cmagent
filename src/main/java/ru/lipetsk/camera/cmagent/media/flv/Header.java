package ru.lipetsk.camera.cmagent.media.flv;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Created by Ivan on 09.03.2016.
 */
public class Header {
    private final static byte[] signature = "FLV".getBytes();

    private final static byte version = 0x01;

    private boolean flagVideo;

    private boolean flagAudio;

    private int dataOffset;

    public static byte[] getSignature() {
        return signature;
    }

    public static byte getVersion() {
        return version;
    }

    public boolean isFlagVideo() {
        return flagVideo;
    }

    public void setFlagVideo(boolean flagVideo) {
        this.flagVideo = flagVideo;
    }

    public boolean isFlagAudio() {
        return flagAudio;
    }

    public void setFlagAudio(boolean flagAudio) {
        this.flagAudio = flagAudio;
    }

    public int getDataOffset() {
        return dataOffset;
    }

    public void setDataOffset(int dataOffset) {
        this.dataOffset = dataOffset;
    }

    public IoBuffer get() {
        IoBuffer ioBuffer = IoBuffer.allocate(1);

        ioBuffer.setAutoExpand(true);

        ioBuffer.put(signature);

        ioBuffer.put(version);

        ioBuffer.put((byte) (4 * (this.flagAudio ? 1 : 0) + (this.flagVideo ? 1 : 0)));

        ioBuffer.putInt(9);

        ioBuffer.flip();

        return ioBuffer;
    }
}
