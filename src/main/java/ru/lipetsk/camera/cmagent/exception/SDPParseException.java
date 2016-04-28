package ru.lipetsk.camera.cmagent.exception;

/**
 * Created by Ivan on 12.02.2016.
 */
public class SDPParseException extends Exception {
    public SDPParseException(String message) {
        super(message);
    }

    public SDPParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
