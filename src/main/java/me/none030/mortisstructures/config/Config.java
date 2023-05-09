package me.none030.mortisstructures.config;

import me.none030.mortisstructures.MortisStructures;

import java.io.File;

public abstract class Config {

    private final MortisStructures plugin = MortisStructures.getInstance();
    private final String fileName;
    private final ConfigManager configManager;

    public Config(String fileName, ConfigManager configManager) {
        this.fileName = fileName;
        this.configManager = configManager;
        loadConfig();
    }

    public abstract void loadConfig();

    public File saveConfig() {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.saveResource(fileName, true);
        }
        return file;
    }

    public MortisStructures getPlugin() {
        return plugin;
    }

    public String getFileName() {
        return fileName;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
