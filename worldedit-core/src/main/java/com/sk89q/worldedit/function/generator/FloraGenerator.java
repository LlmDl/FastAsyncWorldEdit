/*
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.sk89q.worldedit.function.generator;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extension.platform.Capability;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.internal.Constants;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;

/**
 * Generates flora (which may include tall grass, flowers, etc.).
 *
 * <p>The current implementation is not biome-aware, but it may become so in
 * the future.</p>
 */
public class FloraGenerator implements RegionFunction {

    private final EditSession editSession;
    private final boolean biomeAware = false;
    private final Pattern desertPattern = getDesertPattern();
    private final Pattern temperatePattern = getTemperatePattern();
    //FAWE start
    private final Pattern mushroomPattern = mushroomPattern();
    private final Pattern netherPattern = netherPattern();
    private final Pattern warpedNyliumPattern = warpedNyliumPattern();
    //FAWE end

    /**
     * Create a new flora generator.
     *
     * @param editSession the edit session
     */
    public FloraGenerator(EditSession editSession) {
        this.editSession = editSession;
    }

    /**
     * Return whether the flora generator is set to be biome-aware.
     *
     * <p>By default, it is currently disabled by default, but
     * this may change.</p>
     *
     * @return true if biome aware
     */
    public boolean isBiomeAware() {
        return biomeAware;
    }

    /**
     * Set whether the generator is biome aware.
     *
     * <p>It is currently not possible to make the generator biome-aware.</p>
     *
     * @param biomeAware must always be false
     */
    public void setBiomeAware(boolean biomeAware) {
        if (biomeAware) {
            throw new IllegalArgumentException("Cannot enable biome-aware mode; not yet implemented");
        }
    }

    /**
     * Get a pattern for plants to place inside a desert environment.
     *
     * @return a pattern that places flora
     */
    public static Pattern getDesertPattern() {
        RandomPattern pattern = new RandomPattern();
        pattern.add(BlockTypes.DEAD_BUSH.getDefaultState(), 30);
        pattern.add(BlockTypes.CACTUS.getDefaultState(), 20);
        pattern.add(BlockTypes.AIR.getDefaultState(), 300);
        return pattern;
    }

    /**
     * Get a pattern for plants to place inside a temperate environment.
     *
     * @return a pattern that places flora
     */
    public static Pattern getTemperatePattern() {
        RandomPattern pattern = new RandomPattern();
        pattern.add(BlockTypes.GRASS.getDefaultState(), 300);
        pattern.add(BlockTypes.POPPY.getDefaultState(), 5);
        pattern.add(BlockTypes.DANDELION.getDefaultState(), 5);
        return pattern;
    }

    //FAWE start
    /**
     * Get a pattern for plants to place inside a mushroom environment.
     *
     * @return a pattern that places flora
     */
    public static Pattern mushroomPattern() {
        RandomPattern pattern = new RandomPattern();
        pattern.add(BlockTypes.RED_MUSHROOM.getDefaultState(), 10);
        pattern.add(BlockTypes.BROWN_MUSHROOM.getDefaultState(), 10);
        return pattern;
    }

    /**
     * Get a pattern for plants to place inside a nether environment.
     *
     * @return a pattern that places flora
     */
    public static Pattern netherPattern() {
        RandomPattern pattern = new RandomPattern();
        pattern.add(BlockTypes.CRIMSON_ROOTS.getDefaultState(), 10);
        pattern.add(BlockTypes.CRIMSON_FUNGUS.getDefaultState(), 20);
        pattern.add(BlockTypes.WARPED_FUNGUS.getDefaultState(), 5);
        return pattern;
    }

    /**
     * Get a pattern for plants to place inside a nether environment.
     *
     * @return a pattern that places flora
     */
    public static Pattern warpedNyliumPattern() {
        RandomPattern pattern = new RandomPattern();
        pattern.add(BlockTypes.WARPED_ROOTS.getDefaultState(), 15);
        pattern.add(BlockTypes.NETHER_SPROUTS.getDefaultState(), 20);
        pattern.add(BlockTypes.WARPED_FUNGUS.getDefaultState(), 7);
        pattern.add(BlockTypes.CRIMSON_ROOTS.getDefaultState(), 10);
        return pattern;
    }
    //FAWE end

    @Override
    public boolean apply(BlockVector3 position) throws WorldEditException {
        //FAWE start
        int dataVersion = WorldEdit.getInstance().getPlatformManager().queryCapability(Capability.GAME_HOOKS).getDataVersion();
        //FAWE end
        BlockState block = editSession.getBlock(position);

        if (block.getBlockType() == BlockTypes.GRASS_BLOCK) {
            editSession.setBlock(position.add(0, 1, 0), temperatePattern.applyBlock(position));
            return true;
        //FAWE start - add red sand
        } else if (block.getBlockType() == BlockTypes.SAND || block.getBlockType() == BlockTypes.RED_SAND) {
        //FAWE end
            editSession.setBlock(position.add(0, 1, 0), desertPattern.applyBlock(position));
            return true;
        //FAWE start - add new types
        } else if (block.getBlockType() == BlockTypes.MYCELIUM || block.getBlockType() == BlockTypes.NETHERRACK) {
            editSession.setBlock(position.add(0, 1, 0), mushroomPattern.applyBlock(position));
            return true;
        } else if (dataVersion >= Constants.DATA_VERSION_MC_1_16) {
            if (block.getBlockType() == BlockTypes.SOUL_SOIL || block.getBlockType() == BlockTypes.CRIMSON_NYLIUM) {
                editSession.setBlock(position.add(0, 1, 0), netherPattern.applyBlock(position));
                return true;
            } else if (block.getBlockType() == BlockTypes.WARPED_NYLIUM) {
                editSession.setBlock(position.add(0, 1, 0), warpedNyliumPattern.applyBlock(position));
            }
        }
        //FAWE end

        return false;
    }

}
