package me.none030.mortisstructure.utils;

import java.time.LocalDateTime;
import java.util.List;

public class DespawnStructure {

    private final String id;
    private final LocalDateTime time;
    private final List<StructureBlock> blocks;

    public DespawnStructure(String id, LocalDateTime time, List<StructureBlock> blocks) {
        this.id = id;
        this.time = time;
        this.blocks = blocks;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public List<StructureBlock> getBlocks() {
        return blocks;
    }
}
