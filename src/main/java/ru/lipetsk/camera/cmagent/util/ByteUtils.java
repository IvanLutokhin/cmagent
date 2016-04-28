package ru.lipetsk.camera.cmagent.util;

/**
 * Created by Ivan on 26.04.2016.
 */
public class ByteUtils {
    public static boolean startsWith(byte[] source, byte[] match) {
        return startsWith(source, 0, match);
    }

    public static boolean startsWith(byte[] source, int offset, byte[] match) {
        if (match.length > (source.length - offset)) {
            return false;
        }

        for (int i = 0; i < match.length; i++) {
            if (source[offset + i] != match[i]) {
                return false;
            }
        }

        return true;
    }

    public static boolean equals(byte[] source, byte[] match) {
        return match.length == source.length && startsWith(source, 0, match);
    }

    public static void getBytes(byte[] source, int srcBegin, int srcEnd, byte[] destination, int dstBegin) {
        System.arraycopy(source, srcBegin, destination, dstBegin, srcEnd - srcBegin);
    }
}