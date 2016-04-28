package ru.lipetsk.camera.cmagent.exception;

/**
 * Created by Ivan on 11.02.2016.
 */
public class MissingHeaderException extends Exception {
    public MissingHeaderException(String headerKey) {
        super("Header '" + headerKey + "' is missing.");
    }
}
