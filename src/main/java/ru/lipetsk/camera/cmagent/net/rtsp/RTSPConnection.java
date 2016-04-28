package ru.lipetsk.camera.cmagent.net.rtsp;

import ru.lipetsk.camera.cmagent.net.common.BaseConnection;
import ru.lipetsk.camera.cmagent.net.rtsp.io.RTSPIoHandler;

/**
 * Created by Ivan on 30.03.2016.
 */
public class RTSPConnection extends BaseConnection {
    public RTSPConnection() {
        this.socketConnector.setHandler(new RTSPIoHandler(this));
    }
}
