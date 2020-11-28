package com.gestankbratwurst.eproaddons.addons.christmas;

import com.gestankbratwurst.eproaddons.addons.customitems.CustomFood;
import com.gestankbratwurst.eproaddons.addons.customitems.CustomThrowPotion;
import com.gestankbratwurst.eproaddons.playerdata.AddonPlayer;
import com.gestankbratwurst.eproaddons.resourcepack.skins.Model;
import com.gestankbratwurst.eproaddons.util.Msg;
import com.gestankbratwurst.eproaddons.util.common.UtilPlayer;
import com.gestankbratwurst.eproaddons.util.items.ItemBuilder;
import com.google.common.collect.ImmutableList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import net.crytec.inventoryapi.api.ClickableItem;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of EproAddons and was created at the 22.11.2020
 *
 * EproAddons can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class ChristmasPresentProvider {

  private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

  private static final ItemStack[] PRESENT_ICONS = new ItemStack[]{
      Model.PRESENT_BLUE.getItem(),
      Model.PRESENT_LIGHT_BLUE.getItem(),
      Model.PRESENT_GREEN.getItem(),
      Model.PRESENT_ORANGE.getItem(),
      Model.PRESENT_PURPLE.getItem()
  };

  private static final List<Consumer<Player>> PRESENTS = ImmutableList.<Consumer<Player>>builder()
      .add(new ItemPresent(new ItemStack(Material.DIAMOND), 4, 8))
      .add(new ItemPresent(new ItemStack(Material.EMERALD), 4, 8))
      .add(new ItemPresent(new ItemStack(Material.DIAMOND_BLOCK), 1, 2))
      .add(new ItemPresent(new ItemStack(Material.EMERALD_BLOCK), 1, 2))
      .add(new ItemPresent(new ItemStack(Material.GHAST_TEAR), 2, 3))
      .add(new ItemPresent(new ItemStack(Material.IRON_BLOCK), 4, 8))
      .add(new ItemPresent(new ItemStack(Material.GOLDEN_APPLE), 3, 6))
      .add(new ItemPresent(new ItemStack(Material.BOOK), 32, 48))
      .add(new ItemPresent(CustomFood.GINGERBREAD.getItem(), 3, 6))
      .add(new ItemPresent(CustomThrowPotion.CHRISTMAS_SNOW.getItem(), 2, 3))
      .add(new ItemPresent(getCandyCane(), 1, 1))
      .build();

  private static boolean canBeObtained(final int day) {
    final Calendar calendar = Calendar.getInstance();
    final int month = calendar.get(Calendar.MONTH);
    final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    return month + 1 == 12 && dayOfMonth == day;
  }

  private static ItemStack getIcon(final int day) {
    final ItemStack base = day == 24 ? Model.PRESENT_RED.getItem() : PRESENT_ICONS[RANDOM.nextInt(PRESENT_ICONS.length)];
    return new ItemBuilder(base)
        .name("§eTag " + day)
        .lore("", "§7Kann geöffnet werden: " + (canBeObtained(day) ? "§aJa" : "§cNein"))
        .build();
  }

  public static ClickableItem getPresent(final int day) {
    return ClickableItem.of(getIcon(day), event -> get(day, (Player) event.getWhoClicked()));
  }

  public static void get(final int day, final Player who) {
    if (!canBeObtained(day)) {
      Msg.send(who, "Adventskalender", "Das kann noch nicht geöffnet werden.");
      UtilPlayer.playSound(who, Sound.BLOCK_NOTE_BLOCK_BASEDRUM);
      return;
    }
    if (day == 24) {
      getChristmas(who);
    } else {
      getNormal(who);
    }
    AddonPlayer.of(who).getAdventCalendar().christmasSlots[day - 1] = true;
    AdventCalendarGUI.open(who, day > 15 ? 2 : 1);
  }

  public static void getNormal(final Player who) {
    PRESENTS.get(RANDOM.nextInt(PRESENTS.size())).accept(who);
  }

  private static void getChristmas(final Player who) {
    for (int i = 0; i < 10; i++) {
      getNormal(who);
    }
  }

  public static ItemStack getCandyCane() {
    return new ItemBuilder(Model.CANDY_CANE.getItem())
        .name("§eZuckerstange")
        .lore("", "§7Hat den gleichen Effekt, wie", "§7ein Totem.")
        .attribute(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "DMG",
            0.25, Operation.MULTIPLY_SCALAR_1, EquipmentSlot.OFF_HAND))
        .build();
  }

  @AllArgsConstructor
  private static class ItemPresent implements Consumer<Player> {

    private final ItemStack baseItem;
    private final int min;
    private final int max;

    @Override
    public void accept(final Player player) {
      final int amount = RANDOM.nextInt(this.min, this.max + 1);
      final ItemStack item = this.baseItem.asOne();
      final int maxStackSize = item.getMaxStackSize();
      final int stacks = amount / maxStackSize;
      final int over = amount % maxStackSize;
      for (int i = 0; i < stacks; i++) {
        final ItemStack add = item.clone();
        add.setAmount(maxStackSize);
        player.getInventory().addItem(add).values().forEach(left -> player.getWorld().dropItemNaturally(player.getLocation(), left));
      }
      if (over > 0) {
        item.setAmount(over);
        player.getInventory().addItem(item).values().forEach(left -> player.getWorld().dropItemNaturally(player.getLocation(), left));
      }
    }
  }

}
