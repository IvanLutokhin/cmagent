package ru.lipetsk.camera.cmagent.net.sdp.field;

import ru.lipetsk.camera.cmagent.exception.SDPParseException;
import ru.lipetsk.camera.cmagent.exception.SDPException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ivan on 12.02.2016.
 */
public class Bandwidth implements IField {
    private static final Pattern FIELD_PATTERN = Pattern.compile("(.+):((\\d+))");

    public static Bandwidth parse(String field) throws SDPParseException {
        if (!field.startsWith("b=")) {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a bandwidth field");
        }

        Matcher matcher = FIELD_PATTERN.matcher(field.substring(2));

        if(matcher.matches()) {
            try {
                return new Bandwidth(matcher.group(1), Integer.parseInt(matcher.group(2)));
            } catch (SDPException e) {
                throw new SDPParseException("The string \"" + field + "\" isn\'t a valid bandwidth field", e);
            }
        } else {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a valid bandwdith field");
        }
    }

    private String type;

    private int value;

    private Bandwidth(String type, int value) throws SDPException {
        this.setType(type);

        this.setValue(value);
    }

    @Override
    public String getKey() {
        return "b";
    }

    public String getType() {
        return this.type;
    }

    private void setType(String type) throws SDPException {
        if(!type.equals("AS") && !type.equals("CT") && !type.equals("RR") && !type.equals("RS")) {
            throw new SDPException("Unknown type: " + type);
        }

        this.type = type;
    }

    public int getValue() {
        return this.value;
    }

    private void setValue(int value) throws SDPException {
        if(value < 0) {
            throw new SDPException("Invalid bandwidth value: " + value);
        }

        this.value = value;
    }

    @Override
    public String toString() {
        return this.getKey() + "=" + this.type + ":" + this.value;
    }
}
