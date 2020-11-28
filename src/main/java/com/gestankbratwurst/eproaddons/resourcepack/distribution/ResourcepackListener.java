package com.gestankbratwurst.eproaddons.resourcepack.distribution;

import com.gestankbratwurst.eproaddons.util.Msg;
import com.gestankbratwurst.eproaddons.util.tasks.TaskManager;
import java.util.UUID;
import java.util.function.BiConsumer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of avarioncore and was created at the 25.11.2019
 *
 * avarioncore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class ResourcepackListener implements Listener {

  public ResourcepackListener(final BiConsumer<Player, Status> packResultConsumer, final ResourcepackManager manager) {
    this.packResultConsumer = packResultConsumer;
    this.manager = manager;
    this.taskManager = TaskManager.getInstance();
  }

  private final BiConsumer<Player, Status> packResultConsumer;
  private final ResourcepackManager manager;
  private final TaskManager taskManager;

  @EventHandler(priority = EventPriority.HIGH)
  public void onJoin(final PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    this.taskManager.runBukkitSyncDelayed(() -> this.sendResourcepack(player), 20L);
  }

  @EventHandler
  public void resourceStatusEvent(final PlayerResourcePackStatusEvent event) {
    final Player player = event.getPlayer();
    final UUID id = player.getUniqueId();
    final Status status = event.getStatus();
    if (status == Status.SUCCESSFULLY_LOADED) {
      Msg.send(player, "Resourcepack", "Das Resourcepack wurde akzeptiert.");
    } else if (status == Status.FAILED_DOWNLOAD) {
      // TODO enable on production server
//      if (attempts.contains(id)) {
//        attempts.remove(id);
//        player.kickPlayer("Bitte akzeptiere das Resourcepack.");
//      } else {
//        attempts.add(id);
//        plugin.getTaskManager().runBukkitSyncDelayed(() -> sendResourcepack(player), 100L);
//      }
    } else if (status == Status.DECLINED) {
      Msg.send(player, "Resourcepack", "Das Resourcepack wurde abgelehnt.");
      Msg.send(player, "Resourcepack", "Einige Funktionen werden eingeschränkt!");
      Msg.send(player, "Resourcepack", "Das Resourcepack ersetzt NICHT dein jetziges.");
    }
    this.packResultConsumer.accept(player, status);
  }

  private void sendResourcepack(final Player player) {
    player.setResourcePack(this.manager.getDownloadURL(), this.manager.getResourceHash());
  }

}