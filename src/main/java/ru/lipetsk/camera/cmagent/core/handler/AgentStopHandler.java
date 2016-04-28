package ru.lipetsk.camera.cmagent.core.handler;

import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by Ivan on 24.03.2016.
 */
public class AgentStopHandler extends ContextHandler {
    private final static Logger logger = LoggerFactory.getLogger(AgentStopHandler.class);

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        logger.info("Agent has received command at stop");

        this.sendResponse(httpExchange, 200);

        System.exit(0);
    }
}