package ru.lipetsk.camera.cmagent.core.management;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by Ivan on 23.03.2016.
 */
public class WebServer {
    private final static Logger logger = LoggerFactory.getLogger(WebServer.class);

    private static WebServer instance = null;

    public synchronized static WebServer getInstance() {
        if (instance == null) {
            instance = new WebServer();
        }

        return instance;
    }

    private HttpServer httpServer;

    private WebServer() {
        try {
            this.httpServer = HttpServer.create();

            this.httpServer.setExecutor(Executors.newCachedThreadPool());
        } catch (IOException e) {
            logger.error(e.getMessage());

            System.exit(-1);
        }
    }

    public void bind(InetSocketAddress inetSocketAddress, int backlog) throws IOException {
        this.httpServer.bind(inetSocketAddress, backlog);

        System.setProperty("agent.port", String.valueOf(this.httpServer.getAddress().getPort()));

        logger.info("WebServer has bind [{}] successfully", this.httpServer.getAddress().getPort());
    }

    public void bind(InetSocketAddress inetSocketAddress) throws IOException {
        this.bind(inetSocketAddress, 0);
    }

    public void bind(int port, int backlog) throws IOException {
        this.bind(new InetSocketAddress(port), backlog);
    }

    public void bind(int port) throws IOException {
        this.bind(port, 0);
    }

    public void start() {
        this.httpServer.start();

        logger.info("WebServer has started successfully");
    }

    public void stop(int delay) {
        this.httpServer.stop(delay);

        logger.info("WebServer has stopped successfully");
    }

    public InetSocketAddress getAddress() { return this.httpServer.getAddress(); }

    public void createContext(String path, HttpHandler httpHandler) {
        this.httpServer.createContext(path, httpHandler);
    }

    public void removeContext(String path) {
        this.httpServer.removeContext(path);
    }
}