package ru.lipetsk.camera.cmagent.core.task;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lipetsk.camera.cmagent.core.proxy.CameraProxy;
import ru.lipetsk.camera.cmagent.core.proxy.ConfigurationProxy;
import ru.lipetsk.camera.cmagent.core.proxy.vo.CameraVO;
import ru.lipetsk.camera.cmagent.exception.HttpClientException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ivan on 24.03.2016.
 */
public class CameraMonitoringTask extends Task {
    private final static Logger logger = LoggerFactory.getLogger(CameraMonitoringTask.class);

    public CameraMonitoringTask(long period) {
        super("CameraMonitoringTask", TaskType.SCHEDULED_WITH_FIXED_RATE, period);
    }

    @Override
    public void execute() {
        logger.debug("Camera monitoring task has been running");

        ConfigurationProxy configurationProxy = (ConfigurationProxy) this.facade.retrieveProxy("ConfigurationProxy");

        CameraProxy cameraProxy = (CameraProxy) this.facade.retrieveProxy("CameraProxy");

        CameraVO.State state;

        try {
            InetAddress inetAddress = InetAddress.getByName(cameraProxy.data().getIpAddress());

            state = inetAddress.isReachable(5000) ? CameraVO.State.AVAILABLE : CameraVO.State.NOT_AVAILABLE;
        } catch (Exception e) {
            logger.warn(e.getMessage());

            state = CameraVO.State.NOT_AVAILABLE;
        }

        boolean isStateChange = cameraProxy.data().getState() != state;

        if (isStateChange) {
            logger.info("Camera has new state [{}]", state);

            cameraProxy.data().setState(state);

            Map<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(cameraProxy.data().getId()));
            map.put("status_code", String.valueOf(state.getValue()));

            try {
                HttpResponse httpResponse = this.facade.sendPostRequest(configurationProxy.getCmsUri() + "/cms/camera/state", map);

                int responseCode = httpResponse.getStatusLine().getStatusCode();

                if (responseCode != 200) {
                    throw new HttpClientException("Response code for camera update state - " + responseCode);
                }
            } catch (Exception e) {
                logger.error(e.getMessage());

                System.exit(-1);
            }
        }
    }

    @Override
    public void onRegister() { }

    @Override
    public void onUnregister() { }
}