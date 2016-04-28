package ru.lipetsk.camera.cmagent.core.task;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lipetsk.camera.cmagent.core.proxy.ConfigurationProxy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ivan on 24.03.2016.
 */
public class AgentKeepAliveTask extends Task {
    private final static Logger logger = LoggerFactory.getLogger(AgentKeepAliveTask.class);

    public AgentKeepAliveTask(long period) {
        super("AgentKeepAliveTask", TaskType.SCHEDULED_WITH_FIXED_RATE, period, period);
    }

    @Override
    public void execute() {
        logger.debug("Agent keep-alive task has been running");

        ConfigurationProxy configurationProxy = (ConfigurationProxy) this.facade.retrieveProxy("ConfigurationProxy");

        Map<String, String> map = new HashMap<>();

        map.put("id", String.valueOf(configurationProxy.getAgentId()));
        map.put("port", String.valueOf(configurationProxy.getAgentPort()));
        map.put("pid", String.valueOf(configurationProxy.getAgentPid()));

        int count = configurationProxy.data().getAgentKeepAliveCount();

        for (int i = 0; i < count; i++) {
            try {
                HttpResponse httpResponse = this.facade.sendPostRequest(configurationProxy.getCmsUri() + "/cms/agent/keep-alive", map);

                int responseCode = httpResponse.getStatusLine().getStatusCode();

                if (responseCode == 200) {
                    break;
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }

            if (i == count - 1) {
                logger.error("Agent could not get response from CMS on keep-alive request");

                System.exit(-1);
            }
        }
    }

    @Override
    public void onRegister() { }

    @Override
    public void onUnregister() { }
}