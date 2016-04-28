package ru.lipetsk.camera.cmagent.net.sdp.field;

import ru.lipetsk.camera.cmagent.exception.SDPException;
import ru.lipetsk.camera.cmagent.exception.SDPParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Ivan on 13.02.2016.
 */
public class Media implements IField {
    private static final Pattern MEDIA_PATTERN = Pattern.compile("\\w+");

    private static final Pattern PROTOCOL_PATTERN = Pattern.compile("[\\w/]+");

    public static Media parse(String field) throws SDPParseException {
        if (!field.startsWith("m=")) {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a media field");
        }

        Media m = null;

        String[] values = field.substring(2).split(" ");

        try {
            if(values.length < 4) {
                throw new SDPException("Some media field parameters are missing");
            }

            String parseException = values[1];

            String[] portInfoValues = parseException.split("/");

            if (portInfoValues.length == 1) {
                m = new Media(values[0], Integer.parseInt(values[1]), values[2], values[3]);
            } else if(portInfoValues.length == 2) {
                m = new Media(values[0], Integer.parseInt(portInfoValues[0]), Integer.parseInt(portInfoValues[1]), values[2], values[3]);
            }

            if (m != null) {
                try {
                    for (int i = 4; i < values.length; ++i) {
                        m.addMediaFormat(values[i]);
                    }
                } catch (SDPException e) {
                    throw new SDPParseException("The string \"" + field + "\" isn\'t a valid media field", e);
                }
            } else {
                throw new SDPParseException("The string \"" + field + "\" isn\'t a valid media field");
            }

            return m;
        } catch (SDPException e) {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a valid media field", e);
        }
    }


    private String media;

    private int port;

    private int portsCount;

    private String protocol;

    private List<String> formats;

    public Media(String media, int port, int portsCount, String protocol, String format) throws SDPException {
        this.formats = new ArrayList<>();

        this.setMedia(media);

        this.setPort(port);

        this.setPortsCount(portsCount);

        this.setProtocol(protocol);

        this.addMediaFormat(format);
    }

    public Media(String media, int port, String protocol, String format) throws SDPException {
        this(media, port, 1, protocol, format);
    }

    public String getMedia() {
        return this.media;
    }

    public void setMedia(String media) throws SDPException {
        if (!MEDIA_PATTERN.matcher(media).matches()) {
            throw new SDPException("Invalid media: " + media);
        }

        this.media = media;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) throws SDPException {
        if (port < 0) {
            throw new SDPException("The transport port cannot be a negative");
        }

        this.port = port;
    }

    public int getPortsCount() {
        return this.portsCount;
    }

    public void setPortsCount(int n) throws SDPException {
        if (n < 1) {
            throw new SDPException("The ports count must be greater than 0");
        }

        this.portsCount = n;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(String protocol) throws SDPException {
        if (!PROTOCOL_PATTERN.matcher(protocol).matches()) {
            throw new SDPException("Invalid protocol: " + protocol);
        }

        this.protocol = protocol;
    }

    public String[] getMediaFormats() {
        return this.formats.toArray(new String[this.formats.size()]);
    }

    public void addMediaFormat(String format) throws SDPException {
        if (!MEDIA_PATTERN.matcher(format).matches()) {
            throw new SDPException("Invalid media format: " + format);
        }

        this.formats.add(format);
    }

    public void setMediaFormats(String[] formats) throws SDPException {
        if (formats == null) {
            throw new IllegalArgumentException("The media formats cannot be null");
        }

        this.formats.clear();

        for (String format : formats) {
            this.addMediaFormat(format);
        }
    }

    @Override
    public String getKey() {
        return "m";
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.getKey() + "=");

        stringBuilder.append(this.media);

        stringBuilder.append(" ").append(this.port);

        if (this.portsCount > 1) {
            stringBuilder.append("/").append(this.portsCount);
        }

        stringBuilder.append(" ").append(this.protocol);

        for (String format : this.formats) {
            stringBuilder.append(" ").append(format);
        }

        return stringBuilder.toString();
    }
}
