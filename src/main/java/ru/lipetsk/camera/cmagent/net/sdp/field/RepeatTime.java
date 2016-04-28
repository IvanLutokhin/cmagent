package ru.lipetsk.camera.cmagent.net.sdp.field;

import ru.lipetsk.camera.cmagent.exception.SDPException;
import ru.lipetsk.camera.cmagent.exception.SDPParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan on 12.02.2016.
 */
public class RepeatTime implements IField {
    public static RepeatTime parse(String field) throws SDPParseException {
        if (!field.startsWith("r=")) {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a repeat time field");
        }

        String[] values = field.substring(2).split(" ");

        try {
            switch(values.length) {
                case 0:
                case 1:
                case 2:
                    throw new SDPParseException("The string \"" + field + "\" isn\'t a valid repeat time field");
                default:
                    long repeatInterval = Long.parseLong(values[0]);

                    long activeDuration = Long.parseLong(values[1]);

                    long[] offsets = new long[values.length - 2];

                    for(int i = 0; i < offsets.length; ++i) {
                        offsets[i] = Long.parseLong(values[i + 2]);
                    }

                    return new RepeatTime(repeatInterval, activeDuration, offsets);
            }
        } catch (SDPException e) {
            throw new SDPParseException("The string \"" + field + "\" isn\'t a valid repeat time field", e);
        }
    }

    private long repeatInterval;

    private long activeDuration;

    private List<Long> offsets;

    public RepeatTime(long repeatInterval, long activeDuration, long offset) throws SDPException {
        this.offsets = new ArrayList<>();

        this.setRepeatInterval(repeatInterval);

        this.setActiveDuration(activeDuration);

        this.addOffset(offset);
    }

    public RepeatTime(long repeatInterval, long activeDuration, long[] offset) throws SDPException {
        this.offsets = new ArrayList<>();

        this.setRepeatInterval(repeatInterval);

        this.setActiveDuration(activeDuration);

        this.setOffset(offset);
    }

    @Override
    public String getKey() {
        return "r";
    }

    public long getRepeatInterval() {
        return this.repeatInterval;
    }

    public void setRepeatInterval(long repeatInterval) throws SDPException {
        if (repeatInterval < 0L) {
            throw new SDPException("The repeat interval cannot be negative");
        }

        this.repeatInterval = repeatInterval;
    }


    public long getActiveDuration() {
        return this.activeDuration;
    }

    public void setActiveDuration(long activeDuration) throws SDPException {
        if (activeDuration < 0L) {
            throw new SDPException("The active duration cannot be negative");
        }

        this.activeDuration = activeDuration;
    }

    public long[] getOffsets() {
        long[] values = new long[this.offsets.size()];

        int i = 0;
        for (Long offset : this.offsets) {
            values[i++] = offset;
        }

        return values;
    }

    public void setOffset(long[] offsets) throws SDPException {
        for (long offset : offsets) {
            this.addOffset(offset);
        }
    }

    public void addOffset(long offset) throws SDPException {
        if (offset < 0L) {
            throw new SDPException("Offsets cannot be a negative");
        }

        this.offsets.add(offset);
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.getKey() + "=");

        stringBuilder.append(this.repeatInterval).append(" ");

        stringBuilder.append(this.activeDuration);

        for (Long offset : this.offsets) {
            stringBuilder.append(" ").append(offset);
        }

        return stringBuilder.toString();
    }
}
