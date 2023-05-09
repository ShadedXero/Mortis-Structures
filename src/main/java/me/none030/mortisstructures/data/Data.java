package me.none030.mortisstructures.data;

import com.jeff_media.customblockdata.CustomBlockData;
import me.none030.mortisstructures.MortisStructures;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataType;

public abstract class Data {

    private final MortisStructures plugin = MortisStructures.getInstance();
    private final Location core;

    public Data(Block block) {
        this.core = block.getLocation();
    }

    public Data(Location location) {
        this.core = location;
    }

    public String get(String key) {
        CustomBlockData data = new CustomBlockData(core.getBlock(), plugin);
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        return data.get(namespacedKey, PersistentDataType.STRING);
    }

    public void set(String key, String value) {
        CustomBlockData data = new CustomBlockData(core.getBlock(), plugin);
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        if (value == null) {
            data.remove(namespacedKey);
        }else {
            data.set(namespacedKey, PersistentDataType.STRING, value);
        }
    }

    public void delete() {
        CustomBlockData data = new CustomBlockData(core.getBlock(), plugin);
        data.clear();
    }

    public Location getCore() {
        return core;
    }
}
