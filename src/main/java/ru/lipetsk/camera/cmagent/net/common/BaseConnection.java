package ru.lipetsk.camera.cmagent.net.common;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lipetsk.camera.cmagent.event.EventDispatcher;

import java.net.InetSocketAddress;

/**
 * Created by Ivan on 30.03.2016.
 */
public class BaseConnection extends EventDispatcher {
    private final static Logger logger = LoggerFactory.getLogger(BaseConnection.class);

    private final static int WORKER_TIMEOUT = 5000;

    protected SocketConnector socketConnector;

    protected volatile ConnectFuture connectFuture;

    public BaseConnection() {
        this.socketConnector = new NioSocketConnector();

        this.socketConnector.getSessionConfig().setTcpNoDelay(true);

        this.socketConnector.setConnectTimeoutMillis(WORKER_TIMEOUT);
    }

    public boolean connect(String host, int port) {
        this.connectFuture = this.socketConnector.connect(new InetSocketAddress(host, port));

        this.connectFuture.addListener(
                ioFuture -> {
                    try {
                        ioFuture.getSession();
                    } catch (Throwable cause) {
                        logger.error(cause.getMessage(), cause);
                    }
                }
        );

        this.connectFuture.awaitUninterruptibly(WORKER_TIMEOUT);

        return this.connectFuture.isConnected();
    }

    public boolean isConnected() {
        return this.socketConnector.isActive();
    }

    public void disconnect() {
        this.connectFuture.getSession().close(false);

        this.connectFuture.awaitUninterruptibly(WORKER_TIMEOUT);

        this.connectFuture = null;

        this.socketConnector.dispose();
    }

    public void send(Object object) {
        if (this.isConnected()) {
            this.connectFuture.getSession().write(object);
        }
    }
}