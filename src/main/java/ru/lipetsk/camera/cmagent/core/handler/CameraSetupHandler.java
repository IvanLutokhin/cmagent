package ru.lipetsk.camera.cmagent.core.handler;

import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lipetsk.camera.cmagent.core.proxy.CameraProxy;
import ru.lipetsk.camera.cmagent.core.proxy.ConfigurationProxy;
import ru.lipetsk.camera.cmagent.core.proxy.vo.CameraVO;
import ru.lipetsk.camera.cmagent.core.task.CameraMonitoringTask;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Ivan on 24.03.2016.
 */
public class CameraSetupHandler extends ContextHandler {
    private final static Logger logger = LoggerFactory.getLogger(CameraSetupHandler.class);

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Map<String, String> postParameters = this.getPostParameters(httpExchange);

        if (postParameters == null) {
            logger.warn("Agent has received command for camera setup without parameters");

            this.sendResponse(httpExchange, 400);

            return;
        }

        ConfigurationProxy configurationProxy = (ConfigurationProxy) this.facade.retrieveProxy("ConfigurationProxy");

        if (!postParameters.containsKey("id")) {
            logger.warn("Agent has received command for camera setup without required argument 'id'");

            this.sendResponse(httpExchange, 400);

            return;
        }

        int id;

        try {
            id = Integer.parseInt(postParameters.get("id"));
        } catch (NumberFormatException e) {
            logger.warn("Agent has received command for camera setup with illegal argument 'id'");

            this.sendResponse(httpExchange, 400);

            return;
        }

        if (id != configurationProxy.getAgentId()) {
            logger.warn("Agent has received command for camera setup with illegal argument 'id'");

            this.sendResponse(httpExchange, 403);

            return;
        }

        if (!postParameters.containsKey("ip_address")) {
            logger.warn("Agent has received command for camera setup without required argument 'ip_address'");

            this.sendResponse(httpExchange, 400);

            return;
        }

        String ipAddress = postParameters.get("ip_address");

        if (!postParameters.containsKey("rtsp_login")) {
            logger.warn("Agent has received command for camera setup without required argument 'rtsp_login'");

            this.sendResponse(httpExchange, 400);

            return;
        }

        String rtspLogin = postParameters.get("rtsp_login");

        if (!postParameters.containsKey("rtsp_password")) {
            logger.warn("Agent has received command for camera setup without required argument 'rtsp_password'");

            this.sendResponse(httpExchange, 400);

            return;
        }

        String rtspPassword = postParameters.get("rtsp_password");

        if (!postParameters.containsKey("http_login")) {
            logger.warn("Agent has received command for camera setup without required argument 'http_login'");

            this.sendResponse(httpExchange, 400);

            return;
        }

        String httpLogin = postParameters.get("http_login");

        if (!postParameters.containsKey("http_password")) {
            logger.warn("Agent has received command for camera setup without required argument 'http_password'");

            this.sendResponse(httpExchange, 400);

            return;
        }

        String httpPassword = postParameters.get("http_password");

        CameraVO cameraVO = new CameraVO();

        try {
            cameraVO.setId(id);

            cameraVO.setIpAddress(ipAddress);

            cameraVO.setRtspLogin(rtspLogin);

            cameraVO.setRtspPassword(rtspPassword);

            cameraVO.setHttpLogin(httpLogin);

            cameraVO.setHttpPassword(httpPassword);
        } catch (Exception e) {
            logger.warn(e.getMessage());

            this.sendResponse(httpExchange, 400);

            return;
        }

        CameraProxy cameraProxy = (CameraProxy) this.facade.retrieveProxy("CameraProxy");

        cameraProxy.setValueObject(cameraVO);

        if (!this.facade.hasTask("CameraMonitoringTask")) {
            this.facade.executeTask(new CameraMonitoringTask(configurationProxy.data().getCameraMonitoringTimeout()));
        }

        logger.info("Camera was setup successfully");

        this.sendResponse(httpExchange, 200);
    }
}