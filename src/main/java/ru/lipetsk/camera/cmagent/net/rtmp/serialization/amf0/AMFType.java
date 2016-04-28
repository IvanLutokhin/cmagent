package ru.lipetsk.camera.cmagent.net.rtmp.serialization.amf0;

import java.util.Map;

/**
 * Created by Ivan on 14.03.2016.
 */
public enum AMFType {
    NUMBER(0),
    BOOLEAN(1),
    STRING(2),
    OBJECT(3),
    NULL(5),
    UNDEFINED(6),
    MAP(8),
    ARRAY(10),
    DATE(11),
    LONG_STRING(12),
    UNSUPPORTED(13);

    public static AMFType parse(Object value) {
        if (value == null) {
            return NULL;
        }

        if ((value instanceof String)) {
            return STRING;
        }

        if ((value instanceof Number)) {
            return NUMBER;
        }

        if ((value instanceof Boolean)) {
            return BOOLEAN;
        }

        if ((value instanceof AMFObject)) {
            return OBJECT;
        }

        if ((value instanceof Map)) {
            return MAP;
        }

        if ((value instanceof Object[])) {
            return ARRAY;
        }

        throw new IllegalArgumentException("Illegal type: " + value.getClass());
    }

    public static AMFType valueOf(int value) {
        for (AMFType amfType : AMFType.values()) {
            if (amfType.getValue() == value) {
                return amfType;
            }
        }

        return null;
    }

    private final int value;

    AMFType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
