package io.birdactyl.sdk;

import com.google.gson.Gson;
import io.birdactyl.sdk.proto.HTTPRequest;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request {
    private static final Gson gson = new Gson();
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final Map<String, String> query;
    private final byte[] body;
    private final String userId;
    private Map<String, String> pathParams;

    public Request(HTTPRequest req) {
        this.method = req.getMethod();
        this.path = req.getPath();
        this.headers = req.getHeadersMap();
        this.query = req.getQueryMap();
        this.body = req.getBody().toByteArray();
        this.userId = req.getUserId();
    }

    public String getMethod() { return method; }
    public String getPath() { return path; }
    public Map<String, String> getHeaders() { return headers; }
    public Map<String, String> getQuery() { return query; }
    public byte[] getBody() { return body; }
    public String getUserId() { return userId; }

    public String header(String name) {
        return headers.getOrDefault(name, headers.get(name.toLowerCase()));
    }

    public String query(String name) {
        return query.get(name);
    }

    public String query(String name, String defaultValue) {
        return query.getOrDefault(name, defaultValue);
    }

    public int queryInt(String name, int defaultValue) {
        String val = query.get(name);
        if (val == null) return defaultValue;
        try { return Integer.parseInt(val); } catch (NumberFormatException e) { return defaultValue; }
    }

    public boolean queryBool(String name, boolean defaultValue) {
        String val = query.get(name);
        if (val == null) return defaultValue;
        return "true".equalsIgnoreCase(val) || "1".equals(val);
    }

    public void setPathParams(Map<String, String> params) {
        this.pathParams = params;
    }

    public String pathParam(String name) {
        if (pathParams != null) return pathParams.get(name);
        return extractPathParam(name);
    }

    private String extractPathParam(String name) {
        String[] pathParts = path.split("/");
        for (int i = 0; i < pathParts.length; i++) {
            if (pathParts[i].equals(name) || pathParts[i].equals(":" + name)) {
                return i + 1 < pathParts.length ? pathParts[i + 1] : null;
            }
        }
        int idx = path.lastIndexOf("/");
        if (idx >= 0 && idx < path.length() - 1) {
            return path.substring(idx + 1);
        }
        return null;
    }

    public String bodyString() {
        return new String(body);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> json() {
        return gson.fromJson(new String(body), Map.class);
    }

    public <T> T json(Class<T> clazz) {
        return gson.fromJson(new String(body), clazz);
    }
}
