package ru.lipetsk.camera.cmagent.net.rtmp.io;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.lipetsk.camera.cmagent.event.Event;
import ru.lipetsk.camera.cmagent.event.Events;
import ru.lipetsk.camera.cmagent.event.IEventDispatcher;
import ru.lipetsk.camera.cmagent.net.rtmp.codec.RTMPProtocolCodecFactory;
import ru.lipetsk.camera.cmagent.net.rtmp.Handshake;
import ru.lipetsk.camera.cmagent.net.rtmp.RTMP;

/**
 * Created by Ivan on 15.02.2016.
 */
public class RTMPIoHandler extends IoHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RTMPIoHandler.class);

    private final IEventDispatcher eventDispatcher;

    private Handshake handshake;

    public RTMPIoHandler(IEventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;

        this.handshake = new Handshake();
    }

    public void sessionCreated(IoSession session) throws Exception {
        session.setAttribute("rtmp.chunk_size.read", RTMP.DEFAULT_CHUNK_SIZE);

        session.setAttribute("rtmp.chunk_size.write", RTMP.DEFAULT_CHUNK_SIZE);
    }

    public void sessionOpened(IoSession session) throws Exception {
        // Start handshake | send C0
        session.write(this.handshake.getC0());

        // Handshake send C1
        session.write(this.handshake.getC1());
    }

    public void sessionClosed(IoSession session) throws Exception {
        this.eventDispatcher.dispatchEvent(new Event(Events.CONNECTION_CLOSED));
    }

    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        logger.error(cause.getMessage(), cause);
    }

    public void messageReceived(IoSession session, Object message) throws Exception {
        logger.debug("Processing a MESSAGE_RECEIVED for session {}", session.getId());

        if (!this.handshake.isComplete() && (message instanceof IoBuffer)) {
            this.handshakeReceived(session, (IoBuffer) message);

            return;
        }

        this.eventDispatcher.dispatchEvent(new Event(Events.MESSAGE_RECEIVED, message));
    }

    public void handshakeReceived(IoSession session, IoBuffer ioBuffer) {
        if (this.handshake.handle(ioBuffer)) {
            // Handshake send C2
            session.write(this.handshake.getC2());

            // validate
            if (this.handshake.validate()) {
                this.handshake.setComplete(true);

                session.getFilterChain().addLast("codec", new ProtocolCodecFilter(new RTMPProtocolCodecFactory()));

                this.eventDispatcher.dispatchEvent(new Event(Events.CONNECTION_OPENED));
            } else {
                session.close(false);
            }
        }
    }

    public void messageSent(IoSession session, Object message) throws Exception {
        logger.debug("Processing a MESSAGE_SENT for session {}", session.getId());

        if (this.handshake.isComplete()) {
            this.eventDispatcher.dispatchEvent(new Event(Events.MESSAGE_SENT, message));
        }
    }
}