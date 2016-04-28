package ru.lipetsk.camera.cmagent.net.rtsp.message;

import ru.lipetsk.camera.cmagent.exception.MissingHeaderException;
import ru.lipetsk.camera.cmagent.net.rtsp.RTSP;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ivan on 11.02.2016.
 */
public abstract class Message implements IMessage {
    protected String line;

    private Map<String, Header> headerMap = new HashMap<>();

    private byte[] body;

    @Override
    public String getLine() {
        return this.line;
    }

    @Override
    public Header[] getHeaders() {
        return this.headerMap.values().toArray(new Header[this.headerMap.values().size()]);
    }

    @Override
    public Header getHeader(String key) {
        return this.headerMap.get(key);
    }

    @Override
    public void addHeader(Header header) {
        this.headerMap.put(header.getKey(), header);
    }

    @Override
    public boolean containHeader(String key) {
        return this.headerMap.containsKey(key);
    }

    @Override
    public byte[] getBody() {
        return this.body;
    }

    @Override
    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public byte[] getBytes() throws MissingHeaderException {
        if (!this.containHeader("User-Agent")) {
            throw new MissingHeaderException("User-Agent");
        }

        if (!this.containHeader("CSeq")) {
            throw new MissingHeaderException("CSeq");
        }

        return this.toString().getBytes();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(this.getLine()).append(RTSP.CRLF);

        for (Header header : this.headerMap.values()) {
            stringBuilder.append(header).append(RTSP.CRLF);
        }

        stringBuilder.append(RTSP.CRLF);

        if (this.body != null) {
            stringBuilder.append(new String(this.body));
        }

        return stringBuilder.toString();
    }
}