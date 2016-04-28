package ru.lipetsk.camera.cmagent.net.rtsp.message;

import ru.lipetsk.camera.cmagent.net.rtsp.RTSP;

/**
 * Created by Ivan on 11.02.2016.
 */
public class Response extends Message implements IResponse {
    private int statusCode;

    private String reasonPhrase;

    public Response(String line) {
        line = line.substring(line.indexOf(" ") + 1);

        this.setLine(Integer.parseInt(line.substring(0, line.indexOf(' '))), line.substring(line.indexOf(' ') + 1));
    }

    @Override
    public void setLine(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;

        this.reasonPhrase = reasonPhrase;

        this.line = RTSP.TOKEN_VERSION + " " + this.statusCode + " " + this.reasonPhrase;
    }

    @Override
    public int getStatusCode() {
        return this.statusCode;
    }

    @Override
    public String getReasonPhrase() {
        return this.reasonPhrase;
    }
}
