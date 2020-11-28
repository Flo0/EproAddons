package com.gestankbratwurst.eproaddons.addons.christmas.christmasboss;

import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of EproAddons and was created at the 27.11.2020
 *
 * EproAddons can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
@RequiredArgsConstructor
public class SnowTornado implements SnowBossTask {

  private static final int MAX_TICKS_ALIVE = 140;

  private int ticksAlive = 0;
  private final Location startLoc;
  private final Vector velocity;
  private final Predicate<LivingEntity> filter;

  @Override
  public boolean tick() {
    this.ticksAlive++;
    this.playSound();
    if (this.ticksAlive % 3 == 0) {
      this.playEffect();
      this.scanTargets();
    }
    this.playDot();
    this.move();
    return this.ticksAlive >= MAX_TICKS_ALIVE;
  }

  private void move() {
    this.startLoc.add(this.velocity);
  }

  private void playEffect() {
    final World world = this.startLoc.getWorld();
    final Location spawnLoc = this.startLoc.clone().add(0, 1.2, 0);
    world.spawnParticle(Particle.SNOW_SHOVEL, spawnLoc, 28, 0.5, 2, 0.5, 0.1);
    world.spawnParticle(Particle.FIREWORKS_SPARK, spawnLoc, 10, 0.5, 2, 0.5, 0.1);
  }

  private void playDot() {
    final World world = this.startLoc.getWorld();
    final Location spawnLoc = this.startLoc.clone().add(0, 1.2, 0);
    final DustOptions dustOptions = new DustOptions(Color.fromRGB(204, 255, 255), 2F);
    world.spawnParticle(Particle.REDSTONE, spawnLoc, 4, 0, 2, 0, 0, dustOptions);
  }

  private void playSound() {
    this.startLoc.getWorld().playSound(this.startLoc, Sound.BLOCK_SNOW_BREAK, 0.8F, 0.5F);
  }

  private void scanTargets() {
    this.startLoc.getNearbyLivingEntities(2.5, this.filter).forEach(hit -> {
      hit.damage(22);
      hit.setVelocity(this.velocity.clone().multiply(4).add(new Vector(0, 1, 0)));
    });
  }


}
