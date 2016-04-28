package ru.lipetsk.camera.cmagent.media.mpeg2ts;

import org.apache.mina.core.buffer.IoBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ivan on 21.03.2016.
 */
public class PAT extends PSI {
    public static PAT parse(IoBuffer ioBuffer) {
        PSI psi = PSI.parse(ioBuffer);

        if (psi == null) {
            return null;
        }

        List<Integer> pidList = new ArrayList<>();

        Map<Integer, Integer> programMap = new HashMap<>();

        while (ioBuffer.remaining() > 4) {
            int programNum = ioBuffer.getShort();

            int pid = ioBuffer.getShort() & 0x1fff;

            if (programNum == 0) {
                pidList.add(pid);
            } else {
                programMap.put(programNum, pid);
            }
        }

        return new PAT(psi, pidList.toArray(new Integer[pidList.size()]), programMap);
    }

    private Integer[] pidList;

    private Map<Integer, Integer> programMap;

    public PAT(PSI psi, Integer[] pidList, Map<Integer, Integer> programMap) {
        super(psi);

        this.pidList = pidList;

        this.programMap = programMap;
    }

    public Integer[] getPidList() {
        return this.pidList;
    }

    public Map<Integer, Integer> getProgramMap() {
        return this.programMap;
    }
}