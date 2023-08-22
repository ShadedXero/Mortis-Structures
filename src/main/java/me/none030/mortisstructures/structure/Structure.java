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
import me.none030.mortisstructures.data.DataManager;
import me.none030.mortisstructures.data.StructureBlockData;
import me.none030.mortisstructures.data.StructureData;
import me.none030.mortisstructures.structure.mob.Mob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.time.LocalDateTime;
import java.util.*;

public class Structure {

    private final MortisStructures plugin = MortisStructures.getInstance();
    private final String id;
    private final Clipboard clipboard;
    private final StructureType type;
    private final StructureWorld world;
    private final StructureLocation location1;
    private final StructureLocation location2;
    private final boolean unbreakable;
    private final int spawns;
    private final int interval;
    private final int despawn;
    private BlockVector3 radius;
    private final int tries;
    private final Material replacement;
    private final StructureChecks checks;
    private final List<Mob> mobs;
    private final List<String> commandsOnSpawn;
    private final List<String> commandsOnDeSpawn;

    public Structure(String id, Clipboard clipboard, StructureType type, StructureWorld world, StructureLocation location1, StructureLocation location2, boolean unbreakable, int spawns, int interval, int despawn, int tries, Material replacement, StructureChecks checks, List<Mob> mobs, List<String> commandsOnSpawn, List<String> commandsOnDeSpawn) {
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
        this.replacement = replacement;
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

    private void build(DataManager dataManager, BlockVector3 pasteVector) {
        List<StructureLocation> locations = storeData(dataManager, pasteVector);
        try (EditSession session = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(getWorld().getWorld()))) {
            Operation operation = new ClipboardHolder(getClipboard())
                    .createPaste(session)
                    .to(pasteVector)
                    .ignoreAirBlocks(true)
                    .build();
            Operations.complete(operation);
        }
        replace(locations);
        if (mobs != null) {
            for (Mob mob : mobs) {
                Location loc = new Location(world.getWorld(), pasteVector.getX(), pasteVector.getY() + getRadius().getY(), pasteVector.getZ());
                mob.spawn(loc);
            }
        }
        for (String line : commandsOnSpawn) {
            String command = line.replace("%world%", world.getWorldName()).replace("%x%", String.valueOf(pasteVector.getBlockX())).replace("%y%", String.valueOf(pasteVector.getBlockY())).replace("%z%", String.valueOf(pasteVector.getBlockZ()));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    public void build(DataManager dataManager) {
        for (int i = 0; i < spawns; i++) {
            Location location = getRandomLocation();
            if (location == null) {
                continue;
            }
            BlockVector3 pasteLoc = BlockVector3.at(location.getX(), location.getY(), location.getZ());
            build(dataManager, pasteLoc);
        }
    }

    public void build(DataManager dataManager, Location location) {
        for (int i = 0; i < spawns; i++) {
            BlockVector3 pasteLoc = BlockVector3.at(location.getX(), location.getY(), location.getZ());
            build(dataManager, pasteLoc);
        }
    }

    private List<StructureLocation> storeData(DataManager dataManager, BlockVector3 center) {
        UUID uuid = UUID.randomUUID();
        dataManager.addStructure(new StructureData(uuid, id, getNextDespawn(), center));
        List<StructureLocation> locations = new ArrayList<>();
        int x1 = center.getX();
        int y1 = center.getY() + getRadius().getY();
        int z1 = center.getZ();
        for (int x = x1 - getRadius().getX(); x <= x1 + getRadius().getX(); x++) {
            for (int y = y1 - getRadius().getY(); y <= y1 + getRadius().getY(); y++) {
                for (int z = z1 - getRadius().getZ(); z <= z1 + getRadius().getZ(); z++) {
                    Block block = new Location(getWorld().getWorld(), x, y, z).getBlock();
                    StructureLocation location = new StructureLocation(block.getLocation());
                    locations.add(location);
                    StructureBlockData structureBlock = new StructureBlockData(uuid, location, block.getBlockData());
                    dataManager.addStructureBlock(structureBlock);
                }
            }
        }
        return locations;
    }

    private void replace(List<StructureLocation> locations) {
        for (StructureLocation structureLocation : locations) {
            Location location = structureLocation.getLocation();
            if (location == null) {
                continue;
            }
            Material type = location.getBlock().getType();
            if (type.equals(replacement)) {
                location.getBlock().setBlockData(Material.AIR.createBlockData());
            }
        }
    }

    public void despawn(DataManager dataManager, StructureData structureData) {
        UUID uuid = structureData.getUuid();
        List<StructureBlockData> structureBlocks = dataManager.getStructureBlockData(uuid);
        for (StructureBlockData structureBlock : structureBlocks) {
            Location location = structureBlock.getLocation().getLocation();
            if (location == null) {
               continue;
            }
            location.getBlock().setBlockData(structureBlock.getBlockData());
        }
        dataManager.removeStructure(uuid);
    }

    private Location getRandomLocation() {
        Random random = new Random();
        double minX = Math.min(getLocation1().getX(), getLocation2().getX());
        double maxX = Math.max(getLocation1().getX(), getLocation2().getX());
        double minY = Math.min(getLocation1().getY(), getLocation2().getY());
        double maxY = Math.max(getLocation1().getY(), getLocation2().getY());
        double minZ = Math.min(getLocation1().getZ(), getLocation2().getZ());
        double maxZ = Math.max(getLocation1().getZ(), getLocation2().getZ());
        for (int i = 0; i < tries; i++) {
            double x = random.nextDouble(minX, maxX);
            double y = random.nextDouble(minY, maxY);
            double z = random.nextDouble(minZ, maxZ);
            Location location = new Location(getWorld().getWorld(), x, y, z).getBlock().getLocation();
            switch (type) {
                case SKY:
                    location = setSky(location);
                    break;
                case GROUND:
                    location = setGround(location);
                    break;
                case UNDERGROUND:
                    location = setUnderground(location);
                    break;
            }
            if (!hasRequirements(location)) {
                continue;
            }
            return location;
        }
        return null;
    }

    private boolean hasRequirements(Location center) {
        boolean hasMustBlocks = checks.getMustHaveBlocks() == null || checks.getMustHaveBlocks().isEmpty();
        boolean hasMustNotBlocks = checks.getMustNotHaveBlocks() == null || checks.getMustNotHaveBlocks().isEmpty();
        boolean hasWater = !checks.hasWater();
        boolean hasLava = !checks.hasLava();
        boolean hasMustBiomes = checks.getMustHaveBiomes() == null || checks.getMustHaveBiomes().isEmpty();
        boolean hasMustNotBiomes = checks.getMustNotHaveBiomes() == null || checks.getMustNotHaveBiomes().isEmpty();
        boolean hasTown = !checks.hasTown();
        boolean hasTownInRange = checks.getTownRange() <= 0;
        if (!hasTownInRange) {
            if (plugin.hasTowny()) {
                int radius = checks.getTownRange();
                for (int x = center.getBlockX() - radius; x <= center.getBlockX() + radius; x++) {
                    for (int y = center.getWorld().getMinHeight(); y <= center.getWorld().getMaxHeight(); y++) {
                        for (int z = center.getBlockZ() - radius; z <= center.getBlockZ() + radius; z++) {
                            Location loc = new Location(center.getWorld(), x, y, z);
                            if (!TownyAPI.getInstance().isWilderness(loc)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        List<Material> mustBlocks;
        if (hasMustBlocks) {
            mustBlocks = new ArrayList<>();
        }else {
            mustBlocks = new ArrayList<>(checks.getMustHaveBlocks());
        }
        List<Material> mustNotBlocks;
        if (hasMustNotBlocks) {
            mustNotBlocks = new ArrayList<>();
        }else {
            mustNotBlocks = new ArrayList<>(checks.getMustNotHaveBlocks());
        }
        List<Biome> mustBiomes;
        if (hasMustBiomes) {
            mustBiomes = new ArrayList<>();
        }else {
            mustBiomes = new ArrayList<>(checks.getMustHaveBiomes());
        }
        List<Biome> mustNotBiomes;
        if (hasMustNotBiomes) {
            mustNotBiomes = new ArrayList<>();
        }else {
            mustNotBiomes = new ArrayList<>(checks.getMustNotHaveBiomes());
        }
        for (double x = center.getX() - getRadius().getX(); x <= center.getX() + getRadius().getX(); x++) {
            for (double y = center.getY() - getRadius().getY(); y <= center.getY() + getRadius().getY(); y++) {
                for (double z = center.getZ() - getRadius().getZ(); z <= center.getZ() + getRadius().getZ(); z++) {
                    Location location = new Location(center.getWorld(), x, y, z);
                    Material material = location.getBlock().getType();
                    if (!hasWater) {
                        if (material.equals(Material.WATER)) {
                            return false;
                        }
                    }
                    if (!hasLava) {
                        if (material.equals(Material.LAVA)) {
                            return false;
                        }
                    }
                    if (plugin.hasTowny()) {
                        if (!hasTown) {
                            if (!TownyAPI.getInstance().isWilderness(location)) {
                                return false;
                            }
                        }
                    }
                    if (!hasMustNotBlocks) {
                        if (mustNotBlocks.contains(material)) {
                            return false;
                        }
                    }
                    Biome biome = location.getBlock().getBiome();
                    if (!hasMustNotBiomes) {
                        if (mustNotBiomes.contains(biome)) {
                            return false;
                        }
                    }
                    if (!hasMustBlocks) {
                        mustBlocks.remove(material);
                        if (mustBlocks.isEmpty()) {
                            hasMustBlocks = true;
                        }
                    }
                    if (!hasMustBiomes) {
                        mustBiomes.remove(biome);
                        if (mustBiomes.isEmpty()) {
                            hasMustBiomes = true;
                        }
                    }
                }
            }
        }
        return hasMustBlocks && hasMustBiomes;
    }

    private Location setGround(Location location) {
        double minY = Math.min(getLocation1().getY(), getLocation2().getY()) - 1;
        double maxY = Math.max(getLocation1().getY(), getLocation2().getY());
        Location loc = location.getWorld().getHighestBlockAt(location).getLocation();
        if (loc.getY() >= minY && loc.getY() <= maxY) {
            return loc;
        }
        return null;
    }

    private Location setSky(Location location) {
        Random random = new Random();
        int minY = location.getWorld().getHighestBlockYAt(location);
        int maxY = Math.max(getLocation1().getLocation().getBlockY(), getLocation2().getLocation().getBlockY()) - 1;
        int mid = random.nextInt(minY, maxY);
        location.setY(mid);
        Block block = location.getBlock();
        if (block.isEmpty()) {
            return location;
        }
        return null;
    }

    private Location setUnderground(Location location) {
        int minY = Math.min(getLocation1().getLocation().getBlockY(), getLocation2().getLocation().getBlockY());
        int maxY = Math.max(getLocation1().getLocation().getBlockY(), getLocation2().getLocation().getBlockY());
        for (int y = minY; y <= maxY; y++) {
            location.setY(y);
            Block block = location.getBlock();
            if (block.isEmpty()) {
                return location;
            }
        }
        return null;
    }

    public LocalDateTime getNextSpawn() {
        return LocalDateTime.now().plusDays(interval);
    }

    public LocalDateTime getNextDespawn() {
        return LocalDateTime.now().plusDays(despawn);
    }

    public void setRadius(BlockVector3 radius) {
        this.radius = radius;
    }

    public Material getReplacement() {
        return replacement;
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

    public StructureWorld getWorld() {
        return world;
    }

    public StructureLocation getLocation1() {
        return location1;
    }

    public StructureLocation getLocation2() {
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
