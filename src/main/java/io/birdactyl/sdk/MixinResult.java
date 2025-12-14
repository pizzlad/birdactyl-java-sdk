package io.birdactyl.sdk;

import java.util.Map;

public class MixinResult {
    public enum Action { NEXT, RETURN, ERROR }

    private final Action action;
    private final Map<String, Object> output;
    private final String error;
    private final Map<String, Object> modifiedInput;

    MixinResult(Action action, Map<String, Object> output, String error, Map<String, Object> modifiedInput) {
        this.action = action;
        this.output = output;
        this.error = error;
        this.modifiedInput = modifiedInput;
    }

    public Action getAction() { return action; }
    public Map<String, Object> getOutput() { return output; }
    public String getError() { return error; }
    public Map<String, Object> getModifiedInput() { return modifiedInput; }
}
