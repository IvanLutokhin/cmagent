package ru.lipetsk.camera.cmagent.net.sdp.field;

import ru.lipetsk.camera.cmagent.exception.SDPException;
import ru.lipetsk.camera.cmagent.exception.SDPParseException;

import java.util.regex.Pattern;

/**
 * Created by Ivan on 12.02.2016.
 */
public class Phone implements IField {
    private static final Pattern FIELD_PATTERN = Pattern.compile("[^\\r\\n\u0000]+");

    public static Phone parse(String field) throws SDPParseException {
        if (!field.startsWith("p=")) {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a phone field");
        }

        try {
            return new Phone(field.substring(2));
        } catch (SDPException e) {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a phone field", e);
        }
    }

    private String value;

    public Phone(String value) throws SDPException {
        this.setValue(value);
    }

    @Override
    public String getKey() {
        return "p";
    }

    public String getValue() {
        return this.value;
    }

    private void setValue(String value) throws SDPException {
        if (!FIELD_PATTERN.matcher(value).matches()) {
            throw new SDPException("Invalid phone: " + value);
        }

        this.value = value;
    }

    @Override
    public String toString() {
        return this.getKey() + "=" + this.value;
    }
}
