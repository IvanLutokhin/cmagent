package ru.lipetsk.camera.cmagent.net.rtsp.message;

import ru.lipetsk.camera.cmagent.net.rtsp.RTSP;

/**
 * Created by Ivan on 11.02.2016.
 */
public class Request extends Message implements IRequest {
    private RTSP.Method method;

    private String uri;

    public Request() { }

    public Request(String line) {
        String[] parts = line.split(" ");

        this.setLine(RTSP.Method.valueOf(parts[0]), parts[1]);
    }

    @Override
    public void setLine(RTSP.Method method, String uri) {
        this.method = method;

        this.uri = uri;

        this.line = this.method.toString() + " " + this.uri + " " + RTSP.TOKEN_VERSION;
    }

    @Override
    public String getURI() {
        return this.uri;
    }

    @Override
    public RTSP.Method getMethod() {
        return this.method;
    }
}
