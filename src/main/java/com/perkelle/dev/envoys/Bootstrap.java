package com.perkelle.dev.envoys;

import com.perkelle.dev.dependencymanager.dependency.Dependency;
import com.perkelle.dev.envoys.dependencyinjection.Dependencies;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class Bootstrap extends JavaPlugin {

    private Envoys instance;

    @Override
    public void onEnable() {
        if (!isJava16()) {
            checkMigration();

            List<Dependency> dependencies = new Dependencies().getDependencies(this);
            CountDownLatch latch = new CountDownLatch(dependencies.size());

            Runnable onComplete = latch::countDown;
            Consumer<Exception> onError = e -> {
                latch.countDown();
                getLogger().severe("An error occurred while loading a dependency. Envoys will continue to load but is likely to not work!");
                e.printStackTrace();
            };

            dependencies.forEach((dep) -> dep.load(onComplete, onError));

            getServer().getScheduler().runTaskAsynchronously(this, () -> {
                getLogger().info("Loading dependencies...");
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    getLogger().info("Dependencies loading interrupted");
                    e.printStackTrace();
                    return;
                }

                getLogger().info("Dependencies loaded successfully");
                getServer().getScheduler().runTask(this, () -> {
                    instance = new Envoys(this);
                    instance.run();
                });
            });
        } else {
            instance = new Envoys(this);
            instance.run();
        }
    }

    @Override
    public void onDisable() {
        if (instance != null) {
            instance.onDisable();
        }
    }

    private boolean isJava16() {
        // Old versions were 1.8
        // New versions are 11, 16, etc
        try {
            int version = Integer.parseInt(System.getProperty("java.specification.version"));
            return version >= 16;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void checkMigration() {
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        File cacheFolder = new File(this.getDataFolder(), "cache");
        if (!cacheFolder.exists()) {
            cacheFolder.mkdir();
        }

        File migratedFile = new File(cacheFolder, "migrated");

        if (!migratedFile.exists()) {
            for (File child : cacheFolder.listFiles()) {
                if (child.isDirectory() && child.getName().equals("temp")) {
                    for (File tempChild : child.listFiles()) {
                        tempChild.delete();
                    }
                }

                child.delete();
            }

            try {
                migratedFile.createNewFile();
            } catch (IOException ex) {
                getLogger().severe("Failed to create migrated file");
                ex.printStackTrace();
            }
        }

    }
}
