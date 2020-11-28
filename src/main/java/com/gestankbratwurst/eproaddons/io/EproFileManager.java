package com.gestankbratwurst.eproaddons.io;

import com.gestankbratwurst.eproaddons.EproAddons;
import com.gestankbratwurst.eproaddons.playerdata.AddonPlayer;
import com.gestankbratwurst.eproaddons.util.json.GsonProvider;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of EproAddons and was created at the 21.11.2020
 *
 * EproAddons can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class EproFileManager {

  public EproFileManager() {
    final JavaPlugin plugin = EproAddons.getInstance();
    this.pluginFolder = plugin.getDataFolder();
    this.pluginFolder.mkdirs();
    this.configFile = new File(this.pluginFolder, "configuration.yml");
    plugin.saveResource("configuration.yml", false);
    this.eproConfig = new EproConfig(YamlConfiguration.loadConfiguration(this.configFile));
    this.playerDataFolder = new File(this.pluginFolder + File.separator + "playerdata");
    this.playerDataFolder.mkdirs();
  }

  @Getter
  private final EproConfig eproConfig;
  private final File playerDataFolder;
  private final File pluginFolder;
  private final File configFile;

  public void savePlayer(final AddonPlayer addonPlayer) {
    final String data = GsonProvider.toJson(addonPlayer);
    final File playerFile = new File(this.playerDataFolder, addonPlayer.getPlayerID() + ".json");
    try {
      Files.write(playerFile.toPath(), data.getBytes(StandardCharsets.UTF_8));
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public AddonPlayer loadPlayer(final UUID playerID) {
    final File playerFile = new File(this.playerDataFolder, playerID + ".json");
    if (!playerFile.exists()) {
      return null;
    }
    try {
      final String data = new String(Files.readAllBytes(playerFile.toPath()), StandardCharsets.UTF_8);
      return GsonProvider.fromJson(data, AddonPlayer.class);
    } catch (final IOException e) {
      e.printStackTrace();
      return null;
    }
  }


}
