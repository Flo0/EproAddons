package com.gestankbratwurst.eproaddons.playerdata;

import com.gestankbratwurst.eproaddons.io.EproFileManager;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of EproAddons and was created at the 22.11.2020
 *
 * EproAddons can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class AddonPlayerManager {

  private final EproFileManager eproFileManager;
  private final Map<UUID, AddonPlayer> addonPlayerMap;

  public AddonPlayerManager(final EproFileManager eproFileManager) {
    this.eproFileManager = eproFileManager;
    this.addonPlayerMap = new HashMap<>();
  }

  public AddonPlayer getPlayer(final UUID playerID) {
    return this.addonPlayerMap.get(playerID);
  }

  protected void initPlayer(final AddonPlayer addonPlayer) {
    this.addonPlayerMap.put(addonPlayer.getPlayerID(), addonPlayer);
  }

  protected AddonPlayer loadPlayer(final UUID playerID) {
    AddonPlayer addonPlayer = this.eproFileManager.loadPlayer(playerID);
    if (addonPlayer == null) {
      addonPlayer = new AddonPlayer(playerID);
    }
    return addonPlayer;
  }

  protected void outPlayer(final UUID playerID) {
    this.savePlayer(playerID);
    this.addonPlayerMap.remove(playerID);
  }

  protected void savePlayer(final UUID playerID) {
    final AddonPlayer addonPlayer = this.addonPlayerMap.get(playerID);
    if (addonPlayer != null) {
      CompletableFuture.runAsync(() -> this.eproFileManager.savePlayer(addonPlayer));
    }
  }

}
