package ru.lipetsk.camera.cmagent.net.sdp.field;

import ru.lipetsk.camera.cmagent.exception.SDPException;
import ru.lipetsk.camera.cmagent.exception.SDPParseException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ivan on 13.02.2016.
 */
public class Key implements IField {
    public static final String BASE64 = "base64";

    public static final String CLEAR = "clear";

    public static final String PROMPT = "prompt";

    public static final String URI = "uri";

    private static final Pattern FIELD_PATTEN = Pattern.compile("([^:]+)(:((.+)))?");

    private static final Pattern METHOD_BASE64_PATTERN = Pattern.compile("([\\w\\+/]{4})*([\\w\\+/]{2}==|[\\w\\+/]{3}=)*");

    private static final Pattern METHOD_CLEAR_PATTERN = Pattern.compile("[\\w\'-\\./:?#\\$&\\*;=@\\[\\]\\^_`\\{\\}\\|\\+\\~ \\t]+");

    public static Key parse(String field) throws SDPParseException {
        if (!field.startsWith("k=")) {
            throw new SDPParseException("The string \"" + field + "\" isn\'t an key field");
        }

        Matcher matcher = FIELD_PATTEN.matcher(field.substring(2));

        if(matcher.matches()) {
            try {
                return new Key(matcher.group(1), matcher.group(3));
            } catch (SDPException e) {
                throw new SDPParseException("The string \"" + field + "\" isn\'t a valid key field", e);
            }
        } else {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a valid key field");
        }
    }

    private String method;

    private String encryptionKey;

    public Key(String method, String encryptionKey) throws SDPException {
        this.setMethod(method);

        this.setEncryptionKey(encryptionKey);
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) throws SDPException {
        if (method == null) {
            throw new SDPException("The encryption method cannot be null");
        } else if(!method.equals("base64") && !method.equals("clear") && !method.equals("prompt") && !method.equals("uri")) {
            throw new SDPException("The method " + method + " is not supported by SDP");
        } else {
            this.method = method;
        }
    }

    public String getEncryptionKey() {
        return this.encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) throws SDPException {
        boolean result = false;

        switch (this.method) {
            case "base64":
                result = METHOD_BASE64_PATTERN.matcher(encryptionKey).matches();
                break;
            case "clear":
                result = METHOD_CLEAR_PATTERN.matcher(encryptionKey).matches();
                break;
            case "prompt":
                result = encryptionKey == null || encryptionKey.length() == 0;

                this.encryptionKey = null;

                break;
            case "uri":
                try {
                    new URL(encryptionKey);
                } catch (MalformedURLException var4) {
                    result = false;
                }

                break;
        }

        if (!result) {
            throw new SDPException("Invalid key for method " + this.method);
        } else {
            this.encryptionKey = encryptionKey;
        }
    }

    @Override
    public String getKey() {
        return "k";
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.getKey() + "=");

        stringBuilder.append(this.method);

        if (this.encryptionKey != null) {
            stringBuilder.append(":").append(this.encryptionKey);
        }

        return stringBuilder.toString();
    }
}
