package io.birdactyl.sdk;

public abstract class MixinClass {
    protected BirdactylPlugin plugin;

    public void init(BirdactylPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract MixinResult handle(MixinContext ctx);
}
