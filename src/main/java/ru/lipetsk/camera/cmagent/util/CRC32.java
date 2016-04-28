package ru.lipetsk.camera.cmagent.util;

/**
 * Created by Ivan on 24.03.2016.
 */
public class CRC32 {
    public static final int POLY = 0x04C11DB7;

    private static int[] table = new int[256];

    static {
        for (int i = 0; i < 256; i++) {
            int value = i << 24;

            for (int j = 0; j < 8; j++) {
                value = (value << 1) ^ (((value & 0x80000000) != 0) ? POLY : 0);
            }

            table[i] = value;
        }
    }

    public static byte[] compute(byte[] data, int offset, int length) {
        int crc32 = 0xffffffff;

        for (int i = offset; i < offset + length; i++) {
            crc32 = (crc32 << 8) ^ table[((crc32 >> 24) ^ data[i]) & 0xFF];
        }

        byte[] result = new byte[4];

        result[0] = (byte) (crc32 >>> 24);
        result[1] = (byte) (crc32 >>> 16);
        result[2] = (byte) (crc32 >>> 8);
        result[3] = (byte) (crc32 & 0xff);

        return result;
    }
}