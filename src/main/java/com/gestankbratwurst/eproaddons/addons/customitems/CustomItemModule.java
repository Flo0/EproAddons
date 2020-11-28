package com.gestankbratwurst.eproaddons.addons.customitems;

import com.gestankbratwurst.eproaddons.BaseModule;
import com.gestankbratwurst.eproaddons.EproAddons;
import org.bukkit.Bukkit;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of EproAddons and was created at the 22.11.2020
 *
 * EproAddons can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class CustomItemModule implements BaseModule {

  @Override
  public void enable(final EproAddons plugin) {
    EproAddons.registerListener(new CustomItemListener());
    Bukkit.getScheduler().runTaskTimer(plugin, CustomItemThread.getInstance(), 1L, 1L);
  }

  @Override
  public void disable(final EproAddons plugin) {

  }
}
