package ru.lipetsk.camera.cmagent.core.handler;

import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lipetsk.camera.cmagent.core.proxy.ConfigurationProxy;
import ru.lipetsk.camera.cmagent.core.proxy.StreamProxy;
import ru.lipetsk.camera.cmagent.core.proxy.vo.StreamVO;
import ru.lipetsk.camera.cmagent.core.task.StreamMonitoringTask;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * Created by Ivan on 24.03.2016.
 */
public class StreamSetupHandler extends ContextHandler {
    private final static Logger logger = LoggerFactory.getLogger(StreamSetupHandler.class);

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Map<String, String> postParameters = this.getPostParameters(httpExchange);

        if (postParameters == null) {
            logger.warn("Agent has received command for stream setup without parameters");

            this.sendResponse(httpExchange, 400);

            return;
        }

        ConfigurationProxy configurationProxy = (ConfigurationProxy) this.facade.retrieveProxy("ConfigurationProxy");

        if (!postParameters.containsKey("camera_id")) {
            logger.warn("Agent has received command for stream setup without required argument 'camera_id'");

            this.sendResponse(httpExchange, 400);

            return;
        }

        int cameraId;

        try {
            cameraId = Integer.parseInt(postParameters.get("camera_id"));
        } catch (NumberFormatException e) {
            logger.warn("Agent has received command for stream setup with illegal argument 'id'");

            this.sendResponse(httpExchange, 400);

            return;
        }

        if (cameraId != configurationProxy.getAgentId()) {
            logger.warn("Agent has received command for stream setup with illegal argument 'id'");

            this.sendResponse(httpExchange, 403);

            return;
        }

        if (!postParameters.containsKey("id")) {
            logger.warn("Agent has received command for stream setup without required argument 'id'");

            this.sendResponse(httpExchange, 400);

            return;
        }

        String id = postParameters.get("id");

        if (!postParameters.containsKey("enterprise_name")) {
            logger.warn("Agent has received command for camera setup without required argument 'enterprise_name'");

            this.sendResponse(httpExchange, 400);

            return;
        }

        String enterpriseName = postParameters.get("enterprise_name");

        if (!postParameters.containsKey("url")) {
            logger.warn("Agent has received command for camera setup without required argument 'url'");

            this.sendResponse(httpExchange, 400);

            return;
        }

        String url = URLDecoder.decode(postParameters.get("url"), "UTF-8");

        if (!postParameters.containsKey("rtsp_port")) {
            logger.warn("Agent has received command for stream setup without required argument 'rtsp_port'");

            this.sendResponse(httpExchange, 400);

            return;
        }

        String rtspPort = postParameters.get("rtsp_port");

        if (!postParameters.containsKey("record")) {
            logger.warn("Agent has received command for stream setup without required argument 'record'");

            this.sendResponse(httpExchange, 400);

            return;
        }

        String record = postParameters.get("record");

        if (!postParameters.containsKey("screenshot")) {
            logger.warn("Agent has received command for stream setup without required argument 'screenshot'");

            this.sendResponse(httpExchange, 400);

            return;
        }

        String screenshot = postParameters.get("screenshot");

        if (!postParameters.containsKey("fms_app")) {
            logger.warn("Agent has received command for stream setup without required argument 'fms_app'");

            this.sendResponse(httpExchange, 400);

            return;
        }

        String fmsApp = postParameters.get("fms_app");

        StreamVO streamVO = new StreamVO();

        try {
            streamVO.setId(Integer.parseInt(id));

            streamVO.setEnterpriseName(enterpriseName);

            streamVO.setUrl(url);

            streamVO.setRtspPort(Integer.parseInt(rtspPort));

            streamVO.setRecord(Boolean.parseBoolean(record));

            streamVO.setScreenshot(Boolean.parseBoolean(screenshot));

            streamVO.setFmsApp(fmsApp);
        } catch (Exception e) {
            logger.warn(e.getMessage());

            this.sendResponse(httpExchange, 400);

            return;
        }

        StreamProxy streamProxy = (StreamProxy) this.facade.retrieveProxy("StreamProxy");

        streamProxy.addStream(streamVO);

        String taskName = "StreamMonitoringTask_" + id;

        if (!this.facade.hasTask(taskName)) {
            this.facade.executeTask(new StreamMonitoringTask(taskName, configurationProxy.data().getStreamMonitoringTimeout(), Integer.parseInt(id)));
        }

        logger.info("Stream [{}] has been setup successfully", id);

        this.sendResponse(httpExchange, 200);
    }
}