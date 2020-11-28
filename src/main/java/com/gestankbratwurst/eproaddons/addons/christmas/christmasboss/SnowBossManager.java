package com.gestankbratwurst.eproaddons.addons.christmas.christmasboss;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of EproAddons and was created at the 26.11.2020
 *
 * EproAddons can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class SnowBossManager implements Iterable<SnowBoss> {

  @Getter(AccessLevel.PROTECTED)
  private static SnowBossManager instance;

  @Getter
  private final SnowBossThread snowBossThread;

  private final Map<UUID, SnowBoss> targetMap;

  public SnowBossManager(final JavaPlugin plugin) {
    instance = this;
    this.targetMap = new HashMap<>();
    this.snowBossThread = new SnowBossThread();
    Bukkit.getPluginManager().registerEvents(new SnowBossListener(this), plugin);
    Bukkit.getScheduler().runTaskTimer(plugin, this.snowBossThread, 1, 1);
  }

  protected void addSnowBoss(final SnowBoss snowBoss) {
    for (final UUID id : snowBoss.getTargets()) {
      this.targetMap.put(id, snowBoss);
    }
    this.snowBossThread.addTask(snowBoss);
  }

  protected void removeBoss(final SnowBoss snowBoss) {
    this.targetMap.keySet().removeAll(snowBoss.getTargets());
  }

  protected Optional<SnowBoss> getBossOf(final UUID targetID) {
    return Optional.ofNullable(this.targetMap.get(targetID));
  }

  @Override
  public Iterator<SnowBoss> iterator() {
    return this.targetMap.values().iterator();
  }

  @Override
  public void forEach(final Consumer<? super SnowBoss> action) {
    this.targetMap.values().forEach(action);
  }
}
