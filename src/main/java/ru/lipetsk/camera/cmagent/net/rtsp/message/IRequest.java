package ru.lipetsk.camera.cmagent.net.rtsp.message;

import ru.lipetsk.camera.cmagent.net.rtsp.RTSP;

/**
 * Created by Ivan on 11.02.2016.
 */
public interface IRequest extends IMessage {
    void setLine(RTSP.Method method, String uri);

    String getURI();

    RTSP.Method getMethod();
}
