package me.none030.mortisstructures.config;

public class SchematicConfig extends Config {

    public SchematicConfig(ConfigManager configManager) {
        super("schematics/hut.schem", configManager);
    }

    @Override
    public void loadConfig() {
        saveConfig();
    }
}
