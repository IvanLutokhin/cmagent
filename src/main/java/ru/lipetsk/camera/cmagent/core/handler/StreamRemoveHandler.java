package ru.lipetsk.camera.cmagent.core.handler;

import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lipetsk.camera.cmagent.core.proxy.StreamProxy;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Ivan on 24.03.2016.
 */
public class StreamRemoveHandler extends ContextHandler {
    private final static Logger logger = LoggerFactory.getLogger(StreamRemoveHandler.class);

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Map<String, String> postParameters = this.getPostParameters(httpExchange);

        if (postParameters == null) {
            logger.warn("Agent has received command for stream remove without parameters");

            this.sendResponse(httpExchange, 400);

            return;
        }

        if (!postParameters.containsKey("id")) {
            logger.warn("Agent has received command for stream remove without required argument 'id'");

            this.sendResponse(httpExchange, 400);

            return;
        }

        int id;

        try {
            id = Integer.parseInt(postParameters.get("id"));
        } catch (NumberFormatException e) {
            logger.warn("Agent has received command for stream remove with illegal argument 'id'");

            this.sendResponse(httpExchange, 400);

            return;
        }

        StreamProxy streamProxy = (StreamProxy) this.facade.retrieveProxy("StreamProxy");

        if (!streamProxy.hasStream(id)) {
            logger.warn("Stream [{}] could not be found", id);

            this.sendResponse(httpExchange, 404);

            return;
        }

        this.facade.unregisterTask("StreamMonitoringTask_" + id);

        streamProxy.removeStream(id);

        logger.info("Stream [{}] was removed successfully", id);

        this.sendResponse(httpExchange, 200);
    }
}