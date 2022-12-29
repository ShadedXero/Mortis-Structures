package me.none030.mortisstructure;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import me.none030.mortisstructure.structure.StructureManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

public final class MortisStructure extends JavaPlugin {

    private static MortisStructure Instance;
    private static WorldEditPlugin worldEdit;
    private static boolean towny = false;

    private StructureManager structureManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Instance = this;
        worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        if (getServer().getPluginManager().getPlugin("Towny") != null) {
            towny = true;
        }
        structureManager = new StructureManager();
    }

    public static MortisStructure getInstance() {
        return Instance;
    }

    public static WorldEditPlugin getWorldEdit() {
        return worldEdit;
    }

    public static boolean hasTowny() {
        return towny;
    }

    public StructureManager getStructureManager() {
        return structureManager;
    }

}
