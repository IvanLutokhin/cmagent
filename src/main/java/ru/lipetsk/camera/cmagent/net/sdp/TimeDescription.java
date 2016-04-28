package ru.lipetsk.camera.cmagent.net.sdp;

import ru.lipetsk.camera.cmagent.net.sdp.field.RepeatTime;
import ru.lipetsk.camera.cmagent.net.sdp.field.Time;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan on 12.02.2016.
 */
public class TimeDescription {
    private Time time;

    private List<RepeatTime> repeatTimes;

    public TimeDescription() {
        this(new Time());
    }

    public TimeDescription(Time time) {
        this.setTime(time);

        this.repeatTimes = new ArrayList<>();
    }

    public Time getTime() {
        return this.time;
    }

    public void setTime(Time time) throws IllegalArgumentException {
        if (time == null) {
            throw new IllegalArgumentException("The time field cannot be null");
        }

        this.time = time;
    }

    public RepeatTime[] getRepeatTimes() {
        return this.repeatTimes.toArray(new RepeatTime[this.repeatTimes.size()]);
    }

    public void setRepeatTimes(RepeatTime[] fields) throws IllegalArgumentException {
        if (fields == null) {
            throw new IllegalArgumentException("Repeat time fields cannot be null");
        }

        this.clearRepeatTimes();

        for (RepeatTime field : fields) {
            this.addRepeatTime(field);
        }
    }

    public void addRepeatTime(RepeatTime field) throws IllegalArgumentException {
        if (field == null) {
            throw new IllegalArgumentException("A repeat time field cannot be null");
        }

        this.repeatTimes.add(field);
    }

    public void clearRepeatTimes() {
        this.repeatTimes.clear();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(this.time.toString());

        stringBuilder.append("\r\n");

        for (RepeatTime repeatTime : this.repeatTimes) {
            stringBuilder.append(repeatTime).append("\r\n");
        }

        return stringBuilder.toString();
    }
}
