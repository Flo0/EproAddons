package com.gestankbratwurst.eproaddons.addons.christmas.christmasboss;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of EproAddons and was created at the 26.11.2020
 *
 * EproAddons can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
@RequiredArgsConstructor
public class SnowBossListener implements Listener {

  private final SnowBossManager snowBossManager;

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDamage(final EntityDamageEvent event) {
    final UUID targetID = event.getEntity().getUniqueId();
    this.snowBossManager.getBossOf(targetID).ifPresent(boss -> {
      final double dmg = event.getFinalDamage();
      boss.damage(dmg);
      event.setDamage(0D);
    });
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDeath(final EntityDeathEvent event) {
    final UUID targetID = event.getEntity().getUniqueId();
    this.snowBossManager.getBossOf(targetID).ifPresent(SnowBoss::clear);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onUnload(final ChunkUnloadEvent event) {
    for (final Entity entity : event.getChunk().getEntities()) {
      final UUID targetID = entity.getUniqueId();
      this.snowBossManager.getBossOf(targetID).ifPresent(SnowBoss::clear);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onLogout(final PlayerQuitEvent event) {
    this.snowBossManager.forEach(boss -> boss.removePlayerFromBossBar(event.getPlayer()));
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJoin(final PlayerJoinEvent event) {
    this.snowBossManager.forEach(boss -> boss.addPlayerToBossBar(event.getPlayer()));
  }

}
