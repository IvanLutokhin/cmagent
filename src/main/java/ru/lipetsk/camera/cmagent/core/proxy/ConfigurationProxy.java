package ru.lipetsk.camera.cmagent.core.proxy;

import ru.lipetsk.camera.cmagent.core.proxy.vo.ConfigurationVO;

/**
 * Created by Ivan on 23.03.2016.
 */
public class ConfigurationProxy extends Proxy {
    public ConfigurationProxy() {
        super("ConfigurationProxy", new ConfigurationVO());
    }

    public ConfigurationVO data() {
        return (ConfigurationVO) this.valueObject;
    }

    public int getAgentId() {
        return Integer.parseInt(System.getProperty("agent.id"));
    }

    public int getAgentPort() {
        return Integer.parseInt(System.getProperty("agent.port"));
    }

    public int getAgentPid() {
        return Integer.parseInt(System.getProperty("agent.pid"));
    }

    public String getCmsUri() { return "http://127.0.0.1:" + this.data().getCmsPort(); }

    @Override
    public void onRegister() { }

    @Override
    public void onUnregister() { }
}