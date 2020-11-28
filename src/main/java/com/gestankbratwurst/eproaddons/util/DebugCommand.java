package com.gestankbratwurst.eproaddons.util;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Values;
import com.gestankbratwurst.eproaddons.addons.christmas.ChristmasPresentProvider;
import com.gestankbratwurst.eproaddons.addons.christmas.christmasboss.SnowBoss;
import com.gestankbratwurst.eproaddons.addons.customitems.CustomFood;
import com.gestankbratwurst.eproaddons.addons.customitems.CustomThrowPotion;
import com.gestankbratwurst.eproaddons.resourcepack.skins.Model;
import com.gestankbratwurst.eproaddons.util.items.CustomHead;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of JavaPlugin and was created at the 25.10.2020
 *
 * JavaPlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
// TODO remove
@CommandAlias("eproaddons")
@CommandPermission("admin.debug")
public class DebugCommand extends BaseCommand {

  @Default
  public void onDefault(final CommandSender sender) {

  }

  @Subcommand("models get")
  @CommandCompletion("@Models")
  public static void onModelItem(final Player sender, @Values("@Models") final Model skin) {
    sender.getInventory().addItem(skin.getItem());
    Msg.send(sender, "Debug", "Du erhälst das Item zu: " + skin);
  }

  @Subcommand("custompotion")
  public static void onCustomPotion(final Player sender, final CustomThrowPotion customThrowPotion) {
    sender.getInventory().addItem(customThrowPotion.getItem());
    Msg.send(sender, "Debug", "Du erhälst das Item zu: " + customThrowPotion);
  }

  @Subcommand("customhead")
  public static void onCustomHead(final Player sender, final CustomHead customHead) {
    sender.getInventory().addItem(customHead.getItem());
    Msg.send(sender, "Debug", "Du erhälst das Item zu: " + customHead);
  }

  @Subcommand("customfood")
  public static void onCustomFood(final Player sender, final CustomFood customFood) {
    sender.getInventory().addItem(customFood.getItem());
    Msg.send(sender, "Debug", "Du erhälst das Item zu: " + customFood);
  }

  @Subcommand("snowboss")
  public static void onSnowboss(final Player sender) {
    new SnowBoss(sender.getLocation());
    Msg.send(sender, "Weihnachtsboss", "Wurde gespawnt.");
  }

  @Subcommand("candybar")
  public static void onCandybar(final Player sender) {
    sender.getInventory().addItem(ChristmasPresentProvider.getCandyCane());
    Msg.send(sender, "Debug", "Du erhälst das Item zu: CANDY_CANE");
  }

  @Subcommand("getpresent")
  public static void onCandybar(final Player sender, final int amount) {
    for (int i = 0; i < amount; i++) {
      ChristmasPresentProvider.getNormal(sender);
    }
    Msg.send(sender, "Debug", "Du simulierst " + Msg.elem("" + amount) + " Adventskalender-Türen.");
  }

}
