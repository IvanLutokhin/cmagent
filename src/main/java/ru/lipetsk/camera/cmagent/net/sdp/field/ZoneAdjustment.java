package ru.lipetsk.camera.cmagent.net.sdp.field;

import ru.lipetsk.camera.cmagent.exception.SDPException;

/**
 * Created by Ivan on 13.02.2016.
 */
public class ZoneAdjustment {
    private long adjustmentTime;

    private long offset;

    public ZoneAdjustment(long adjustmentTime, long offset) throws SDPException {
        this.setAdjustmentTime(adjustmentTime);

        this.setOffset(offset);
    }

    public long getAdjustmentTime() {
        return this.adjustmentTime;
    }

    public void setAdjustmentTime(long adjustmentTime) throws SDPException {
        if (adjustmentTime < 0L) {
            throw new SDPException("Adjustment time cannot be a negative");
        }

        this.adjustmentTime = adjustmentTime;
    }

    public long getOffset() {
        return this.offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public String toString() {
        return this.adjustmentTime + " " + this.offset;
    }
}
