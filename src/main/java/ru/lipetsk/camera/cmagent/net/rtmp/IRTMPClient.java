package ru.lipetsk.camera.cmagent.net.rtmp;

import ru.lipetsk.camera.cmagent.net.rtmp.message.Message;

import java.util.Map;

/**
 * Created by Ivan on 16.02.2016.
 */
public interface IRTMPClient {
    int getStreamId();

    boolean connect(String host, int port, Map<String, Object> connectionArgs);

    boolean connect(String host, int port, String application);

    boolean isConnected();

    void disconnect();

    void publishStream(String streamName);

    void writeStreamData(Message message);
}