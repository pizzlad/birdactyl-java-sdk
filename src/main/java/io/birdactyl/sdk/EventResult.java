package io.birdactyl.sdk;

public class EventResult {
    private final boolean allowed;
    private final String message;

    private EventResult(boolean allowed, String message) {
        this.allowed = allowed;
        this.message = message;
    }

    public static EventResult allow() {
        return new EventResult(true, "");
    }

    public static EventResult block(String message) {
        return new EventResult(false, message);
    }

    public boolean isAllowed() { return allowed; }
    public String getMessage() { return message != null ? message : ""; }
}
