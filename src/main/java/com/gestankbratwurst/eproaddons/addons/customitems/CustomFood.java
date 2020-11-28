package com.gestankbratwurst.eproaddons.addons.customitems;

import com.gestankbratwurst.eproaddons.resourcepack.skins.Model;
import com.gestankbratwurst.eproaddons.util.common.NameSpaceFactory;
import com.gestankbratwurst.eproaddons.util.items.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of EproAddons and was created at the 24.11.2020
 *
 * EproAddons can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
@RequiredArgsConstructor
public enum CustomFood {

  GINGERBREAD(Model.GINGERBREAD, "§fLebkuchen",
      new String[]{"", "§7Sättigt dich und stellt", "§750% deines fehlenden Lebens", "§7wieder her."}) {
    @Override
    protected void handle(final PlayerItemConsumeEvent event) {
      final Player player = event.getPlayer();
      final double maxHP = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
      final double damage = maxHP - player.getHealth();
      final double heal = damage / 2;

      player.setHealth(player.getHealth() + heal);
      player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getEyeLocation(), 16, 0.25, 0.65, 0.25);
      player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 200, 0));
    }

  };

  private static final NamespacedKey KEY = NameSpaceFactory.provide("CustomFood");

  private final Model itemModel;
  private final String name;
  private final String[] lore;

  protected abstract void handle(PlayerItemConsumeEvent event);

  public ItemStack getItem() {
    final ItemStack item = new ItemBuilder(this.itemModel.getItem())
        .name(this.name)
        .lore(this.lore)
        .build();
    this.tag(item);
    return item;
  }

  private void tag(final ItemStack item) {
    final ItemMeta meta = item.getItemMeta();
    final PersistentDataContainer pdc = meta.getPersistentDataContainer();
    pdc.set(KEY, PersistentDataType.STRING, this.toString());
    item.setItemMeta(meta);
  }

  public static void handleEvent(final PlayerItemConsumeEvent event) {
    final ItemStack item = event.getItem();
    final ItemMeta meta = item.getItemMeta();
    final PersistentDataContainer pdc = meta.getPersistentDataContainer();
    if (!pdc.has(KEY, PersistentDataType.STRING)) {
      return;
    }
    CustomFood.valueOf(pdc.get(KEY, PersistentDataType.STRING)).handle(event);
  }

}
