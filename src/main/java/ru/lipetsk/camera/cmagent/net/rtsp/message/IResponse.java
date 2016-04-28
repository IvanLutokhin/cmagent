package ru.lipetsk.camera.cmagent.net.rtsp.message;

/**
 * Created by Ivan on 11.02.2016.
 */
public interface IResponse extends IMessage {
    void setLine(int statusCode, String reasonPhrase);

    int getStatusCode();

    String getReasonPhrase();
}
