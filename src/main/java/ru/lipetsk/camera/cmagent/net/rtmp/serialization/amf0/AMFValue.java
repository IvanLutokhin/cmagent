package ru.lipetsk.camera.cmagent.net.rtmp.serialization.amf0;

import org.apache.mina.core.buffer.IoBuffer;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ivan on 18.02.2016.
 */
public class AMFValue {
    public static void encode(IoBuffer ioBuffer, Object value) {
        AMFType amfType = AMFType.parse(value);

        ioBuffer.put((byte) amfType.getValue());

        switch (amfType) {
            case NUMBER:
                if (value instanceof Integer) {
                    ioBuffer.putDouble(((Integer) value).doubleValue());
                } else {
                    ioBuffer.putDouble((Double) value);
                }

                return;
            case BOOLEAN:
                ioBuffer.put((byte) ((Boolean) value ? 1 : 0));

                return;
            case STRING:
                encodeString(ioBuffer, (String) value);

                return;
            case NULL:
                return;
            case MAP:
                ioBuffer.putInt(0);
            case OBJECT:
                for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
                    encodeString(ioBuffer, entry.getKey());

                    encode(ioBuffer, entry.getValue());
                }

                ioBuffer.put(AMF0.OBJECT_END_MARKER);

                return;
            case ARRAY:
                Object[] objects = (Object[]) value;

                ioBuffer.putInt(objects.length);

                for (Object object : objects) {
                    encode(ioBuffer, object);
                }

                return;
            default:
                throw new IllegalArgumentException("Illegal type: " + amfType);
        }
    }

    public static void encode(IoBuffer ioBuffer, Object... values)
    {
        for (Object value : values) {
            encode(ioBuffer, value);
        }
    }

    public static Object decode(IoBuffer ioBuffer) {
        AMFType amfType = AMFType.valueOf(ioBuffer.get());

        if (amfType == null) {
            return null;
        }

        switch (amfType) {
            case NUMBER:
                return Double.longBitsToDouble(ioBuffer.getLong());
            case BOOLEAN:
                return ioBuffer.get() == 1;
            case STRING:
                return decodeString(ioBuffer);
            case ARRAY:
                int arraySize = ioBuffer.getInt();

                Object[] array = new Object[arraySize];

                for (int i = 0; i < arraySize; i++) {
                    array[i] = decode(ioBuffer);
                }

                return array;
            case MAP:
                ioBuffer.getInt();
            case OBJECT:
                Map<String, Object> map = (amfType == AMFType.MAP) ? new HashMap<>() : new AMFObject();

                byte[] objectEndMarker = new byte[3];

                while (true) {
                    ioBuffer.mark();

                    ioBuffer.get(objectEndMarker);

                    if (Arrays.equals(objectEndMarker, AMF0.OBJECT_END_MARKER)) {
                        ioBuffer.reset();

                        ioBuffer.skip(3);

                        break;
                    }

                    ioBuffer.reset();

                    map.put(decodeString(ioBuffer), decode(ioBuffer));
                }

                return map;
            case DATE:
                Double dateValue = ioBuffer.getDouble();

                ioBuffer.getShort();

                return new Date(dateValue.longValue());
            case LONG_STRING:
                int stringSize = ioBuffer.getInt();

                byte[] bytes = new byte[stringSize];

                ioBuffer.get(bytes);

                return new String(bytes);
            case NULL:
            case UNDEFINED:
            case UNSUPPORTED:
                return null;
            default:
                throw new IllegalArgumentException("Illegal type: " + amfType);
        }
    }

    private static void encodeString(IoBuffer ioBuffer, String value) {
        byte[] bytes = value.getBytes();

        ioBuffer.putShort((short) bytes.length);

        ioBuffer.put(bytes);
    }

    private static String decodeString(IoBuffer ioBuffer) {
        short size = ioBuffer.getShort();

        byte[] bytes = new byte[size];

        ioBuffer.get(bytes);

        return new String(bytes);
    }

    public static String toString(AMFType amfType, Object value)
    {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append('[').append(amfType).append(" ");

        if (amfType == AMFType.ARRAY) {
            stringBuilder.append(Arrays.toString((Object[]) value));
        } else {
            stringBuilder.append(value);
        }

        stringBuilder.append(']');

        return stringBuilder.toString();
    }
}
