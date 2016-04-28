package ru.lipetsk.camera.cmagent.net.sdp.field;

import ru.lipetsk.camera.cmagent.exception.SDPParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ivan on 12.02.2016.
 */
public class ConnectionData implements IField {
    private static final Pattern FIELD_PATTERN = Pattern.compile("(IN) (IP4|IP6) (([^ ]+))");

    public static ConnectionData parse(String field) throws SDPParseException {
        if (!field.startsWith("c=")) {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a connection data field");
        }

        Matcher matcher = FIELD_PATTERN.matcher(field.substring(2));

        if(matcher.matches()) {
            return new ConnectionData(matcher.group(1), matcher.group(2), matcher.group(3));
        } else {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a valid connection data field");
        }
    }

    private String netType;

    private String addressType;

    private String connectionAddress;

    public ConnectionData(String netType, String addressType, String connectionAddress) {
        this.netType = netType;

        this.addressType = addressType;

        this.connectionAddress = connectionAddress;
    }

    public String getNetType() {
        return this.netType;
    }

    public String getAddressType() {
        return this.addressType;
    }

    public String getConnectionAddress() {
        return this.connectionAddress;
    }

    @Override
    public String getKey() {
        return "c";
    }

    @Override
    public String toString() {
        return this.getKey() + "=" + this.netType + " " + this.addressType + " " + this.connectionAddress;
    }
}
