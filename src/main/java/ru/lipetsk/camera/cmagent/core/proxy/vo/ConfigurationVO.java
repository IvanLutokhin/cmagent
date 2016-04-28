package ru.lipetsk.camera.cmagent.core.proxy.vo;

import java.io.File;

/**
 * Created by Ivan on 23.03.2016.
 */
public class ConfigurationVO {
    private int cmsPort;

    private String fmsHost;

    private int fmsPort;

    private int agentPortMin;

    private int agentPortMax;

    private int agentKeepAliveTimeout;

    private int agentKeepAliveCount;

    private int cameraMonitoringTimeout;

    private int streamMonitoringTimeout;

    public int getCmsPort() {
        return this.cmsPort;
    }

    public void setCmsPort(int cmsPort) {
        if (cmsPort < 1 || cmsPort > 65536) {
            throw new IllegalArgumentException("Illegal argument 'cms.port' - " + cmsPort);
        }

        this.cmsPort = cmsPort;
    }

    public String getFmsHost() {
        return this.fmsHost;
    }

    public void setFmsHost(String fmsHost) {
        this.fmsHost = fmsHost;
    }

    public int getFmsPort() {
        return this.fmsPort;
    }

    public void setFmsPort(int fmsPort) {
        if (fmsPort < 1 || fmsPort > 65536) {
            throw new IllegalArgumentException("Illegal argument 'fms.port' - " + fmsPort);
        }

        this.fmsPort = fmsPort;
    }

    public int getAgentPortMin() {
        return this.agentPortMin;
    }

    public void setAgentPortMin(int agentPortMin) {
        if (agentPortMin < 1 || agentPortMin > 65536) {
            throw new IllegalArgumentException("Illegal argument 'agent.port.min' - " + agentPortMin);
        }

        this.agentPortMin = agentPortMin;
    }

    public int getAgentPortMax() {
        return this.agentPortMax;
    }

    public void setAgentPortMax(int agentPortMax) {
        if (agentPortMax < 1 || agentPortMax > 65536) {
            throw new IllegalArgumentException("Illegal argument 'agent.port.max' - " + agentPortMax);
        }

        this.agentPortMax = agentPortMax;
    }

    public int getAgentKeepAliveTimeout() {
        return this.agentKeepAliveTimeout;
    }

    public void setAgentKeepAliveTimeout(int agentKeepAliveTimeout) {
        if (agentKeepAliveTimeout < 0) {
            throw new IllegalArgumentException("Argument 'agent.keep-alive.timeout' can't be a negative");
        }

        this.agentKeepAliveTimeout = agentKeepAliveTimeout;
    }

    public int getAgentKeepAliveCount() {
        return this.agentKeepAliveCount;
    }

    public void setAgentKeepAliveCount(int agentKeepAliveCount) {
        if (agentKeepAliveCount < 0) {
            throw new IllegalArgumentException("Argument 'agent.keep-alive.count' can't be a negative");
        }

        this.agentKeepAliveCount = agentKeepAliveCount;
    }

    public int getCameraMonitoringTimeout() {
        return this.cameraMonitoringTimeout;
    }

    public void setCameraMonitoringTimeout(int cameraMonitoringTimeout) {
        if (cameraMonitoringTimeout < 0) {
            throw new IllegalArgumentException("Argument 'camera.monitoring.timeout' can't be a negative");
        }

        this.cameraMonitoringTimeout = cameraMonitoringTimeout;
    }

    public int getStreamMonitoringTimeout() {
        return this.streamMonitoringTimeout;
    }

    public void setStreamMonitoringTimeout(int streamMonitoringTimeout) {
        if (streamMonitoringTimeout < 0) {
            throw new IllegalArgumentException("Argument 'stream.monitoring.timeout' can't be a negative");
        }

        this.streamMonitoringTimeout = streamMonitoringTimeout;
    }
}