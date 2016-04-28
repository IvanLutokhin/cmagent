package ru.lipetsk.camera.cmagent.net.sdp.field;

import ru.lipetsk.camera.cmagent.exception.SDPException;
import ru.lipetsk.camera.cmagent.exception.SDPParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ivan on 13.02.2016.
 */
public class Attribute implements IField {
    private static final Pattern FIELD_PATTERN = Pattern.compile("([^:]+)(:((.+)))?");

    private static final Pattern NAME_PATTERN = Pattern.compile("(\\w)+(-\\w+)*");

    private static final Pattern VALUE_PATTERN = Pattern.compile("[^\u0000\\r\\n]+");

    public static Attribute parse(String field) throws SDPParseException {
        if (!field.startsWith("a=")) {
            throw new SDPParseException("The string \"" + field + "\" isn\'t an attribute field");
        }

        Matcher matcher = FIELD_PATTERN.matcher(field.substring(2));

        if(matcher.matches()) {
            try {
                return (matcher.group(3) != null) ? new Attribute(matcher.group(1), matcher.group(3)) :  new Attribute(matcher.group(1));
            } catch (SDPException e) {
                throw new SDPParseException("The string \"" + field + "\" isn\'t a valid attribute field", e);
            }
        } else {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a valid attribute field");
        }
    }

    private String name;

    private String value;

    public Attribute(String name) throws SDPException {
        this.setName(name);

        this.value = null;
    }

    public Attribute(String name, String value) throws SDPException {
        this.setName(name);

        this.setValue(value);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) throws SDPException {
        if (name == null) {
            throw new SDPException("The attribute name cannot be null");
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            throw new SDPException("Invalid attribute name: " + name);
        } else {
            this.name = name;
        }
    }

    public String getValue() {
        return this.value;
    }

    public boolean hasValue() {
        return this.value != null;
    }

    public void setValue(String value) throws SDPException {
        if (value != null && !VALUE_PATTERN.matcher(value).matches()) {
            throw new SDPException("Invalid attribute value: " + value);
        }

        this.value = value;
    }

    @Override
    public String getKey() {
        return "a";
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.getKey() + "=");

        stringBuilder.append(this.name);

        if (this.hasValue()) {
            stringBuilder.append(":" + this.value);
        }

        return stringBuilder.toString();
    }
}
