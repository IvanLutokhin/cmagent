package ru.lipetsk.camera.cmagent.core.management;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Ivan on 23.03.2016.
 */
public class WebClient {
    private static WebClient instance = null;

    public synchronized static WebClient getInstance() {
        if (instance == null) {
            instance = new WebClient();
        }

        return instance;
    }

    private final String USER_AGENT = "CMAgent/1.0";

    private WebClient() { }

    public HttpResponse get(String uri, String query) throws IOException {
        if (query.startsWith("?")) {
            query = query.substring(1);
        }

        HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();

        HttpGet httpGet = new HttpGet(uri + "?" + query);

        httpGet.addHeader("User-Agent", USER_AGENT);

        return httpClient.execute(httpGet);
    }

    public HttpResponse get(String uri, Map<String, String> args) throws IOException {
        String query = this.buildQuery(args);

        return this.get(uri, query);
    }

    public HttpResponse post(String uri, Map<String, String> args) throws IOException {
        List<NameValuePair> nameValuePairs = new ArrayList<>();

        for (Map.Entry<String, String> entry : args.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        HttpPost httpPost = new HttpPost(uri);

        httpPost.addHeader("User-Agent", USER_AGENT);

        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();

        return httpClient.execute(httpPost);
    }

    private String buildQuery(Map<String, String> map) {
        if (map == null) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append("&");
            }

            stringBuilder.append((entry.getKey() != null) ? entry.getKey() : "");
            stringBuilder.append((entry.getKey() != null && entry.getValue() != null) ? "=" : "");
            stringBuilder.append((entry.getValue() != null) ? entry.getValue() : "");
        }

        return stringBuilder.toString();
    }
}
