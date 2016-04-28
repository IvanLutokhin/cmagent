package ru.lipetsk.camera.cmagent.core.facade;

import com.sun.net.httpserver.HttpHandler;
import org.apache.http.HttpResponse;
import ru.lipetsk.camera.cmagent.core.proxy.IProxy;
import ru.lipetsk.camera.cmagent.core.task.ITask;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Ivan on 23.03.2016.
 */
public interface IFacade {
    void registerProxy(IProxy proxy);

    IProxy retrieveProxy(String proxyName);

    boolean hasProxy(String proxyName);

    IProxy unregisterProxy(String proxyName);

    void registerTask(ITask task);

    ITask retrieveTask(String taskName);

    boolean hasTask(String taskName);

    ITask unregisterTask(String taskName);

    void executeTask(ITask task, boolean autoRegistration);

    void executeTask(ITask task);

    void bindWebServer(int port) throws IOException;

    void startWebServer();

    void stopWebServer(int delay);

    void createWebServerContext(String path, HttpHandler httpHandler);

    void removeWebServerContext(String path);

    HttpResponse sendGetRequest(String uri, String query) throws IOException;

    HttpResponse sendGetRequest(String uri, Map<String, String> args) throws IOException;

    HttpResponse sendPostRequest(String uri, Map<String, String> args) throws IOException;
}