package com.gestankbratwurst.eproaddons.io;

import com.gestankbratwurst.eproaddons.EproAddons;
import lombok.Getter;
import org.bukkit.configuration.MemoryConfiguration;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of EproAddons and was created at the 21.11.2020
 *
 * EproAddons can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class EproConfig {

  public static EproConfig get() {
    return EproAddons.getInstance().getEproFileManager().getEproConfig();
  }

  protected EproConfig(final MemoryConfiguration configuration) {
    this.resourcePackServerIP = configuration.getString("ResourcePack.ServerIP");
    this.resourcePackServerPort = configuration.getInt("ResourcePack.ServerPort");
  }

  @Getter
  private final String resourcePackServerIP;
  @Getter
  private final int resourcePackServerPort;

}
