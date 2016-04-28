package ru.lipetsk.camera.cmagent.media;

/**
 * Created by Ivan on 24.03.2016.
 */
public interface IVideoFrame {
    int getTimestamp();

    int getSize();

    byte[] getPayload();
}