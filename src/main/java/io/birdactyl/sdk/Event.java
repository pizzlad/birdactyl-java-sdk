package io.birdactyl.sdk;

import java.util.Map;

public class Event {
    private final String type;
    private final Map<String, String> data;
    private final boolean sync;

    public Event(String type, Map<String, String> data, boolean sync) {
        this.type = type;
        this.data = data;
        this.sync = sync;
    }

    public String getType() { return type; }
    public Map<String, String> getData() { return data; }
    public String get(String key) { return data.get(key); }
    public boolean isSync() { return sync; }
}
