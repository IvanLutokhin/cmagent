package ru.lipetsk.camera.cmagent.net.sdp.field;

import ru.lipetsk.camera.cmagent.exception.SDPParseException;

/**
 * Created by Ivan on 12.02.2016.
 */
public class Version implements IField {
    public static Version parse(String field) throws SDPParseException {
        if (!field.startsWith("v=")) {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a version field");
        }

        try {
            int version = Integer.parseInt(field.substring(2));

            if (version != 0) {
                throw new SDPParseException("Invalid SDP protocol version: the only allowed value is 0");
            }

            return new Version(version);
        } catch (NumberFormatException e) {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a valid version field", e);
        }
    }

    private int value;

    public Version() {
        this.value = 0;
    }

    public Version(int value) {
        this.value = value;
    }

    @Override
    public String getKey() {
        return "v";
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.getKey() + "=" + this.value;
    }
}
