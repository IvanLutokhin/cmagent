package ru.lipetsk.camera.cmagent.net.sdp.field;

import ru.lipetsk.camera.cmagent.exception.SDPException;
import ru.lipetsk.camera.cmagent.exception.SDPParseException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Ivan on 12.02.2016.
 */
public class URI implements IField {
    public static URI parse(String field) throws SDPParseException {
        if (!field.startsWith("u=")) {
            throw new SDPParseException("The string \"" + field + "\" isn\'t an URI field");
        }

        try {
            return new URI(field.substring(2));
        } catch (SDPException e) {
            throw new SDPParseException("The string \"" + field + "\" isn\'t an URI field", e);
        }
    }

    private URL value;

    public URI(String value) throws SDPException {
        this.setValue(value);
    }

    @Override
    public String getKey() {
        return "u";
    }

    public URL getValue() {
        return this.value;
    }

    private void setValue(String value) throws SDPException {
        try {
            this.value = new URL(value);
        } catch (MalformedURLException e) {
            throw new SDPException("Invalid URI: " + value);
        }
    }

    @Override
    public String toString() {
        return this.getKey() + "=" + this.value.toString();
    }
}
