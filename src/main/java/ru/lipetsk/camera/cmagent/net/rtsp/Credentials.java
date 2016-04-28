package ru.lipetsk.camera.cmagent.net.rtsp;

/**
 * Created by Ivan on 28.03.2016.
 */
public class Credentials {
    private String username;

    private String password;

    public Credentials(String username, String password) {
        this.username = username;

        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}