package io.birdactyl.sdk;

import org.yaml.snakeyaml.Yaml;
import java.io.*;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class HotConfig<T> {
    private final File file;
    private final Class<T> type;
    private final ConfigLoader<T> loader;
    private final ConfigSaver<T> saver;
    private volatile T config;
    private Consumer<T> onChange;
    private long lastModified;
    private ScheduledExecutorService watcher;

    @FunctionalInterface
    public interface ConfigLoader<T> { T load(Map<String, Object> data); }

    @FunctionalInterface
    public interface ConfigSaver<T> { Map<String, Object> save(T config); }

    public HotConfig(File file, T defaultConfig, ConfigLoader<T> loader, ConfigSaver<T> saver) {
        this.file = file;
        this.type = (Class<T>) defaultConfig.getClass();
        this.loader = loader;
        this.saver = saver;
        this.config = defaultConfig;
        load();
    }

    public T get() { return config; }

    public void set(T config) {
        this.config = config;
        save();
    }

    public HotConfig<T> onChange(Consumer<T> callback) {
        this.onChange = callback;
        return this;
    }

    public HotConfig<T> dynamicConfig() {
        if (watcher != null) return this;
        watcher = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "dynamic-config");
            t.setDaemon(true);
            return t;
        });
        watcher.scheduleAtFixedRate(this::checkReload, 1, 1, TimeUnit.SECONDS);
        return this;
    }

    public void stopWatching() {
        if (watcher != null) {
            watcher.shutdown();
            watcher = null;
        }
    }

    private void checkReload() {
        if (!file.exists()) return;
        long modified = file.lastModified();
        if (modified > lastModified) {
            lastModified = modified;
            load();
            System.out.println("[HotConfig] Reloaded " + file.getName());
            if (onChange != null) {
                try { onChange.accept(config); } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void load() {
        if (!file.exists()) {
            save();
            return;
        }
        try (FileReader reader = new FileReader(file)) {
            Map<String, Object> data = new Yaml().load(reader);
            if (data != null) {
                config = loader.load(data);
            }
            lastModified = file.lastModified();
        } catch (Exception e) {
            System.err.println("[HotConfig] Failed to load " + file.getName() + ": " + e.getMessage());
        }
    }

    public void save() {
        try {
            file.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(file)) {
                new Yaml().dump(saver.save(config), writer);
            }
            lastModified = file.lastModified();
        } catch (Exception e) {
            System.err.println("[HotConfig] Failed to save " + file.getName() + ": " + e.getMessage());
        }
    }
}
