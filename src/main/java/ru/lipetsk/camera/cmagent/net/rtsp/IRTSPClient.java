package ru.lipetsk.camera.cmagent.net.rtsp;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Ivan on 12.02.2016.
 */
public interface IRTSPClient {
    String getURI();

    int getCSeq();

    String getSession();

    boolean connect(String host, int port, String path, Credentials credentials);

    boolean isConnected();

    void disconnect();
}