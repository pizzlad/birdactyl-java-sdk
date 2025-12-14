package io.birdactyl.sdk;

import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private static final Gson gson = new Gson();
    private final int status;
    private final Map<String, String> headers;
    private final byte[] body;

    private Response(int status, Map<String, String> headers, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public static Response json(Object data) {
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("success", true);
        wrapper.put("data", data);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return new Response(200, headers, gson.toJson(wrapper).getBytes(StandardCharsets.UTF_8));
    }

    public static Response error(int status, String message) {
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("success", false);
        wrapper.put("error", message);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return new Response(status, headers, gson.toJson(wrapper).getBytes(StandardCharsets.UTF_8));
    }

    public static Response text(String text) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
        return new Response(200, headers, text.getBytes(StandardCharsets.UTF_8));
    }

    public static Response html(String html) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/html; charset=utf-8");
        return new Response(200, headers, html.getBytes(StandardCharsets.UTF_8));
    }

    public int getStatus() { return status; }
    public Map<String, String> getHeaders() { return headers; }
    public byte[] getBody() { return body; }
}
