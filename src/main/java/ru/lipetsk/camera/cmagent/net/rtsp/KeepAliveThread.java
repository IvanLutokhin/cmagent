package ru.lipetsk.camera.cmagent.net.rtsp;

/**
 * Created by Ivan on 01.04.2016.
 */
public class KeepAliveThread extends Thread {
    private final static int TIMEOUT = 5000;

    private final RTSPClient rtspClient;

    public KeepAliveThread(RTSPClient rtspClient) {
        this.rtspClient = rtspClient;
    }

    public void run() {
        while (this.rtspClient.isConnected()) {
            try {
                Thread.sleep(TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.rtspClient.getParameter();
        }
    }
}