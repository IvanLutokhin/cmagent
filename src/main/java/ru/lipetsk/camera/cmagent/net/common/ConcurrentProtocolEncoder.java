package ru.lipetsk.camera.cmagent.net.common;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.util.concurrent.Semaphore;

/**
 * Created by Ivan on 20.03.2016.
 */
public abstract class ConcurrentProtocolEncoder extends ProtocolEncoderAdapter {
    private Semaphore semaphore;

    public ConcurrentProtocolEncoder() {
        this.semaphore = new Semaphore(1, true);
    }

    @Override
    public void encode(IoSession ioSession, Object object, ProtocolEncoderOutput protocolEncoderOutput) throws Exception {
        try {
            this.semaphore.acquire();

            IoBuffer ioBuffer = (object instanceof IoBuffer) ? (IoBuffer) object : this.encodeObject(ioSession, object);

            if (ioBuffer != null) {
                protocolEncoderOutput.write(ioBuffer);
            }
        } finally {
            this.semaphore.release();
        }
    }

    protected abstract IoBuffer encodeObject(IoSession ioSession, Object object) throws Exception;
}