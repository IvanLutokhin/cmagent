package ru.lipetsk.camera.cmagent.exception;

/**
 * Created by Ivan on 12.02.2016.
 */
public class SDPException extends Exception {
    public SDPException(String message) {
        super(message);
    }

    public SDPException(String message, Throwable cause) {
        super(message, cause);
    }
}
