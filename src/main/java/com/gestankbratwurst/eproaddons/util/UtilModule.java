package com.gestankbratwurst.eproaddons.util;

import com.gestankbratwurst.eproaddons.BaseModule;
import com.gestankbratwurst.eproaddons.EproAddons;
import com.gestankbratwurst.eproaddons.resourcepack.skins.Model;
import com.gestankbratwurst.eproaddons.util.actionbar.ActionBarManager;
import com.gestankbratwurst.eproaddons.util.common.BukkitTime;
import com.gestankbratwurst.eproaddons.util.common.NameSpaceFactory;
import com.gestankbratwurst.eproaddons.util.common.UtilItem;
import com.gestankbratwurst.eproaddons.util.common.UtilMobs;
import com.gestankbratwurst.eproaddons.util.common.UtilPlayer;
import com.gestankbratwurst.eproaddons.util.items.display.ItemDisplayCompiler;
import com.gestankbratwurst.eproaddons.util.json.GsonProvider;
import com.gestankbratwurst.eproaddons.util.json.ItemStackArraySerializer;
import com.gestankbratwurst.eproaddons.util.json.ItemStackSerializer;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.Getter;
import net.crytec.inventoryapi.InventoryAPI;
import net.crytec.libs.protocol.ProtocolAPI;
import net.crytec.libs.protocol.holograms.impl.HologramManager;
import net.crytec.libs.protocol.holograms.impl.infobar.InfoBar;
import net.crytec.libs.protocol.holograms.infobars.InfoBarManager;
import net.crytec.libs.protocol.npc.NpcAPI;
import net.crytec.libs.protocol.skinclient.PlayerSkinManager;
import net.crytec.libs.protocol.tablist.TabListManager;
import org.bukkit.inventory.ItemStack;

public class UtilModule implements BaseModule {

  @Getter
  private HologramManager hologramManager;
  @Getter
  private ActionBarManager actionBarManager;
  @Getter
  private InfoBarManager infoBarManager;
  @Getter
  private ProtocolAPI protocolAPI;
  @Getter
  private NpcAPI npcAPI;
  @Getter
  private TabListManager tabListManager;
  @Getter
  private PlayerSkinManager playerSkinManager;
  @Getter
  private ItemDisplayCompiler displayCompiler;

  @Override
  public void enable(final EproAddons plugin) {
    plugin.getPaperCommandManager().getCommandCompletions()
        .registerStaticCompletion("Models", Arrays.stream(Model.values()).map(Enum::toString).collect(Collectors.toList()));
    plugin.getPaperCommandManager().registerCommand(new DebugCommand());

    GsonProvider.register(ItemStack.class, new ItemStackSerializer());
    GsonProvider.register(ItemStack[].class, new ItemStackArraySerializer());

    BukkitTime.start(plugin);
    NameSpaceFactory.init(plugin);
    UtilPlayer.init(plugin);
    UtilMobs.init(plugin);
    UtilItem.init(plugin);

    InventoryAPI.init(plugin);

    this.displayCompiler = new ItemDisplayCompiler(plugin);
    plugin.getProtocolManager().addPacketListener(this.displayCompiler);
    this.hologramManager = new HologramManager(plugin);
    this.playerSkinManager = new PlayerSkinManager();
    // this.actionBarManager = new ActionBarManager(plugin);
    this.infoBarManager = new InfoBarManager(plugin, (entity) -> new InfoBar(entity, this.infoBarManager));
    this.protocolAPI = new ProtocolAPI(plugin);
    this.npcAPI = new NpcAPI(plugin);
    // final EmptyTablist et = new EmptyTablist(this.tabListManager);
    // this.tabListManager = new TabListManager(plugin, (p) -> et);
  }

  @Override
  public void disable(final EproAddons plugin) {

  }

}
