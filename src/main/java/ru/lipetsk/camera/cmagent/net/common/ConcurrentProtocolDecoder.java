package ru.lipetsk.camera.cmagent.net.common;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import java.util.concurrent.Semaphore;

/**
 * Created by Ivan on 19.03.2016.
 */
public abstract class ConcurrentProtocolDecoder extends ProtocolDecoderAdapter {
    protected ProtocolState protocolState;

    private Semaphore semaphore;

    public ConcurrentProtocolDecoder() {
        this.protocolState = new ProtocolState();

        this.semaphore = new Semaphore(1, true);
    }

    @Override
    public void decode(IoSession ioSession, IoBuffer ioBuffer, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
        IoBuffer cumulativeBuffer = (IoBuffer) ioSession.getAttribute("buffer.decoder");

        if (cumulativeBuffer == null) {
            cumulativeBuffer = IoBuffer.allocate(ioBuffer.remaining());

            cumulativeBuffer.setAutoExpand(true);

            ioSession.setAttribute("buffer.decoder", cumulativeBuffer);
        } else {
            cumulativeBuffer.compact();
        }

        cumulativeBuffer.put(ioBuffer);

        cumulativeBuffer.flip();

        try {
            this.semaphore.acquire();

            while (cumulativeBuffer.hasRemaining()) {
                int remaining = cumulativeBuffer.remaining();

                if (!this.protocolState.canDecode(remaining)) {
                    break;
                }

                this.protocolState.restore();

                Object object = this.decodeObject(ioSession, cumulativeBuffer);

                if (object != null) {
                    protocolDecoderOutput.write(object);
                }
            }
        } finally {
            this.semaphore.release();
        }
    }

    protected abstract Object decodeObject(IoSession ioSession, IoBuffer ioBuffer) throws Exception;
}