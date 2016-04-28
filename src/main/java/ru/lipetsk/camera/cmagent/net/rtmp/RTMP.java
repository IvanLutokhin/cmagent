package ru.lipetsk.camera.cmagent.net.rtmp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ivan on 15.02.2016.
 */
public class RTMP {
    public static final int WORKER_TIMEOUT = 5000;

    /**
     * Size of initial handshake between client and server
     */
    public static final int HANDSHAKE_SIZE = 1536;

    public static final int SERVER_HANDSHAKE_SIZE = 2 * RTMP.HANDSHAKE_SIZE + 1;

    /**
     * Marker byte for standard or non-encrypted RTMP data.
     */
    public static final byte NON_ENCRYPTED = (byte) 0x03;

    public static final int DEFAULT_CHUNK_SIZE = 128;

    public static Map<String, Object> getDefaultConnectionArgs(String host, int port, String application) {
        Map<String, Object> defaultConnectionArgs = new HashMap<>();

        defaultConnectionArgs.put("app", application);
        defaultConnectionArgs.put("tcUrl", String.format("rtmp://%s:%s/%s", host, port, application));
        defaultConnectionArgs.put("type", "nonprivate");
        //defaultConnectionArgs.put("flashVer", "FMLE/3.0 (compatible; FMSc/1.0)");
        //defaultConnectionArgs.put("flashVer", "WIN 11,2,202,235");
        defaultConnectionArgs.put("flashVer", "CMAgent 1.0.0");
        defaultConnectionArgs.put("swfUrl", null);
        defaultConnectionArgs.put("objectEncoding", 0);

        return defaultConnectionArgs;
    }
}
