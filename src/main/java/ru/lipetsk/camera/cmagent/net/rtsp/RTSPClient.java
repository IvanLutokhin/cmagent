package ru.lipetsk.camera.cmagent.net.rtsp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lipetsk.camera.cmagent.event.*;
import ru.lipetsk.camera.cmagent.exception.SDPParseException;
import ru.lipetsk.camera.cmagent.net.common.BaseConnection;
import ru.lipetsk.camera.cmagent.net.rtcp.RTCPPacket;
import ru.lipetsk.camera.cmagent.net.rtp.RTPPacket;
import ru.lipetsk.camera.cmagent.net.rtsp.message.*;
import ru.lipetsk.camera.cmagent.net.sdp.MediaDescription;
import ru.lipetsk.camera.cmagent.net.sdp.SessionDescription;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Ivan on 11.02.2016.
 */
public class RTSPClient extends EventDispatcher implements IRTSPClient, IEventListener {
    private final static Logger logger = LoggerFactory.getLogger(RTSPClient.class);

    private String uri;

    private Credentials credentials;

    private AtomicInteger CSeq;

    private String session;

    private String authenticate;

    private Map<String, IRequest> requestMap;

    private KeepAliveThread keepAliveThread;

    private BaseConnection baseConnection;

    public RTSPClient() {
        this.CSeq = new AtomicInteger(0);

        this.requestMap = new ConcurrentHashMap<>();

        this.keepAliveThread = new KeepAliveThread(this);

        this.baseConnection = new RTSPConnection();

        this.baseConnection.addEventListener(Events.CONNECTION_OPENED, this);

        this.baseConnection.addEventListener(Events.CONNECTION_CLOSED, this);

        this.baseConnection.addEventListener(Events.MESSAGE_RECEIVED, this);

        this.baseConnection.addEventListener(Events.MESSAGE_SENT, this);
    }

    @Override
    public String getURI() {
        return this.uri;
    }

    @Override
    public int getCSeq() {
        return this.CSeq.get();
    }

    @Override
    public String getSession() {
        return this.session;
    }

    @Override
    public boolean connect(String host, int port, String path, Credentials credentials) {
        this.uri = String.format("rtsp://%s:%s/%s", host, port, path);

        this.credentials = credentials;

        if (port == -1) {
            port = RTSP.DEFAULT_PORT;
        }

        return this.baseConnection.connect(host, port);
    }

    @Override
    public boolean isConnected() {
        return this.baseConnection.isConnected();
    }

    @Override
    public void disconnect() {
        this.CSeq.set(0);

        this.session = null;

        this.authenticate = null;

        this.requestMap.clear();

        this.baseConnection.disconnect();
    }

    public synchronized void options() {
        IRequest request = RTSP.request(RTSP.Method.OPTIONS, this.uri, this.CSeq.incrementAndGet());

        this.requestMap.put(request.getHeader("CSeq").getValue(), request);

        if (this.authenticate != null) {
            request.addHeader(new Header("Authorization", this.authenticate));
        }

        this.baseConnection.send(request);
    }

    public synchronized void describe() {
        IRequest request = RTSP.request(RTSP.Method.DESCRIBE, this.uri, this.CSeq.incrementAndGet(), new Header("Accept", "application/sdp"));

        this.requestMap.put(request.getHeader("CSeq").getValue(), request);

        if (this.authenticate != null) {
            request.addHeader(new Header("Authorization", this.authenticate));
        }

        this.baseConnection.send(request);
    }

    public synchronized void setup(String track, int channel, int controlChannel) {
        String path = String.format("%s/%s", this.uri, track);

        String transport = String.format("RTP/AVP/TCP;unicast;interleaved=%s-%s", channel, controlChannel);

        IRequest request = RTSP.request(RTSP.Method.SETUP, path, this.CSeq.incrementAndGet(), new Header("Transport", transport));

        this.requestMap.put(request.getHeader("CSeq").getValue(), request);

        if (this.authenticate != null) {
            request.addHeader(new Header("Authorization", this.authenticate));
        }

        if (this.session != null) {
            request.addHeader(new Header("Session", this.session));
        }

        this.baseConnection.send(request);
    }

    public synchronized void play() {
        if (this.session != null) {
            IRequest request = RTSP.request(RTSP.Method.PLAY, this.uri, this.CSeq.incrementAndGet(), new Header("Session", this.session));

            this.requestMap.put(request.getHeader("CSeq").getValue(), request);

            if (this.authenticate != null) {
                request.addHeader(new Header("Authorization", this.authenticate));
            }

            this.baseConnection.send(request);

            this.keepAliveThread.start();
        } else {
            this.options();
        }
    }

    public synchronized void pause() {
        if (this.session != null) {
            IRequest request = RTSP.request(RTSP.Method.PAUSE, this.uri, this.CSeq.incrementAndGet(), new Header("Session", this.session));

            this.requestMap.put(request.getHeader("CSeq").getValue(), request);

            if (this.authenticate != null) {
                request.addHeader(new Header("Authorization", this.authenticate));
            }

            this.baseConnection.send(request);
        }
    }

    public synchronized void teardown() {
        if (this.session != null) {
            IRequest request = RTSP.request(RTSP.Method.TEARDOWN, this.uri, this.CSeq.incrementAndGet(), new Header("Session", this.session));

            this.requestMap.put(request.getHeader("CSeq").getValue(), request);

            if (this.authenticate != null) {
                request.addHeader(new Header("Authorization", this.authenticate));
            }

            this.baseConnection.send(request);
        }

        this.keepAliveThread.interrupt();
    }

    public synchronized void getParameter() {
        if (this.session != null) {
            IRequest request = RTSP.request(RTSP.Method.GET_PARAMETER, this.uri, this.CSeq.incrementAndGet(), new Header("Session", this.session));

            this.requestMap.put(request.getHeader("CSeq").getValue(), request);

            if (this.authenticate != null) {
                request.addHeader(new Header("Authorization", this.authenticate));
            }

            this.baseConnection.send(request);
        }
    }

    @Override
    public void onEventHandle(IEvent event) {
        Object eventContext = event.getContext();

        switch (event.getName()) {
            case Events.CONNECTION_CLOSED:
                this.disconnect();

                break;
            case Events.MESSAGE_RECEIVED:
                if (eventContext instanceof Response) {
                    this.onRtspResponseHandle((IResponse) eventContext);
                } else if (eventContext instanceof RTPPacket) {
                    this.dispatchEvent(new Event(Events.RTP_PACKET, eventContext));
                } else if (eventContext instanceof RTCPPacket) {
                    this.dispatchEvent(new Event(Events.RTCP_PACKET, eventContext));
                }

                break;
            case Events.MESSAGE_SENT:
                if (eventContext instanceof Request) {
                    Request request = (Request) eventContext;

                    logger.debug(request.getMethod() + RTSP.CRLF + request);
                }

                break;
        }
    }

    private void onRtspResponseHandle(IResponse response) {
        logger.debug(response.getReasonPhrase() + RTSP.CRLF + response);

        if (!response.containHeader("CSeq")) {
            return;
        }

        String CSeq = response.getHeader("CSeq").getValue();

        IRequest request = this.requestMap.get(CSeq);

        if (request != null) {
            this.onRtspRequestHandle(request, response);
        }
    }

    private void onRtspRequestHandle(IRequest request, IResponse response) {
        if (this.isConnectionClose(request) || this.isConnectionClose(response)) {
            this.disconnect();
        }

        switch (request.getMethod()) {
            case OPTIONS:
                if (response.getStatusCode() == 200) {
                    this.describe();
                } else if (response.getStatusCode() == 401 && this.credentials != null) {
                    String authenticate = response.getHeader("WWW-Authenticate").getValue();

                    this.authenticate = Authenticate.execute(request, authenticate, this.credentials);

                    if (this.authenticate == null) {
                        return;
                    }

                    this.options();
                } else {
                    this.disconnect();
                }

                break;
            case DESCRIBE:
                if (response.getStatusCode() == 200 && response.getBody() != null) {
                    try {
                        SessionDescription sessionDescription = SessionDescription.parse(new String(response.getBody()));

                        int i = 0;
                        for (MediaDescription mediaDescription : sessionDescription.getMediaDescriptions()) {
                            if (mediaDescription.getMedia().getMedia().equals("video") && mediaDescription.hasAttribute("control")) {
                                this.setup(mediaDescription.getAttribute("control").getValue(), i * 2, i * 2 + 1);

                                i++;
                            }
                        }
                    } catch (SDPParseException e) {
                        logger.warn(e.getMessage());
                    }
                } else if (response.getStatusCode() == 401 && this.credentials != null) {
                    String authenticate = response.getHeader("WWW-Authenticate").getValue();

                    this.authenticate = Authenticate.execute(request, authenticate, this.credentials);

                    if (this.authenticate == null) {
                        return;
                    }

                    this.describe();
                } else {
                    this.disconnect();
                }

                break;
            case SETUP:
                if (response.getStatusCode() == 200 && response.containHeader("Session")) {
                    String session = response.getHeader("Session").getValue();

                    int index = session.indexOf(";");

                    session = index > 0 ? session.substring(0, index) : session;

                    this.session = session;

                    this.play();
                }

                break;
            case TEARDOWN:
                if (response.getStatusCode() == 200) {
                    this.session = null;
                }

                break;
        }
    }

    private boolean isConnectionClose(IMessage message) {
        return message.containHeader("Connection") && message.getHeader("Connection").getValue().equalsIgnoreCase("close");
    }
}