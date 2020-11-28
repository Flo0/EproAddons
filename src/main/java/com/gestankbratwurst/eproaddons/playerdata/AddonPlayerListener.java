package com.gestankbratwurst.eproaddons.playerdata;

import com.gestankbratwurst.eproaddons.util.tasks.TaskManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of EproAddons and was created at the 22.11.2020
 *
 * EproAddons can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
@RequiredArgsConstructor
public class AddonPlayerListener implements Listener {

  private final AddonPlayerManager addonPlayerManager;

  @EventHandler
  public void onPreLogin(final AsyncPlayerPreLoginEvent event) {
    final AddonPlayer addonPlayer = this.addonPlayerManager.loadPlayer(event.getUniqueId());
    TaskManager.getInstance().completeBukkitSync(() -> this.addonPlayerManager.initPlayer(addonPlayer)).join();
  }

  @EventHandler
  public void onLogout(final PlayerQuitEvent event) {
    this.addonPlayerManager.outPlayer(event.getPlayer().getUniqueId());
  }

}