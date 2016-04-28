package ru.lipetsk.camera.cmagent.core.task;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.lipetsk.camera.cmagent.core.proxy.CameraProxy;
import ru.lipetsk.camera.cmagent.core.proxy.ConfigurationProxy;
import ru.lipetsk.camera.cmagent.core.proxy.StreamProxy;
import ru.lipetsk.camera.cmagent.core.proxy.vo.CameraVO;
import ru.lipetsk.camera.cmagent.core.proxy.vo.StreamVO;
import ru.lipetsk.camera.cmagent.exception.HttpClientException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ivan on 24.03.2016.
 */
public class StreamMonitoringTask extends Task {
    private final static Logger logger = LoggerFactory.getLogger(StreamMonitoringTask.class);

    private final int streamId;

    public StreamMonitoringTask(String name, long period, int streamId) {
        super(name, TaskType.SCHEDULED_WITH_FIXED_RATE, period);

        this.streamId = streamId;
    }

    @Override
    public void execute() {
        logger.debug("Stream [{}] monitoring task has been running", this.streamId);

        CameraProxy cameraProxy = (CameraProxy) this.facade.retrieveProxy("CameraProxy");

        StreamProxy streamProxy = (StreamProxy) this.facade.retrieveProxy("StreamProxy");

        StreamVO streamVO = streamProxy.retrieveStream(this.streamId);

        StreamVO.State state;

        String taskName = "StreamTranslationTask_" + this.streamId;

        if (cameraProxy.data().getState() != CameraVO.State.AVAILABLE) {
            if (this.facade.hasTask(taskName)) {
                this.facade.unregisterTask(taskName);
            }

            state = StreamVO.State.STOPPED;
        } else if (cameraProxy.data().getState() == CameraVO.State.AVAILABLE) {
            if (!this.facade.hasTask(taskName)) {
                this.facade.executeTask(new StreamTranslationTask(taskName, this.streamId));

                return;
            }

            StreamTranslationTask streamTranslationTask = (StreamTranslationTask) this.facade.retrieveTask(taskName);

            if (!streamTranslationTask.isActive()) {
                this.facade.unregisterTask(taskName);

                state = StreamVO.State.STOPPED;
            } else {
                state = StreamVO.State.RUNNING;
            }
        } else {
            state = StreamVO.State.UNKNOWN;
        }

        boolean isStateChange = streamVO.getState() != state;

        if (isStateChange) {
            logger.info("Stream [{}] has new state [{}]", this.streamId, state);

            ConfigurationProxy configurationProxy = (ConfigurationProxy) this.facade.retrieveProxy("ConfigurationProxy");

            streamVO.setState(state);

            Map<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(this.streamId));
            map.put("status_code", String.valueOf(state.getValue()));

            try {
                HttpResponse httpResponse = this.facade.sendPostRequest(configurationProxy.getCmsUri() + "/cms/stream/state", map);

                int responseCode = httpResponse.getStatusLine().getStatusCode();

                if (responseCode != 200) {
                    throw new HttpClientException("Response code for stream update state - " + responseCode);
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