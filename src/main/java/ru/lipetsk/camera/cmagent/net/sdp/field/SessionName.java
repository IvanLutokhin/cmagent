package ru.lipetsk.camera.cmagent.net.sdp.field;

import ru.lipetsk.camera.cmagent.exception.SDPException;
import ru.lipetsk.camera.cmagent.exception.SDPParseException;

import java.util.regex.Pattern;

/**
 * Created by Ivan on 12.02.2016.
 */
public class SessionName implements IField {
    private static final Pattern FIELD_PATTERN = Pattern.compile("[^\\r\\n\u0000]+");

    public static SessionName parse(String field) throws SDPParseException {
        if (!field.startsWith("s=")) {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a session name field");
        }

        try {
            return new SessionName(field.substring(2));
        } catch (SDPException e) {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a session name field", e);
        }
    }

    private String value;

    public SessionName(String value) throws SDPException {
        this.setValue(value);
    }

    @Override
    public String getKey() {
        return "s";
    }

    public String getValue() {
        return this.value;
    }

    private void setValue(String value) throws SDPException {
        if (!FIELD_PATTERN.matcher(value).matches()) {
            throw new SDPException("Invalid session name:" + value);
        }

        this.value = value;
    }

    @Override
    public String toString() {
        return this.getKey() + "=" + this.value;
    }
}
