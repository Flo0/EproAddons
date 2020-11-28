package com.gestankbratwurst.eproaddons.addons.christmas.christmasboss;

import java.util.function.Predicate;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
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
public class SnowMeteor implements SnowBossTask {
  
  private final Location currentLoc;
  private final Vector velocity;
  private final ArmorStand blockMarker;
  private final Predicate<LivingEntity> filter;
  private boolean hit = false;

  public SnowMeteor(final Location startLoc, final Predicate<LivingEntity> filter) {
    this.filter = filter;
    this.currentLoc = startLoc.clone().add(0, 40, 0);
    this.velocity = new Vector(0, -0.33, 0);
    this.blockMarker = this.currentLoc.getWorld().spawn(this.currentLoc, ArmorStand.class);
    this.blockMarker.setMarker(true);
    this.blockMarker.setVisible(false);
    this.blockMarker.setInvulnerable(true);
    this.blockMarker.getEquipment().setHelmet(new ItemStack(Material.SNOW_BLOCK));
  }

  private void move() {
    this.currentLoc.add(this.velocity);
    this.blockMarker.teleport(this.currentLoc);
  }

  private void spawnParticle() {
    final Location particleLoc = this.currentLoc.clone().add(0, 4, 0);
    final DustOptions dustOptions = new DustOptions(Color.WHITE, 1.2F);
    this.currentLoc.getWorld().spawnParticle(Particle.CLOUD, particleLoc, 2, 0.1, 0.1, 0.1, 0);
    this.currentLoc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc.clone().add(0, 0.5, 0), 4, 0.1, 1.5, 0.1, 0, dustOptions);
  }

  private void checkHit() {
    if (this.currentLoc.getBlock().getType() != Material.AIR) {
      this.hit = true;
      this.impact();
    }
  }

  private void impact() {
    final World world = this.currentLoc.getWorld();
    world.spawnParticle(Particle.EXPLOSION_LARGE, this.currentLoc, 1);
    world.spawnParticle(Particle.EXPLOSION_NORMAL, this.currentLoc, 1);
    world.spawnParticle(Particle.FIREWORKS_SPARK, this.currentLoc, 16, 1, 1, 1, 0.3);
    world.playSound(this.currentLoc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.5F, 1.2F);
    this.currentLoc.getNearbyLivingEntities(5, this.filter).forEach(hit -> {
      hit.damage(22);
      hit.setVelocity(new Vector(0, 1.5, 0));
    });
    this.blockMarker.remove();
  }

  @Override
  public boolean tick() {
    this.spawnParticle();
    this.move();
    this.checkHit();
    return this.hit;
  }
}
