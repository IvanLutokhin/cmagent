package ru.lipetsk.camera.cmagent.core.proxy.vo;

/**
 * Created by Ivan on 23.03.2016.
 */
public class StreamVO {
    public enum State {
        UNKNOWN(-1),
        INIT(0),
        SETUP(1),
        RUNNING(200),
        STOPPED(404);

        private final int value;

        State(int value) {
            this.value = value;
        }

        public int getValue() { return this.value; }
    }

    private int id;

    private String enterpriseName;

    private String url;

    private int rtspPort;

    private boolean record;

    private boolean screenshot;

    private String fmsApp;

    private State state;

    public StreamVO() { }

    public StreamVO(int id, String enterpriseName, String url, int rtspPort, boolean record, boolean screenshot) {
        this.setId(id);

        this.setEnterpriseName(enterpriseName);

        this.setUrl(url);

        this.setRtspPort(rtspPort);

        this.setRecord(record);

        this.setScreenshot(screenshot);

        this.state = State.SETUP;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Argument 'id' can't be a negative");
        }

        this.id = id;
    }

    public String getEnterpriseName() {
        return this.enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getRtspPort() {
        return this.rtspPort;
    }

    public void setRtspPort(int rtspPort) {
        if (rtspPort < 1 || rtspPort > 65536) {
            throw new IllegalArgumentException("Illegal argument 'rtsp port' - " + rtspPort);
        }

        this.rtspPort = rtspPort;
    }

    public boolean isRecord() {
        return this.record;
    }

    public void setRecord(boolean record) {
        this.record = record;
    }

    public boolean isScreenshot() {
        return this.screenshot;
    }

    public void setScreenshot(boolean screenshot) {
        this.screenshot = screenshot;
    }

    public String getFmsApp() {
        return this.fmsApp;
    }

    public void setFmsApp(String fmsApp) {
        this.fmsApp = fmsApp;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }
}