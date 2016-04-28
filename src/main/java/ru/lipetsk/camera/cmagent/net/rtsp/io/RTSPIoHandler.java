package ru.lipetsk.camera.cmagent.net.rtsp.io;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lipetsk.camera.cmagent.event.Event;
import ru.lipetsk.camera.cmagent.event.Events;
import ru.lipetsk.camera.cmagent.event.IEventDispatcher;
import ru.lipetsk.camera.cmagent.net.rtsp.codec.RTSPProtocolCodecFactory;

/**
 * Created by Ivan on 24.03.2016.
 */
public class RTSPIoHandler extends IoHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RTSPIoHandler.class);

    private final IEventDispatcher eventDispatcher;

    public RTSPIoHandler(IEventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    public void sessionOpened(IoSession session) throws Exception {
        session.getFilterChain().addLast("codec", new ProtocolCodecFilter(new RTSPProtocolCodecFactory()));

        this.eventDispatcher.dispatchEvent(new Event(Events.CONNECTION_OPENED));
    }

    public void sessionClosed(IoSession session) throws Exception {
        this.eventDispatcher.dispatchEvent(new Event(Events.CONNECTION_CLOSED));
    }

    public void messageReceived(IoSession session, Object message) throws Exception {
        this.eventDispatcher.dispatchEvent(new Event(Events.MESSAGE_RECEIVED, message));
    }

    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        logger.error(cause.getMessage(), cause);
    }

    public void messageSent(IoSession session, Object message) throws Exception {
        this.eventDispatcher.dispatchEvent(new Event(Events.MESSAGE_SENT, message));
    }
}