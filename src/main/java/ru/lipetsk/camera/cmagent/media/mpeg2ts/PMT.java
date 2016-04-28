package ru.lipetsk.camera.cmagent.media.mpeg2ts;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Created by Ivan on 21.03.2016.
 */
public class PMT extends PSI {
    public static PMT parse(IoBuffer ioBuffer) {
        PSI psi = PSI.parse(ioBuffer);

        if (psi == null) {
            return null;
        }

        int pcrPID = ioBuffer.getShort() & 0x1fff;

        return new PMT(psi, pcrPID);
    }

    private int pcrPID;

    public PMT(PSI psi, int pcrPID) {
        super(psi);

        this.pcrPID = pcrPID;
    }

    public int getPcrPID() {
        return pcrPID;
    }
}