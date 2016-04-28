package ru.lipetsk.camera.cmagent.core.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.lipetsk.camera.cmagent.core.facade.FacadeHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ivan on 23.03.2016.
 */
public abstract class ContextHandler extends FacadeHolder implements HttpHandler {
    protected void sendResponse(HttpExchange httpExchange, int statusCode, String body) throws IOException {
        httpExchange.sendResponseHeaders(statusCode, body.length());

        OutputStream outputStream = httpExchange.getResponseBody();

        outputStream.write(body.getBytes());

        outputStream.close();
    }

    protected void sendResponse(HttpExchange httpExchange, int statusCode) throws IOException {
        this.sendResponse(httpExchange, statusCode, "");
    }

    protected Map<String, String> getQueryArguments(HttpExchange httpExchange) {
        if (httpExchange.getRequestMethod().equalsIgnoreCase("get")) {
            String query = httpExchange.getRequestURI().getQuery();

            return this.parseQuery(query);
        }

        return null;
    }

    protected Map<String, String> getPostParameters(HttpExchange httpExchange) throws IOException {
        if (httpExchange.getRequestMethod().equalsIgnoreCase("post")) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()))) {
                String query = bufferedReader.readLine();

                return this.parseQuery(query);
            }
        }

        return null;
    }

    private Map<String, String> parseQuery(String query) {
        if (query != null) {
            Map<String, String> map = new HashMap<>();

            for (String pair : query.split("[&]")) {
                String[] item = pair.split("[=]");

                String key = null;

                if (item.length > 0) {
                    key = item[0];
                }

                String value = null;

                if (item.length > 1) {
                    value = item[1];
                }

                map.put(key, value);
            }

            return map;
        }

        return null;
    }
}