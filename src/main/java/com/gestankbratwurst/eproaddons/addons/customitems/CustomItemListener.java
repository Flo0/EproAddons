package com.gestankbratwurst.eproaddons.addons.customitems;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

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
public class CustomItemListener implements Listener {

  @EventHandler
  public static void onSplash(final PotionSplashEvent event) {
    CustomThrowPotion.handleEvent(event);
  }

  @EventHandler
  public static void onEat(final PlayerItemConsumeEvent event) {
    CustomFood.handleEvent(event);
  }

}