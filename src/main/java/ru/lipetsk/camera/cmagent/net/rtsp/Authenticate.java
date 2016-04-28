package ru.lipetsk.camera.cmagent.net.rtsp;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import ru.lipetsk.camera.cmagent.net.rtsp.message.IRequest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ivan on 30.03.2016.
 */
public class Authenticate {
    public static String execute(IRequest request, String authenticate, Credentials credentials) {
        if (request == null) {
            return null;
        }

        if (authenticate.startsWith("Basic")) {
            return basic(credentials);
        } else if (authenticate.startsWith("Digest")) {
            return digest(authenticate, request.getMethod().toString(), request.getURI(), credentials);
        }

        return null;
    }

    public static String basic(Credentials credentials) {
        if (credentials == null) {
            return null;
        }

        String message = Base64.encodeBase64String((credentials.getUsername() + ":" + credentials.getPassword()).getBytes());

        return "Basic " + message;
    }

    public static String basic(String username, String password) {
        return basic(new Credentials(username, password));
    }

    public static String digest(String authenticate, String method, String uri, Credentials credentials) {
        if (credentials == null) {
            return null;
        }

        authenticate = authenticate.substring(authenticate.indexOf(" ") + 1, authenticate.length());

        String[] items = authenticate.split(", ");

        Map<String, String> map = new HashMap<>();

        for (String item : items) {
            if (item.contains("=")) {
                String[] params = item.split("=");

                map.put(params[0].trim(), params[1].replace("\"", "").replace(",", "").trim());
            }
        }

        if (map.containsKey("realm") && map.containsKey("nonce")) {
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");

                String ha1 = Hex.encodeHexString(md5.digest((credentials.getUsername() + ":" + map.get("realm") + ":" + credentials.getPassword()).getBytes()));

                String ha2 = Hex.encodeHexString(md5.digest((method + ":" + uri).getBytes()));

                String response = Hex.encodeHexString(md5.digest((ha1 + ":" + map.get("nonce") + ":" + ha2).getBytes()));

                return String.format("Digest username=\"%s\", realm=\"%s\", nonce=\"%s\", uri=\"%s\", response=\"%s\"", credentials.getUsername(), map.get("realm"), map.get("nonce"), uri, response);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static String digest(String authenticate, String method, String uri, String username, String password) {
        return digest(authenticate, method, uri, new Credentials(username, password));
    }
}
