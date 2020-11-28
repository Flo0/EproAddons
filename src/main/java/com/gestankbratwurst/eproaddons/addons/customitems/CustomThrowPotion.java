package com.gestankbratwurst.eproaddons.addons.customitems;

import com.gestankbratwurst.eproaddons.util.common.NameSpaceFactory;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

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
public enum CustomThrowPotion {

  CHRISTMAS_SNOW(PotionType.AWKWARD, Color.FUCHSIA, "§eSchneebombe", new String[]{"", "§7Lässt es in einem", "§7Bereich schneien."}) {
    @Override
    public void handle(final PotionSplashEvent event) {
      final Block block = event.getHitBlock();
      final Location hitLoc;
      if (block == null) {
        final Entity entity = event.getHitEntity();
        if (entity == null) {
          hitLoc = event.getEntity().getLocation();
        } else {
          hitLoc = entity.getLocation();
        }
      } else {
        hitLoc = block.getLocation();
      }
      CustomItemThread.getInstance().addTask(new SnowBombTask(hitLoc));
    }
  };

  private static final NamespacedKey KEY = NameSpaceFactory.provide("CustomThrowPotion");

  private final PotionType potionType;
  private final Color color;
  private final String name;
  private final String[] lore;

  protected abstract void handle(PotionSplashEvent event);

  public ItemStack getItem() {
    final ItemStack potion = new ItemStack(Material.SPLASH_POTION);
    final PotionMeta meta = (PotionMeta) potion.getItemMeta();
    meta.setColor(this.color);
    meta.setBasePotionData(new PotionData(this.potionType, false, false));
    meta.setDisplayName(this.name);
    meta.setLore(Arrays.asList(this.lore));
    potion.setItemMeta(meta);
    this.tag(potion);
    return potion;
  }

  private void tag(final ItemStack item) {
    final ItemMeta meta = item.getItemMeta();
    final PersistentDataContainer pdc = meta.getPersistentDataContainer();
    pdc.set(KEY, PersistentDataType.STRING, this.toString());
    item.setItemMeta(meta);
  }

  public static void handleEvent(final PotionSplashEvent event) {
    final ItemStack item = event.getPotion().getItem();
    final ItemMeta meta = item.getItemMeta();
    final PersistentDataContainer pdc = meta.getPersistentDataContainer();
    if (!pdc.has(KEY, PersistentDataType.STRING)) {
      return;
    }
    CustomThrowPotion.valueOf(pdc.get(KEY, PersistentDataType.STRING)).handle(event);
  }

}
