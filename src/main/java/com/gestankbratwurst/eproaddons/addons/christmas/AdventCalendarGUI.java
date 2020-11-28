package com.gestankbratwurst.eproaddons.addons.christmas;

import com.gestankbratwurst.eproaddons.playerdata.AddonPlayer;
import com.gestankbratwurst.eproaddons.resourcepack.skins.Model;
import com.gestankbratwurst.eproaddons.util.common.UtilPlayer;
import com.gestankbratwurst.eproaddons.util.items.ItemBuilder;
import lombok.RequiredArgsConstructor;
import net.crytec.inventoryapi.SmartInventory;
import net.crytec.inventoryapi.api.ClickableItem;
import net.crytec.inventoryapi.api.InventoryContent;
import net.crytec.inventoryapi.api.InventoryProvider;
import org.bukkit.entity.Player;

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
public class AdventCalendarGUI implements InventoryProvider {

  private static final int[] PAGE_1_SLOTS = new int[]{0, 2, 4, 6, 8, 18, 20, 22, 24, 26, 36, 38, 40, 42, 44};
  private static final int[] PAGE_2_SLOTS = new int[]{0, 2, 4, 6, 8, 20, 22, 24, 40};

  protected static void open(final Player player, final int page) {
    final AddonPlayer addonPlayer = AddonPlayer.of(player);
    SmartInventory.builder().size(6).title(" ").provider(new AdventCalendarGUI(addonPlayer.getAdventCalendar(), page)).build().open(player);
  }

  public static void open(final Player player) {
    AdventCalendarGUI.open(player, 1);
  }

  private final AdventCalendar adventCalendar;
  private final int page;

  @Override
  public void init(final Player player, final InventoryContent content) {
    if (this.page == 1) {
      for (int day = 1; day <= 15; day++) {
        if (!this.adventCalendar.christmasSlots[day - 1]) {
          content.set(PAGE_1_SLOTS[day - 1], ChristmasPresentProvider.getPresent(day));
        }
      }
      content.set(53, AdventCalendarGUI.getNext());
      content.set(49, ClickableItem.empty(new ItemBuilder(Model.ADVENT_CALENDAR.getItem()).name(" ").build()));
    } else {
      for (int day = 16; day <= 24; day++) {
        if (!this.adventCalendar.christmasSlots[day - 1]) {
          content.set(PAGE_2_SLOTS[day - 16], ChristmasPresentProvider.getPresent(day));
        }
      }
      content.set(45, AdventCalendarGUI.getBefore());
      content.set(49, ClickableItem.empty(new ItemBuilder(Model.ADVENT_CALENDAR_TWO.getItem()).name(" ").build()));
    }
  }

  private static ClickableItem getNext() {
    return ClickableItem.of(new ItemBuilder(Model.DOUBLE_GRAY_ARROW_RIGHT.getItem()).name("§eSeite 2").build(), e -> {
      UtilPlayer.playUIClick((Player) e.getWhoClicked());
      AdventCalendarGUI.open((Player) e.getWhoClicked(), 2);
    });
  }

  private static ClickableItem getBefore() {
    return ClickableItem.of(new ItemBuilder(Model.DOUBLE_GRAY_ARROW_LEFT.getItem()).name("§eSeite 1").build(), e -> {
      UtilPlayer.playUIClick((Player) e.getWhoClicked());
      AdventCalendarGUI.open((Player) e.getWhoClicked(), 1);
    });
  }

}
