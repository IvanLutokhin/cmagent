package ru.lipetsk.camera.cmagent.net.rtsp.message;

import ru.lipetsk.camera.cmagent.exception.MissingHeaderException;

/**
 * Created by Ivan on 11.02.2016.
 */
public interface IMessage {
    String getLine();

    Header[] getHeaders();

    Header getHeader(String key);

    void addHeader(Header header);

    boolean containHeader(String key);

    byte[] getBody();

    void setBody(byte[] body);

    byte[] getBytes() throws MissingHeaderException;
}