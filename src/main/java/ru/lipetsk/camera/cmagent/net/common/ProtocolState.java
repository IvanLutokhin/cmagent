package ru.lipetsk.camera.cmagent.net.common;

/**
 * Created by Ivan on 17.02.2016.
 */
public class ProtocolState {
    public enum State {
        OK,
        CONTINUE,
        BUFFER
    }

    private int bufferCount;

    private State state;

    public ProtocolState() {
        this.bufferCount = 0;

        this.state = State.OK;
    }

    public void restore() {
        this.bufferCount = 0;

        this.state = State.OK;
    }

    public void buffer(int bufferCount) {
        this.bufferCount = bufferCount;

        this.state = State.BUFFER;
    }

    public void continueDecoding() {
        this.state = State.CONTINUE;
    }

    public boolean isDecoding() {
        return this.state == State.CONTINUE;
    }

    public boolean canDecode(int remaining) {
        return remaining >= this.bufferCount;
    }

    public boolean canContinue() {
        return this.state != State.BUFFER;
    }

    public boolean hasDecodeObject() {
        return this.state == State.OK;
    }
}