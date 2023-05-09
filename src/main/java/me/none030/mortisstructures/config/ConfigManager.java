package me.none030.mortisstructures.config;

import me.none030.mortisstructures.manager.MainManager;

public class ConfigManager {

    private final MainManager mainManager;
    private final SchematicConfig schematicConfig;
    private final MainConfig mainConfig;

    public ConfigManager(MainManager mainManager) {
        this.mainManager = mainManager;
        this.schematicConfig = new SchematicConfig(this);
        this.mainConfig = new MainConfig(this);
    }

    public MainManager getMainManager() {
        return mainManager;
    }

    public SchematicConfig getSchematicConfig() {
        return schematicConfig;
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }
}
