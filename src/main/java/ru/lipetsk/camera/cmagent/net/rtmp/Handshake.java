package ru.lipetsk.camera.cmagent.net.rtmp;

import org.apache.mina.core.buffer.IoBuffer;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Ivan on 16.02.2016.
 */
public class Handshake {
    private static final Random random = new Random();

    private byte[] c0;

    private byte[] c1;

    private byte[] c2;

    private byte[] s0;

    private byte[] s1;

    private byte[] s2;

    private IoBuffer buffer;

    private boolean complete;

    public Handshake() {
        this.c0 = new byte[] { RTMP.NON_ENCRYPTED };

        this.c1 = this.generateC1();

        this.c2 = new byte[RTMP.HANDSHAKE_SIZE];

        this.s0 = new byte[1];

        this.s1 = new byte[RTMP.HANDSHAKE_SIZE];

        this.s2 = new byte[RTMP.HANDSHAKE_SIZE];

        this.buffer = IoBuffer.allocate(RTMP.SERVER_HANDSHAKE_SIZE);

        this.complete = false;
    }

    public IoBuffer getC0() { return IoBuffer.wrap(this.c0); }

    public IoBuffer getC1() {
        return IoBuffer.wrap(this.c1);
    }

    public IoBuffer getC2() {
        System.arraycopy(this.s1, 0, this.c2, 0, 4);

        System.arraycopy(this.c1, 4 , this.c2, 4, 4);

        System.arraycopy(this.s1, 8, this.c2, 8, RTMP.HANDSHAKE_SIZE - 8);

        return IoBuffer.wrap(this.c2);
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean handle(IoBuffer ioBuffer) {
        this.buffer.put(ioBuffer);

        if (this.buffer.position() == RTMP.SERVER_HANDSHAKE_SIZE) {
            byte[] data = new byte[RTMP.SERVER_HANDSHAKE_SIZE];

            this.buffer.flip();

            this.buffer.get(data);

            System.arraycopy(data, 0, this.s0, 0, 1);

            System.arraycopy(data, 1, this.s1, 0, RTMP.HANDSHAKE_SIZE);

            System.arraycopy(data, 1 + RTMP.HANDSHAKE_SIZE, this.s2, 0, RTMP.HANDSHAKE_SIZE);

            return true;
        }

        return false;
    }

    public boolean validate() {
        return Arrays.equals(this.c1, this.s2);
    }

    private byte[] generateC1() {
        byte[] data = new byte[RTMP.HANDSHAKE_SIZE];

        int timestamp = (int) (System.currentTimeMillis() / 1000);

        // timestamp
        data[0] = (byte) (timestamp >>> 24);
        data[1] = (byte) (timestamp >>> 16);
        data[2] = (byte) (timestamp >>> 8);
        data[3] = (byte) timestamp;

        data[4] = 0;
        data[5] = 0;
        data[6] = 0;
        data[7] = 0;

        //fill the rest with random bytes
        byte[] randomBytes = new byte[RTMP.HANDSHAKE_SIZE - 8];

        random.nextBytes(randomBytes);

        System.arraycopy(randomBytes, 0, data, 8, (RTMP.HANDSHAKE_SIZE - 8));

        return data;
    }
}