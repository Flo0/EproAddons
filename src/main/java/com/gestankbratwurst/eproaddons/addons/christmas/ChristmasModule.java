package com.gestankbratwurst.eproaddons.addons.christmas;

import com.gestankbratwurst.eproaddons.BaseModule;
import com.gestankbratwurst.eproaddons.EproAddons;
import com.gestankbratwurst.eproaddons.addons.christmas.christmasboss.SnowBossManager;
import lombok.Getter;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of EproAddons and was created at the 22.11.2020
 *
 * EproAddons can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class ChristmasModule implements BaseModule {

  @Getter
  private SnowBossManager snowBossManager;

  @Override
  public void enable(final EproAddons plugin) {
    plugin.getPaperCommandManager().registerCommand(new ChristmasCommand());
    this.snowBossManager = new SnowBossManager(plugin);
  }

  @Override
  public void disable(final EproAddons plugin) {

  }
}
