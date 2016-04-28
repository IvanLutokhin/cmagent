package ru.lipetsk.camera.cmagent.net.rtsp;

import ru.lipetsk.camera.cmagent.net.rtsp.message.Header;
import ru.lipetsk.camera.cmagent.net.rtsp.message.IRequest;
import ru.lipetsk.camera.cmagent.net.rtsp.message.Request;

/**
 * Created by Ivan on 11.02.2016.
 */
public class RTSP {
    public static final int WORKER_TIMEOUT = 5000;

    public static final int DEFAULT_PORT = 554;

    public static final int CR = 13;

    public static final int LF = 10;

    public static final String CRLF = "\r\n";

    public static final String USER_AGENT = "CMAgent";

    public static final String TOKEN = "RTSP";

    public static final String VERSION = "1.0";

    public static final String TOKEN_VERSION = TOKEN + "/" + VERSION;

    public enum Method
    {
        DESCRIBE,
        OPTIONS,
        PLAY,
        PAUSE,
        RECORD,
        REDIRECT,
        SETUP,
        ANNOUNCE,
        GET_PARAMETER,
        SET_PARAMETER,
        TEARDOWN
    }

    public static IRequest request(RTSP.Method method, String uri, int CSeq, Header... headers) {
        IRequest request = new Request();

        request.setLine(method, uri);

        request.addHeader(new Header("User-Agent", RTSP.USER_AGENT));

        request.addHeader(new Header("CSeq", String.valueOf(CSeq)));

        for (Header header : headers) {
            request.addHeader(header);
        }

        return request;
    }
}
