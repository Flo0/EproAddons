package com.gestankbratwurst.eproaddons.resourcepack;

import com.gestankbratwurst.eproaddons.BaseModule;
import com.gestankbratwurst.eproaddons.EproAddons;
import com.gestankbratwurst.eproaddons.resourcepack.distribution.ResourcepackListener;
import com.gestankbratwurst.eproaddons.resourcepack.distribution.ResourcepackManager;
import com.gestankbratwurst.eproaddons.resourcepack.packing.AssetLibrary;
import com.gestankbratwurst.eproaddons.resourcepack.packing.ResourcepackAssembler;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class ResourcepackModule implements BaseModule {

  public ResourcepackModule(final BiConsumer<Player, PlayerResourcePackStatusEvent.Status> packResultConsumer) {
    this.packResultConsumer = packResultConsumer;
  }

  private final BiConsumer<Player, PlayerResourcePackStatusEvent.Status> packResultConsumer;
  private ResourcepackManager resourcepackManager;
  private AssetLibrary assetLibrary;

  @Override
  public void enable(final EproAddons plugin) {
    this.assetLibrary = new AssetLibrary(plugin);
    CompletableFuture.runAsync(() -> {
      try {
        new ResourcepackAssembler(plugin, this.assetLibrary).zipResourcepack();
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }).thenRun(() -> {
      try {
        this.resourcepackManager = new ResourcepackManager();
      } catch (final Exception exception) {
        exception.printStackTrace();
        Bukkit.shutdown();
      }
      EproAddons.registerListener(new ResourcepackListener(this.packResultConsumer, this.resourcepackManager));
    });
  }

  @Override
  public void disable(final EproAddons plugin) {
    if (this.resourcepackManager == null) {
      System.out.println("§c ResourcepackManager is null.");
    } else {
      this.resourcepackManager.shutdown();
    }
    this.assetLibrary.saveCache();
  }
}
