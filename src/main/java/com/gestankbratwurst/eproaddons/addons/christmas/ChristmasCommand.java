package com.gestankbratwurst.eproaddons.addons.christmas;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.gestankbratwurst.eproaddons.playerdata.AddonPlayer;
import com.gestankbratwurst.eproaddons.util.Msg;
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
@CommandAlias("adventskalender")
@CommandPermission("eproaddons.commands.adventskalender")
public class ChristmasCommand extends BaseCommand {

  @Default
  public static void onDefault(final Player sender) {
    if (!AddonPlayer.of(sender).isResourcePackAccepted()) {
      Msg.send(sender, "Adventskalender", "Du kannst den Kalender benutzen, wenn");
      Msg.send(sender, "Adventskalender", "du das Resourcepack angenommen hast.");
      return;
    }

    AdventCalendarGUI.open(sender);
    Msg.send(sender, "Adventskalender", "Öffne Kalender.");
  }

}
