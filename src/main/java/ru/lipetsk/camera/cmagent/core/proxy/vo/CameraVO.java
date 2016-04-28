package ru.lipetsk.camera.cmagent.core.proxy.vo;

import java.util.regex.Pattern;

/**
 * Created by Ivan on 23.03.2016.
 */
public class CameraVO {
    public enum State {
        UNKNOWN(-1),
        AVAILABLE(200),
        NOT_AVAILABLE(404);

        private final int value;

        State(int value) {
            this.value = value;
        }

        public int getValue() { return this.value; }
    }

    private final static String IP_ADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    private int id;

    private String ipAddress;

    private String rtspLogin;

    private String rtspPassword;

    private String httpLogin;

    private String httpPassword;

    private State state;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Argument 'id' can't be a negative");
        }

        this.id = id;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        if (!Pattern.compile(IP_ADDRESS_PATTERN).matcher(ipAddress).matches()) {
            throw new IllegalArgumentException("Illegal argument 'ip_address' - " + ipAddress);
        }

        this.ipAddress = ipAddress;
    }

    public String getRtspLogin() {
        return this.rtspLogin;
    }

    public void setRtspLogin(String rtspLogin) {
        this.rtspLogin = rtspLogin;
    }

    public String getRtspPassword() {
        return this.rtspPassword;
    }

    public void setRtspPassword(String rtspPassword) {
        this.rtspPassword = rtspPassword;
    }

    public String getHttpLogin() {
        return this.httpLogin;
    }

    public void setHttpLogin(String httpLogin) {
        this.httpLogin = httpLogin;
    }

    public String getHttpPassword() {
        return this.httpPassword;
    }

    public void setHttpPassword(String httpPassword) {
        this.httpPassword = httpPassword;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }
}