package ru.lipetsk.camera.cmagent.net.sdp.field;

import ru.lipetsk.camera.cmagent.exception.SDPException;
import ru.lipetsk.camera.cmagent.exception.SDPParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ivan on 12.02.2016.
 */
public class Origin implements IField {
    private static final Pattern FIELD_PATTERN = Pattern.compile("([^ ]+) (\\d+) (\\d+) (IN) (IP4|IP6) (([^ ]+))");

    private static final Pattern USERNAME_PATTERN = Pattern.compile("[\\w\'-\\./:?#\\$&\\*;=@\\[\\]\\^_`\\{\\}\\|\\+\\~]+");

    public static Origin parse(String field) throws SDPParseException {
        if (!field.startsWith("o=")) {
            throw new SDPParseException("The string \"" + field + "\" isn\'t an origin field");
        }

        Matcher matcher = FIELD_PATTERN.matcher(field.substring(2));

        if(matcher.matches()) {
            try {
                return new Origin(matcher.group(1), Long.parseLong(matcher.group(2)), Long.parseLong(matcher.group(3)), matcher.group(4), matcher.group(5), matcher.group(6));
            } catch (SDPException e) {
                throw new SDPParseException("The string \"" + field + "\" isn\'t a valid origin field", e);
            }
        } else {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a valid origin field");
        }
    }

    private String username;

    private long sessionId;

    private long sessionVersion;

    private String netType;

    private String addressType;

    private String unicastAddress;

    public Origin(String username, long sessionId, long sessionVersion, String netType, String addressType, String unicastAddress) throws SDPException {
        this.setUsername(username);

        this.setSessionId(sessionId);

        this.setSessionVersion(sessionVersion);

        this.netType = netType;

        this.addressType = addressType;

        this.unicastAddress = unicastAddress;
    }

    public String getUsername() {
        return this.username;
    }

    private void setUsername(String username) throws SDPException {
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new SDPException("Invalid username: " + username);
        }

        this.username = username;
    }

    public long getSessionId() {
        return this.sessionId;
    }

    private void setSessionId(long sessionId) throws SDPException {
        if (sessionId < 0L) {
            throw new SDPException("Session id cannot be a negative");
        }

        this.sessionId = sessionId;
    }

    public long getSessionVersion() {
        return this.sessionVersion;
    }

    private void setSessionVersion(long sessionVersion) throws SDPException {
        if (sessionVersion < 0L) {
            throw new SDPException("Session version cannot be a negative");
        }

        this.sessionVersion = sessionVersion;
    }

    public String getNetType() {
        return this.netType;
    }

    public String getAddressType() {
        return this.addressType;
    }

    public String getUnicastAddress() {
        return this.unicastAddress;
    }

    @Override
    public String getKey() {
        return "o";
    }

    @Override
    public String toString() {
        return this.getKey() + "=" + this.username + " " + this.sessionId + " " + this.sessionVersion + " " + this.netType + " " + this.addressType + " " + this.unicastAddress;
    }
}
