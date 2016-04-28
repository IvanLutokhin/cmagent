package ru.lipetsk.camera.cmagent.net.rtmp;

import ru.lipetsk.camera.cmagent.net.common.BaseConnection;
import ru.lipetsk.camera.cmagent.net.rtmp.io.RTMPIoHandler;

/**
 * Created by Ivan on 30.03.2016.
 */
public class RTMPConnection extends BaseConnection {
    public RTMPConnection() {
        this.socketConnector.setHandler(new RTMPIoHandler(this));
    }
}
