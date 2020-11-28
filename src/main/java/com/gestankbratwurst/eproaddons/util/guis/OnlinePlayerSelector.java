package com.gestankbratwurst.eproaddons.util.guis;

import com.gestankbratwurst.eproaddons.resourcepack.skins.Model;
import com.gestankbratwurst.eproaddons.util.Msg;
import com.gestankbratwurst.eproaddons.util.common.UtilPlayer;
import com.gestankbratwurst.eproaddons.util.items.ItemBuilder;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import net.crytec.inventoryapi.SmartInventory;
import net.crytec.inventoryapi.anvil.AnvilGUI;
import net.crytec.inventoryapi.anvil.Response;
import net.crytec.inventoryapi.api.ClickableItem;
import net.crytec.inventoryapi.api.InventoryContent;
import net.crytec.inventoryapi.api.InventoryProvider;
import net.crytec.inventoryapi.api.SlotPos;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of JavaPlugin and was created at the 28.10.2020
 *
 * JavaPlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
@RequiredArgsConstructor
public class OnlinePlayerSelector implements InventoryProvider {

  public static void open(final Player player, final Consumer<Player> consumer) {
    OnlinePlayerSelector.open(player, consumer, (p) -> true);
  }

  public static void open(final Player player, final Consumer<Player> consumer, final Predicate<Player> filter) {
    final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
    SmartInventory.builder()
        .size(5)
        .title("Spieler auswählen")
        .provider(new OnlinePlayerSelector(players, consumer))
        .build()
        .open(player);
  }

  private final Collection<? extends Player> players;
  private final Consumer<Player> consumer;

  @Override
  public void init(final Player player, final InventoryContent content) {
    this.players.stream().map(OnlinePlayerSelector::getPlayerIcon).forEach(content::add);
    content.set(SlotPos.of(4, 8), this.getInputIcon());
    content.set(SlotPos.of(4, 0), this.getBackIcon());
  }

  private static ClickableItem getPlayerIcon(final Player player) {
    // TODO get Head Item
    return null;
  }

  private ClickableItem getInputIcon() {
    final ItemStack icon = new ItemBuilder(Material.WRITABLE_BOOK).name("§eNamen eingeben").build();
    return ClickableItem.of(icon, this::onNameInput);
  }

  private ClickableItem getBackIcon() {
    final ItemStack icon = new ItemBuilder(Model.DOUBLE_GRAY_ARROW_LEFT.getItem()).name("§eZurück").build();
    return ClickableItem.of(icon, event -> {
      this.consumer.accept(null);
      UtilPlayer.playUIClick((Player) event.getWhoClicked());
    });
  }

  private void onNameInput(final InventoryClickEvent event) {
    UtilPlayer.playUIClick((Player) event.getWhoClicked());
    new AnvilGUI.Builder().item(new ItemBuilder(Material.WRITABLE_BOOK).name("§eSpieler namen eingeben").build())
        .title("Spieler namen eingeben")
        .onClose(pl -> OnlinePlayerSelector.open(pl, this.consumer))
        .onComplete((p, in) -> {
          final Player selected = Bukkit.getPlayer(in);
          if (selected == null) {
            Msg.send(p, "Online", "Es gibt keinen online Spieler mit diesem Namen.");
          } else {
            this.consumer.accept(selected);
          }
          return Response.close();
        }).open((Player) event.getWhoClicked());
  }

}