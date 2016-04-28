package ru.lipetsk.camera.cmagent.net.sdp.field;

import ru.lipetsk.camera.cmagent.exception.SDPException;
import ru.lipetsk.camera.cmagent.exception.SDPParseException;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ivan on 12.02.2016.
 */
public class Time implements IField {
    private static final long NTP_CONSTANT = 2208988800L;

    private static final Pattern FIELD_PATTERN = Pattern.compile("(([1-9](\\d){0,9})|0) (([1-9](\\d){0,9})|0)");

    public static Time parse(String field) throws SDPParseException {
        if (!field.startsWith("t=")) {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a session name field");
        }

        Matcher matcher = FIELD_PATTERN.matcher(field.substring(2));

        if(matcher.matches()) {
            try {
                return new Time(Long.parseLong(matcher.group(1)), Long.parseLong(matcher.group(4)));
            } catch (SDPException e) {
                throw new SDPParseException("The string \"" + field + "\" isn\'t a valid time field", e);
            }
        } else {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a valid time field");
        }
    }

    private static Date getDateFromNTP(long ntpTime) {
        return new Date((ntpTime - NTP_CONSTANT) * 1000L);
    }

    private long startTime;

    private long stopTime;

    public Time() {
        this.startTime = 0L;

        this.stopTime = 0L;
    }

    public Time(long startTime, long stopTime) throws SDPException {
        this.setStartTime(startTime);

        this.setStopTime(stopTime);
    }

    @Override
    public String getKey() {
        return "t";
    }

    public Date getStartTime() {
        return getDateFromNTP(this.startTime);
    }

    public void setStartTime(long startTime) throws SDPException {
        if(startTime < 0L) {
            throw new SDPException("The session start time cannot be negative");
        }

        this.startTime = startTime;
    }

    public Date getStopTime() {
        return getDateFromNTP(this.stopTime);
    }

    public void setStopTime(long stopTime) throws SDPException {
        if(stopTime < 0L) {
            throw new SDPException("The session stop time cannot be negative");
        }

        this.stopTime = stopTime;
    }

    @Override
    public String toString() {
        return this.getKey() + "=" + this.startTime + " " + this.stopTime;
    }
}
