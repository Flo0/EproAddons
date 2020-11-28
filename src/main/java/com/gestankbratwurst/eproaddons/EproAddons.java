package com.gestankbratwurst.eproaddons;

import co.aikar.commands.PaperCommandManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.gestankbratwurst.eproaddons.addons.christmas.ChristmasModule;
import com.gestankbratwurst.eproaddons.addons.customitems.CustomItemModule;
import com.gestankbratwurst.eproaddons.io.EproFileManager;
import com.gestankbratwurst.eproaddons.playerdata.AddonPlayer;
import com.gestankbratwurst.eproaddons.playerdata.AddonPlayerModule;
import com.gestankbratwurst.eproaddons.resourcepack.ResourcepackModule;
import com.gestankbratwurst.eproaddons.util.UtilModule;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.plugin.java.JavaPlugin;

public final class EproAddons extends JavaPlugin {

  @Getter
  private static EproAddons instance;

  @Getter
  private EproFileManager eproFileManager;

  @Getter
  private ProtocolManager protocolManager;

  @Getter
  private PaperCommandManager paperCommandManager;

  @Getter
  private UtilModule utilModule;

  @Getter
  private ResourcepackModule resourcepackModule;

  @Getter
  private AddonPlayerModule addonPlayerModule;

  @Getter
  private CustomItemModule customItemModule;

  @Getter
  private ChristmasModule christmasModule;

  @Override
  public void onEnable() {
    EproAddons.instance = this;
    this.eproFileManager = new EproFileManager();
    this.protocolManager = ProtocolLibrary.getProtocolManager();
    this.paperCommandManager = new PaperCommandManager(this);
    this.utilModule = new UtilModule();
    this.resourcepackModule = new ResourcepackModule((player, status) -> {
      AddonPlayer.of(player).setResourcePackAccepted(status == Status.SUCCESSFULLY_LOADED);
    });
    this.addonPlayerModule = new AddonPlayerModule();
    this.customItemModule = new CustomItemModule();
    this.christmasModule = new ChristmasModule();

    this.enableModules();
  }

  @Override
  public void onDisable() {
    this.disableModules();
  }

  private void enableModules() {
    this.utilModule.enable(this);
    this.resourcepackModule.enable(this);
    this.addonPlayerModule.enable(this);
    this.customItemModule.enable(this);
    this.christmasModule.enable(this);
  }

  private void disableModules() {
    this.christmasModule.disable(this);
    this.customItemModule.disable(this);
    this.addonPlayerModule.disable(this);
    this.resourcepackModule.disable(this);
    this.utilModule.disable(this);
  }

  public static void registerListener(final Listener listener) {
    EproAddons.instance.getServer().getPluginManager().registerEvents(listener, EproAddons.instance);
  }

}