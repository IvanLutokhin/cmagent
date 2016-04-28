package ru.lipetsk.camera.cmagent.net.rtmp.serialization.amf0;

import java.util.Map;

/**
 * Created by Ivan on 14.03.2016.
 */
public class AMF0 {
    public static final byte[] OBJECT_END_MARKER = { 0x00, 0x00, 0x09 };

    public static AMFObject amfObject(AMFObject object, Map<String, Object> pairs) {
        if (object != null && pairs != null) {
            for (Map.Entry<String, Object> pair : pairs.entrySet()) {
                object.put(pair.getKey(), pair.getValue());
            }
        }

        return object;
    }

    public static AMFObject amfObject(Map<String, Object> pairs) {
        return amfObject(new AMFObject(), pairs);
    }
}