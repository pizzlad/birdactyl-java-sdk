package io.birdactyl.sdk;

@FunctionalInterface
public interface MixinHandler {
    MixinResult handle(MixinContext ctx);
}
