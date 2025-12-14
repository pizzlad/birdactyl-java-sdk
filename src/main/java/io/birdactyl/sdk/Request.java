package io.birdactyl.sdk;

import com.google.gson.Gson;
import io.birdactyl.sdk.proto.HTTPRequest;
import java.util.Map;

public class Request {
    private static final Gson gson = new Gson();
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final Map<String, String> query;
    private final byte[] body;
    private final String userId;

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

    @SuppressWarnings("unchecked")
    public Map<String, Object> json() {
        return gson.fromJson(new String(body), Map.class);
    }
}
