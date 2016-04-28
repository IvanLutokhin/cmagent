package ru.lipetsk.camera.cmagent.core.facade;

import com.sun.net.httpserver.HttpHandler;
import org.apache.http.HttpResponse;
import ru.lipetsk.camera.cmagent.core.management.WebClient;
import ru.lipetsk.camera.cmagent.core.management.WebServer;
import ru.lipetsk.camera.cmagent.core.proxy.IProxy;
import ru.lipetsk.camera.cmagent.core.proxy.ProxyManager;
import ru.lipetsk.camera.cmagent.core.task.ITask;
import ru.lipetsk.camera.cmagent.core.task.TaskManager;
import ru.lipetsk.camera.cmagent.event.EventDispatcher;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Ivan on 23.03.2016.
 */
public class Facade extends EventDispatcher implements IFacade {
    protected static Facade instance = null;

    public synchronized static Facade getInstance() {
        if (instance == null) {
            instance = new Facade();
        }

        return instance;
    }

    protected ProxyManager proxyManager;

    protected TaskManager taskManager;

    protected WebServer webServer;

    protected WebClient webClient;

    protected Facade() {
        this.proxyInitialization();

        this.taskInitialization();

        this.webServerInitialization();

        this.webClientInitialization();
    }

    protected void proxyInitialization() {
        if (this.proxyManager == null) {
            this.proxyManager = ProxyManager.getInstance();
        }
    }

    protected void taskInitialization() {
        if (this.taskManager == null) {
            this.taskManager = TaskManager.getInstance();
        }
    }

    protected void webServerInitialization() {
        if (this.webServer == null) {
            this.webServer = WebServer.getInstance();
        }
    }

    private void webClientInitialization() {
        if (this.webClient == null) {
            this.webClient = WebClient.getInstance();
        }
    }

    @Override
    public void registerProxy(IProxy proxy) {
        if (this.proxyManager != null) {
            this.proxyManager.register(proxy);
        }
    }

    @Override
    public IProxy retrieveProxy(String proxyName) {
        return (this.proxyManager != null) ? this.proxyManager.retrieve(proxyName) : null;
    }

    @Override
    public boolean hasProxy(String proxyName) {
        return this.proxyManager != null && this.proxyManager.hasProxy(proxyName);
    }

    @Override
    public IProxy unregisterProxy(String proxyName) {
        return (this.proxyManager != null) ? this.proxyManager.unregister(proxyName) : null;
    }

    @Override
    public void registerTask(ITask task) {
        if (this.taskManager != null) {
            this.taskManager.register(task);
        }
    }

    @Override
    public ITask retrieveTask(String taskName) {
        return (this.taskManager != null) ? this.taskManager.retrieve(taskName) : null;
    }

    @Override
    public boolean hasTask(String taskName) {
        return this.taskManager != null && this.taskManager.hasTask(taskName);
    }

    @Override
    public ITask unregisterTask(String taskName) {
        return (this.taskManager != null) ? this.taskManager.unregister(taskName) : null;
    }

    @Override
    public void executeTask(ITask task, boolean autoRegistration) {
        if (this.taskManager != null) {
            this.taskManager.execute(task, autoRegistration);
        }
    }

    @Override
    public void executeTask(ITask task) {
        if (this.taskManager != null) {
            this.taskManager.execute(task);
        }
    }

    @Override
    public void bindWebServer(int port) throws IOException {
        if (this.webServer != null) {
            this.webServer.bind(port);
        }
    }

    @Override
    public void startWebServer() {
        if (this.webServer != null) {
            this.webServer.start();
        }
    }

    @Override
    public void stopWebServer(int delay) {
        if (this.webServer != null) {
            this.webServer.stop(delay);
        }
    }

    @Override
    public void createWebServerContext(String path, HttpHandler httpHandler) {
        if (this.webServer != null) {
            this.webServer.createContext(path, httpHandler);
        }
    }

    @Override
    public void removeWebServerContext(String path) {
        if (this.webServer != null) {
            this.webServer.removeContext(path);
        }
    }

    @Override
    public HttpResponse sendGetRequest(String uri, String query) throws IOException {
        return (this.webClient != null) ? this.webClient.get(uri, query) : null;
    }

    @Override
    public HttpResponse sendGetRequest(String uri, Map<String, String> args) throws IOException {
        return (this.webClient != null) ? this.webClient.get(uri, args) : null;
    }

    @Override
    public HttpResponse sendPostRequest(String uri, Map<String, String> args) throws IOException {
        return (this.webClient != null) ? this.webClient.post(uri, args) : null;
    }
}