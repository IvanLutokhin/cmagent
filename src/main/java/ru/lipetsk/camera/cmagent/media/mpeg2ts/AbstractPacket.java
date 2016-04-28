package ru.lipetsk.camera.cmagent.media.mpeg2ts;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Created by Ivan on 21.03.2016.
 */
public abstract class AbstractPacket {
    protected IoBuffer buffer;

    protected boolean dirty;

    public AbstractPacket() {
        this.dirty = false;
    }

    public AbstractPacket(IoBuffer ioBuffer) {
        this.buffer = ioBuffer;

        this.buffer.rewind();

        this.parse();

        this.buffer.rewind();

        this.setDirty(false);
    }

    public IoBuffer getBuffer() {
        return this.buffer;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    protected abstract void parse();

    protected abstract void write();
}