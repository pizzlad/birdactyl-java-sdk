package io.birdactyl.sdk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MixinResult {
    public enum Action { NEXT, RETURN, ERROR }

    private final Action action;
    private final Map<String, Object> output;
    private final String error;
    private final Map<String, Object> modifiedInput;
    private final List<Notification> notifications;

    MixinResult(Action action, Map<String, Object> output, String error, Map<String, Object> modifiedInput, List<Notification> notifications) {
        this.action = action;
        this.output = output;
        this.error = error;
        this.modifiedInput = modifiedInput;
        this.notifications = notifications != null ? notifications : new ArrayList<>();
    }

    public Action getAction() { return action; }
    public Map<String, Object> getOutput() { return output; }
    public String getError() { return error; }
    public Map<String, Object> getModifiedInput() { return modifiedInput; }
    public List<Notification> getNotifications() { return notifications; }

    public static class Notification {
        private final String title;
        private final String message;
        private final String type;

        public Notification(String title, String message, String type) {
            this.title = title;
            this.message = message;
            this.type = type;
        }

        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public String getType() { return type; }
    }
}
