package me.none030.mortisstructures.structure;

import com.palmergames.bukkit.towny.TownyAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.none030.mortisstructures.MortisStructures;
import me.none030.mortisstructures.data.StructureData;
import me.none030.mortisstructures.manager.Manager;
import me.none030.mortisstructures.structure.mob.Mob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Structure {

    private final MortisStructures plugin = MortisStructures.getInstance();
    private final String id;
    private final Clipboard clipboard;
    private final StructureType type;
    private final World world;
    private final Location location1;
    private final Location location2;
    private final boolean unbreakable;
    private final int spawns;
    private final int interval;
    private final int despawn;
    private BlockVector3 radius;
    private final int tries;
    private final StructureChecks checks;
    private final List<Mob> mobs;
    private final List<String> commandsOnSpawn;
    private final List<String> commandsOnDeSpawn;

    public Structure(String id, Clipboard clipboard, StructureType type, World world, Location location1, Location location2, boolean unbreakable, int spawns, int interval, int despawn, int tries, StructureChecks checks, List<Mob> mobs, List<String> commandsOnSpawn, List<String> commandsOnDeSpawn) {
        this.id = id;
        this.clipboard = clipboard;
        this.type = type;
        this.world = world;
        this.location1 = location1;
        this.location2 = location2;
        this.unbreakable = unbreakable;
        this.spawns = spawns;
        this.interval = interval;
        this.despawn = despawn;
        this.tries = tries;
        this.checks = checks;
        this.mobs = mobs;
        this.commandsOnSpawn = commandsOnSpawn;
        this.commandsOnDeSpawn = commandsOnDeSpawn;
        center();
    }

    private void center() {
        BlockVector3 min = getClipboard().getMinimumPoint();
        BlockVector3 max = getClipboard().getMaximumPoint();
        double y = getClipboard().getMinY();

        double minX = Math.min(min.getX(), max.getX());
        double maxX = Math.max(min.getX(), max.getX());
        double minY = Math.min(min.getY(), max.getY());
        double maxY = Math.max(min.getY(), max.getY());
        double minZ = Math.min(min.getZ(), max.getZ());
        double maxZ = Math.max(min.getZ(), max.getZ());

        BlockVector3 difference = BlockVector3.at(maxX - minX, maxY - minY, maxZ - minZ);
        double halfX = difference.getX() * 0.5;
        double halfY = difference.getY() * 0.5;
        double halfZ = difference.getZ() * 0.5;
        setRadius(BlockVector3.at(Math.round(halfX), Math.round(halfY), Math.round(halfZ)));

        getClipboard().setOrigin(BlockVector3.at(minX + halfX, y, minZ + halfZ));
    }

    public void build(Manager manager) {
        for (int i = 0; i < spawns; i++) {
            Location location = getRandomLocation();
            if (location == null) {
                continue;
            }
            manager.add(location);
            BlockVector3 paste = BlockVector3.at(location.getX(), location.getY(), location.getZ());
            storeData(paste);
            try (EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(getWorld()))) {
                Operation operation = new ClipboardHolder(getClipboard())
                        .createPaste(session)
                        .to(paste)
                        .ignoreAirBlocks(false)
                        .build();
                Operations.complete(operation);
            }
            if (mobs != null) {
                for (Mob mob : mobs) {
                    Location loc = new Location(world, location.getX(), location.getY() + getRadius().getY(), location.getZ());
                    mob.spawn(loc);
                }
            }
            for (String line : commandsOnSpawn) {
                String command = line.replace("%world%", world.getName()).replace("%x%", String.valueOf(location.getBlockX())).replace("%y%", String.valueOf(location.getBlockY())).replace("%z%", String.valueOf(location.getBlockZ()));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }

    public void build(Manager manager, Location location) {
        for (int i = 0; i < spawns; i++) {
            manager.add(location);
            BlockVector3 paste = BlockVector3.at(location.getX(), location.getY(), location.getZ());
            storeData(paste);
            try (EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(getWorld()))) {
                Operation operation = new ClipboardHolder(getClipboard())
                        .createPaste(session)
                        .to(paste)
                        .ignoreAirBlocks(true)
                        .build();
                Operations.complete(operation);
            }
            if (mobs != null) {
                for (Mob mob : mobs) {
                    Location loc = new Location(world, location.getX(), location.getY() + getRadius().getY(), location.getZ());
                    mob.spawn(loc);
                }
            }
            for (String line : commandsOnSpawn) {
                String command = line.replace("%world%", world.getName()).replace("%x%", String.valueOf(location.getBlockX())).replace("%y%", String.valueOf(location.getBlockY())).replace("%z%", String.valueOf(location.getBlockZ()));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }
    }

    public void deSpawn(UUID uuid, BlockVector3 center) {
        int x1 = center.getX();
        int y1 = center.getY() + getRadius().getY();
        int z1 = center.getZ();
        for (int x = x1 - getRadius().getX(); x <= x1 + getRadius().getX(); x++) {
            for (int y = y1 - getRadius().getY(); y <= y1 + getRadius().getY(); y++) {
                for (int z = z1 - getRadius().getZ(); z <= z1 + getRadius().getZ(); z++) {
                    Block block = new Location(getWorld(), x, y, z).getBlock();
                    StructureData data = new StructureData(block.getLocation());
                    if (data.getUUID().equals(uuid)) {
                        block.setBlockData(data.getBlockData());
                        data.delete();
                    }
                }
            }
        }
    }

    private void storeData(BlockVector3 center) {
        int x1 = center.getX();
        int y1 = center.getY() + getRadius().getY();
        int z1 = center.getZ();
        LocalDateTime time = LocalDateTime.now().plusDays(getDespawn());
        UUID uuid = UUID.randomUUID();
        for (int x = x1 - getRadius().getX(); x <= x1 + getRadius().getX(); x++) {
            for (int y = y1 - getRadius().getY(); y <= y1 + getRadius().getY(); y++) {
                for (int z = z1 - getRadius().getZ(); z <= z1 + getRadius().getZ(); z++) {
                    Block block = new Location(getWorld(), x, y, z).getBlock();
                    StructureData data = new StructureData(block.getLocation());
                    data.create(uuid, id, center, block.getBlockData(), time);
                }
            }
        }
    }

    private Location getRandomLocation() {
        Random random = new Random();
        double minX = Math.min(getLocation1().getX(), getLocation2().getX());
        double maxX = Math.max(getLocation1().getX(), getLocation2().getX());
        double minY = Math.min(getLocation1().getY(), getLocation2().getY());
        double maxY = Math.max(getLocation1().getY(), getLocation2().getY());
        double minZ = Math.min(getLocation1().getZ(), getLocation2().getZ());
        double maxZ = Math.max(getLocation1().getZ(), getLocation2().getZ());
        for (int i = 0; i < getTries(); i++) {
            double x1 = random.nextDouble(minX, maxX);
            double y1 = random.nextDouble(minY, maxY);
            double z1 = random.nextDouble(minZ, maxZ);

            Location loc = new Location(getWorld(), x1, y1, z1);
            Location location = null;
            if (getType().equals(StructureType.SKY)) {
                location = setInSky(loc);
            }
            if (getType().equals(StructureType.GROUND)) {
                location = setOnGround(loc);
            }
            if (getType().equals(StructureType.UNDERGROUND)) {
                location = setInGround(loc);
            }
            if (location == null) {
                return null;
            }
            List<Location> locations = new ArrayList<>();
            for (double x = x1 - getRadius().getX(); x <= x1 + getRadius().getX(); x++) {
                for (double y = y1 - getRadius().getY(); y <= y1 + getRadius().getY(); y++) {
                    for (double z = z1 - getRadius().getZ(); z <= z1 + getRadius().getZ(); z++) {
                        locations.add(new Location(getWorld(), x, y, z));
                    }
                }
            }
            if (getChecks().hasTown()) {
                if (plugin.hasTowny()) {
                    if (isInTown(locations)) {
                        continue;
                    }
                    if (isTownInRange(location)) {
                        continue;
                    }
                }
            }
            if (getChecks().hasWater()) {
                if (isInWater(locations)) {
                    continue;
                }
            }
            if (getChecks().hasLava()) {
                if (isInLava(locations)) {
                    continue;
                }
            }
            if (getChecks().getMustHaveBlocks() != null) {
                if (isInBlocks(locations)) {
                    continue;
                }
            }
            if (getChecks().getMustNotHaveBlocks() != null) {
                if (isNotInBlocks(locations)) {
                    continue;
                }
            }
            if (getChecks().getMustHaveBiomes() != null) {
                if (isInBiomes(locations)) {
                    continue;
                }
            }
            if (getChecks().getMustNotHaveBiomes() != null) {
                if (isNotInBiomes(locations)) {
                    continue;
                }
            }
            return location;
        }
        return null;
    }

    private boolean isInBlocks(List<Location> locations) {
        List<Material> blocks = checks.getMustHaveBlocks();
        for (Location location : locations) {
            if (!blocks.contains(location.getBlock().getType())) {
                continue;
            }
            blocks.remove(location.getBlock().getType());
        }
        return blocks.size() == 0;
    }

    private boolean isNotInBlocks(List<Location> locations) {
        List<Material> blocks = checks.getMustNotHaveBlocks();
        for (Location location : locations) {
            if (blocks.contains(location.getBlock().getType())) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isInWater(List<Location> locations) {
        for (Location location : locations) {
            if (location.getBlock().getType().equals(Material.WATER)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInLava(List<Location> locations) {
        for (Location location : locations) {
            if (location.getBlock().getType().equals(Material.LAVA)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInBiomes(List<Location> locations) {
        List<Biome> biomes = checks.getMustHaveBiomes();
        for (Location location : locations) {
            if (!biomes.contains(location.getBlock().getBiome())) {
                continue;
            }
            biomes.remove(location.getBlock().getBiome());
        }
        return biomes.size() == 0;
    }

    private boolean isNotInBiomes(List<Location> locations) {
        List<Biome> biomes = checks.getMustNotHaveBiomes();
        for (Location location : locations) {
            if (biomes.contains(location.getBlock().getBiome())) {
                return true;
            }
        }
        return false;
    }

    private Location setOnGround(Location location) {
        double minY = Math.min(getLocation1().getY(), getLocation2().getY());
        double maxY = Math.max(getLocation1().getY(), getLocation2().getY());
        Location loc = location.getWorld().getHighestBlockAt(location).getLocation();
        if (loc.getY() >= minY && loc.getY() <= maxY) {
            return loc;
        }
        return null;
    }

    private Location setInSky(Location location) {
        Random random = new Random();
        int minY = location.getWorld().getHighestBlockYAt(location);
        int maxY = Math.max(getLocation1().getBlockY(), getLocation2().getBlockY());
        int mid = random.nextInt(minY, maxY);
        location.setY(mid);
        Block block = location.getBlock();
        if (block.isEmpty()) {
            return location;
        }
        return null;
    }

    private Location setInGround(Location location) {
        int minY = Math.min(getLocation1().getBlockY(), getLocation2().getBlockY());
        int maxY = Math.max(getLocation1().getBlockY(), getLocation2().getBlockY());
        for (int y = minY; y < maxY; y++) {
            location.setY(y);
            Block block = location.getBlock();
            if (block.isEmpty()) {
                return location;
            }
        }
        return null;
    }

    private boolean isTownInRange(Location location) {
        int radius = checks.getTownRange();
        TownyAPI town = TownyAPI.getInstance();
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (int y = location.getWorld().getMinHeight(); y <= location.getWorld().getMaxHeight(); y++) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockX() + radius; z++) {
                    Location loc = new Location(location.getWorld(), x, y, z);
                    if (!town.isWilderness(loc)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isInTown(List<Location> locations) {
        TownyAPI town = TownyAPI.getInstance();
        for (Location location : locations) {
            if (!town.isWilderness(location)) {
                return true;
            }
        }
        return false;
    }

    public LocalDateTime getNextSpawn() {
        return LocalDateTime.now().plusDays(interval);
    }

    public void setRadius(BlockVector3 radius) {
        this.radius = radius;
    }

    public StructureChecks getChecks() {
        return checks;
    }

    public BlockVector3 getRadius() {
        return radius;
    }

    public String getId() {
        return id;
    }

    public Clipboard getClipboard() {
        return clipboard;
    }

    public StructureType getType() {
        return type;
    }

    public World getWorld() {
        return world;
    }

    public Location getLocation1() {
        return location1;
    }

    public Location getLocation2() {
        return location2;
    }

    public int getInterval() {
        return interval;
    }

    public int getDespawn() {
        return despawn;
    }

    public int getTries() {
        return tries;
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }

    public int getSpawns() {
        return spawns;
    }

    public List<Mob> getMobs() {
        return mobs;
    }

    public List<String> getCommandsOnSpawn() {
        return commandsOnSpawn;
    }

    public List<String> getCommandsOnDespawn() {
        return commandsOnDeSpawn;
    }
}
