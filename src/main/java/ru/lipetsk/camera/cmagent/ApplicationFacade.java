package ru.lipetsk.camera.cmagent;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lipetsk.camera.cmagent.core.facade.Facade;
import ru.lipetsk.camera.cmagent.core.handler.AgentStopHandler;
import ru.lipetsk.camera.cmagent.core.handler.CameraSetupHandler;
import ru.lipetsk.camera.cmagent.core.handler.StreamRemoveHandler;
import ru.lipetsk.camera.cmagent.core.handler.StreamSetupHandler;
import ru.lipetsk.camera.cmagent.core.proxy.CameraProxy;
import ru.lipetsk.camera.cmagent.core.proxy.ConfigurationProxy;
import ru.lipetsk.camera.cmagent.core.proxy.StreamProxy;
import ru.lipetsk.camera.cmagent.core.task.AgentKeepAliveTask;
import ru.lipetsk.camera.cmagent.exception.HttpClientException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Ivan on 23.03.2016.
 */
public class ApplicationFacade extends Facade {
    private final static Logger logger = LoggerFactory.getLogger(ApplicationFacade.class);

    private static ApplicationFacade instance = null;

    public synchronized static ApplicationFacade getInstance() {
        if (instance == null) {
            instance = new ApplicationFacade();
        }

        return instance;
    }

    @Override
    protected void proxyInitialization() {
        super.proxyInitialization();

        this.registerProxy(new ConfigurationProxy());

        this.registerProxy(new CameraProxy());

        this.registerProxy(new StreamProxy());

        logger.info("Proxies initialization success");
    }

    @Override
    protected void taskInitialization() {
        super.taskInitialization();

        logger.info("Tasks initialization success");
    }

    @Override
    protected void webServerInitialization() {
        super.webServerInitialization();

        this.createWebServerContext("/agent/stop", new AgentStopHandler());

        this.createWebServerContext("/camera/setup", new CameraSetupHandler());

        this.createWebServerContext("/stream/setup", new StreamSetupHandler());

        this.createWebServerContext("/stream/remove", new StreamRemoveHandler());

        logger.info("WebServer initialization success");
    }

    public void bootstrap(String configurationFile) {
        this.configuration(configurationFile);

        this.bind();

        this.registration();
    }

    private void configuration(String configurationFile) {
        Properties properties = new Properties();

        try {
            properties.load(new FileInputStream(configurationFile));
        } catch (IOException e) {
            logger.error(e.getMessage());

            System.exit(-1);
        }

        ConfigurationProxy configurationProxy = (ConfigurationProxy) this.retrieveProxy("ConfigurationProxy");

        try {
            configurationProxy.data().setCmsPort(Integer.parseInt(properties.getProperty("cms.port")));

            configurationProxy.data().setFmsHost(properties.getProperty("fms.host"));

            configurationProxy.data().setFmsPort(Integer.parseInt(properties.getProperty("fms.port")));

            configurationProxy.data().setAgentPortMin(Integer.parseInt(properties.getProperty("agent.port.min")));

            configurationProxy.data().setAgentPortMax(Integer.parseInt(properties.getProperty("agent.port.max")));

            configurationProxy.data().setAgentKeepAliveTimeout(Integer.parseInt(properties.getProperty("agent.keep-alive.timeout")));

            configurationProxy.data().setAgentKeepAliveCount(Integer.parseInt(properties.getProperty("agent.keep-alive.count")));

            configurationProxy.data().setCameraMonitoringTimeout(Integer.parseInt(properties.getProperty("camera.monitoring.timeout")));

            configurationProxy.data().setStreamMonitoringTimeout(Integer.parseInt(properties.getProperty("stream.monitoring.timeout")));
        } catch (Exception e) {
            logger.error("Failed to load configuration");

            logger.error(e.getMessage());

            System.exit(-1);
        }

        logger.info("Agent load configuration success");
    }

    private void bind() {
        ConfigurationProxy configurationProxy = (ConfigurationProxy) this.retrieveProxy("ConfigurationProxy");

        boolean isBind = false;

        for (int i = configurationProxy.data().getAgentPortMin(); i < configurationProxy.data().getAgentPortMax(); i++) {
            try {
                this.bindWebServer(i);

                isBind = true;

                break;
            } catch (IOException ignored) { }
        }

        if (!isBind) {
            logger.error("Agent could not find free port for binding");

            System.exit(-1);
        }

        this.startWebServer();

        logger.info("Agent management initialization success");
    }

    private void registration() {
        ConfigurationProxy configurationProxy = (ConfigurationProxy) this.retrieveProxy("ConfigurationProxy");

        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(configurationProxy.getAgentId()));
        map.put("port", String.valueOf(configurationProxy.getAgentPort()));
        map.put("pid", String.valueOf(configurationProxy.getAgentPid()));

        try {
            HttpResponse httpResponse = this.sendPostRequest(configurationProxy.getCmsUri() + "/cms/agent/registration", map);

            if (httpResponse != null) {
                int responseCode = httpResponse.getStatusLine().getStatusCode();

                if (responseCode == 200) {
                    logger.info("Agent has registered successfully");

                    this.executeTask(new AgentKeepAliveTask(configurationProxy.data().getAgentKeepAliveTimeout()));

                    return;
                }

                throw new HttpClientException("Response code for registration - " + responseCode);
            }
        } catch (IOException | HttpClientException e) {
            String message = e.getMessage() != null ? e.getMessage() : e.getCause().getMessage();

            logger.error(message);
        }

        logger.error("Failed to register agent");

        System.exit(-1);
    }
}