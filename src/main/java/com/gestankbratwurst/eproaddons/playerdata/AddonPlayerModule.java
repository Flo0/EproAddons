package com.gestankbratwurst.eproaddons.playerdata;

import com.gestankbratwurst.eproaddons.BaseModule;
import com.gestankbratwurst.eproaddons.EproAddons;
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
public class AddonPlayerModule implements BaseModule {

  @Getter
  private AddonPlayerManager addonPlayerManager;

  @Override
  public void enable(final EproAddons plugin) {
    this.addonPlayerManager = new AddonPlayerManager(plugin.getEproFileManager());
    EproAddons.registerListener(new AddonPlayerListener(this.addonPlayerManager));
  }

  @Override
  public void disable(final EproAddons plugin) {

  }
}
