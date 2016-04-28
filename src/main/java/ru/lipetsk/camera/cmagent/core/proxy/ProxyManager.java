package ru.lipetsk.camera.cmagent.core.proxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ivan on 22.03.2016.
 */
public class ProxyManager {
    private static ProxyManager instance = null;

    public synchronized static ProxyManager getInstance() {
        if (instance == null) {
            instance = new ProxyManager();
        }

        return instance;
    }

    private Map<String, IProxy> proxyMap;

    private ProxyManager() {
        this.proxyMap = new ConcurrentHashMap<>();
    }

    public void register(IProxy proxy) {
        this.proxyMap.put(proxy.getName(), proxy);

        proxy.onRegister();
    }

    public IProxy retrieve(String proxyName) {
        return this.proxyMap.get(proxyName);
    }

    public boolean hasProxy(String proxyName) {
        return this.proxyMap.containsKey(proxyName);
    }

    public IProxy unregister(String proxyName) {
        IProxy proxy = this.retrieve(proxyName);

        if (proxy != null) {
            this.proxyMap.remove(proxyName);

            proxy.onUnregister();
        }

        return proxy;
    }
}