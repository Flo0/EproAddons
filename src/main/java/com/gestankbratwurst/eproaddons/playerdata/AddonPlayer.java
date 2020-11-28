package com.gestankbratwurst.eproaddons.playerdata;

import com.gestankbratwurst.eproaddons.EproAddons;
import com.gestankbratwurst.eproaddons.addons.christmas.AdventCalendar;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of EproAddons and was created at the 22.11.2020
 *
 * EproAddons can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class AddonPlayer {

  public static AddonPlayer of(final Player player) {
    return EproAddons.getInstance().getAddonPlayerModule().getAddonPlayerManager().getPlayer(player.getUniqueId());
  }

  @Getter
  private final UUID playerID;

  @Getter
  private final AdventCalendar adventCalendar;

  @Getter
  @Setter
  private transient boolean resourcePackAccepted;

  public AddonPlayer(final UUID playerID) {
    this.playerID = playerID;
    this.adventCalendar = new AdventCalendar();
  }

}
