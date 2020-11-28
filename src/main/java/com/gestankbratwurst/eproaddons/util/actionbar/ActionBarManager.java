package com.gestankbratwurst.eproaddons.util.actionbar;

import com.gestankbratwurst.eproaddons.util.tasks.TaskManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of avarioncore and was created at the 23.03.2020
 *
 * LaLaLand-CorePlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class ActionBarManager {

  public ActionBarManager(final JavaPlugin plugin) {
    this.boardMap = new Object2ObjectOpenHashMap<>();
    this.taskManager = TaskManager.getInstance();
    for (final Player player : Bukkit.getOnlinePlayers()) {
      this.init(player);
    }
    Bukkit.getPluginManager().registerEvents(new ActionBarListener(this), plugin);
    this.taskManager
        .runRepeatedBukkit(new ActionBarUpdateThread(this), 0L,
            ActionBarUpdateThread.UPDATE_PERIOD);
  }

  private final Object2ObjectOpenHashMap<Player, ActionBarBoard> boardMap;
  @Getter(AccessLevel.PROTECTED)
  private final TaskManager taskManager;

  public ActionBarBoard getBoard(final Player player) {
    return this.boardMap.get(player);
  }

  protected void init(final Player player) {
    this.boardMap.put(player, new ActionBarBoard(player.getUniqueId(), this));
  }

  protected void terminate(final Player player) {
    this.boardMap.remove(player);
  }

  public void showTo(final Player player) {
    player.sendActionBar(this.boardMap.get(player).getCurrentDisplay());
  }

  public void updateAndShow(final Player player) {
    final ActionBarBoard board = this.boardMap.get(player);
    board.update();
    player.sendActionBar(board.getCurrentDisplay());
  }

  protected void updateAndShowAll() {
    for (final Player player : Bukkit.getOnlinePlayers()) {
      this.updateAndShow(player);
    }
  }

  protected void showToAll() {
    for (final Player player : Bukkit.getOnlinePlayers()) {
      this.showTo(player);
    }
  }

  public void update(final Player player) {
    this.boardMap.get(player).update();
  }

  protected void updateAll() {
    for (final Player player : Bukkit.getOnlinePlayers()) {
      this.update(player);
    }
  }

}
