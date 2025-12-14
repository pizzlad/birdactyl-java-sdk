package io.birdactyl.sdk;

import java.util.HashMap;
import java.util.Map;

public class MixinContext {
    private final String target;
    private final String requestId;
    private final Map<String, Object> input;
    private final Map<String, Object> chainData;
    private Map<String, Object> modifiedInput;

    public MixinContext(String target, String requestId, Map<String, Object> input, Map<String, Object> chainData) {
        this.target = target;
        this.requestId = requestId;
        this.input = input != null ? input : new HashMap<>();
        this.chainData = chainData != null ? chainData : new HashMap<>();
    }

    public String getTarget() { return target; }
    public String getRequestId() { return requestId; }
    public Map<String, Object> getInput() { return input; }
    public Map<String, Object> getChainData() { return chainData; }

    public Object get(String key) { return input.get(key); }

    public String getString(String key) {
        Object v = input.get(key);
        return v instanceof String ? (String) v : "";
    }

    public int getInt(String key) {
        Object v = input.get(key);
        if (v instanceof Number) return ((Number) v).intValue();
        return 0;
    }

    public boolean getBool(String key) {
        Object v = input.get(key);
        return v instanceof Boolean && (Boolean) v;
    }

    public void set(String key, Object value) {
        if (modifiedInput == null) {
            modifiedInput = new HashMap<>(input);
        }
        modifiedInput.put(key, value);
    }

    public MixinResult next() {
        return new MixinResult(MixinResult.Action.NEXT, null, null, modifiedInput);
    }

    public MixinResult returnValue(Object data) {
        Map<String, Object> output = new HashMap<>();
        if (data instanceof Map) {
            output = (Map<String, Object>) data;
        } else {
            output.put("result", data);
        }
        return new MixinResult(MixinResult.Action.RETURN, output, null, null);
    }

    public MixinResult error(String message) {
        return new MixinResult(MixinResult.Action.ERROR, null, message, null);
    }
}
