package com.gestankbratwurst.eproaddons.addons.customitems;

import com.gestankbratwurst.eproaddons.util.common.UtilLoc;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Snow;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of EproAddons and was created at the 23.11.2020
 *
 * EproAddons can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class SnowBombTask implements CustomItemTask {

  public SnowBombTask(final Location location) {
    this.location = location;
    this.snowPlaced = false;
    this.random = ThreadLocalRandom.current();
    this.snowLocations = UtilLoc.generateSphere(location, 7, false)
        .stream()
        .filter(loc -> loc.getBlock().getType() == Material.AIR)
        .collect(Collectors.toList());
    int snAmount = this.snowLocations.size() / 100;
    if (snAmount <= 0) {
      snAmount = 1;
    }
    this.amounts = snAmount;
    this.size = this.snowLocations.size();
  }

  private final int size;
  private final int amounts;
  private final List<Location> snowLocations;
  private final ThreadLocalRandom random;
  private final Location location;
  private boolean snowPlaced;
  private int ticksAlive = 0;

  @Override
  public boolean isDone() {
    return this.ticksAlive == 600;
  }

  @Override
  public void tick() {
    if (!this.snowPlaced) {
      this.placeSnow();
    }
    this.playParticles();
    this.ticksAlive++;
  }

  private void playParticles() {
    final World world = this.location.getWorld();
    for (int i = 0; i < this.amounts; i++) {
      final Location loc = this.snowLocations.get(this.random.nextInt(this.size));
      world.spawnParticle(Particle.FIREWORKS_SPARK, loc, 1, 0.9, 0.9, 0.9, 0);
    }
  }

  private void placeSnow() {

    final Set<Block> blocks = UtilLoc.getBlocksInSphere(this.location, 6, false);

    for (final Block block : blocks) {
      if (block.getType() != Material.AIR && block.getType().isSolid()) {
        final Block relUp = block.getRelative(BlockFace.UP);
        if (relUp.getType() == Material.AIR) {
          this.placeRandomSnow(relUp);
        }
      }
    }

    this.snowPlaced = true;
  }

  private void placeRandomSnow(final Block block) {
    block.setType(Material.SNOW);
    final BlockData data = block.getBlockData();
    if (!(data instanceof Snow)) {
      return;
    }
    final Snow snow = (Snow) block.getBlockData();
    snow.setLayers(this.random.nextInt(snow.getMinimumLayers(), snow.getMaximumLayers() + 1));
    block.getState().update(true);
  }

}
