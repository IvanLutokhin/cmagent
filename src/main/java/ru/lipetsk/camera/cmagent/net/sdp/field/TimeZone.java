package ru.lipetsk.camera.cmagent.net.sdp.field;

import ru.lipetsk.camera.cmagent.exception.SDPParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan on 13.02.2016.
 */
public class TimeZone implements IField {
    public static TimeZone parse(String field) throws SDPParseException {
        if (!field.startsWith("z=")) {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a timezone field");
        }

        String[] values = field.substring(2).split(" ");

        if (values.length >= 2 && values.length % 2 == 0) {
            TimeZone timeZone = new TimeZone();

            for (int i = 0; i < values.length; i += 2) {
                try {
                    timeZone.addZoneAdjustment(new ZoneAdjustment(Long.parseLong(values[i]), Long.parseLong(values[i + 1])));
                } catch (Exception e) {
                    throw new SDPParseException("The string \"" + field + "\" isn\'t a valid timezone field", e);
                }
            }

            return timeZone;
        } else {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a valid timezone field");
        }
    }

    private List<ZoneAdjustment> zoneAdjustments;

    public TimeZone() {
        this.zoneAdjustments = new ArrayList<>();
    }

    public void addZoneAdjustment(ZoneAdjustment adjustment) {
        this.zoneAdjustments.add(adjustment);
    }

    @Override
    public String getKey() {
        return "z";
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.getKey() + "=");

        for (ZoneAdjustment zoneAdjustment : this.zoneAdjustments) {
            stringBuilder.append(zoneAdjustment.getAdjustmentTime()).append(" ").append(zoneAdjustment.getOffset()).append(" ");
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        return stringBuilder.toString();
    }
}
