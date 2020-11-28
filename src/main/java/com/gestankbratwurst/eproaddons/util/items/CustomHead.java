package com.gestankbratwurst.eproaddons.util.items;

import com.gestankbratwurst.eproaddons.util.common.UtilItem;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of EproAddons and was created at the 23.11.2020
 *
 * EproAddons can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
@RequiredArgsConstructor
public enum CustomHead {

  PLACEHOLDER(
      "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjZhNzZjYzIyZTdjMmFiOWM1NDBkMTI0NGVhZGJhNTgxZjVkZDllMThmOWFkYWNmMDUyODBhNWI0OGI4ZjYxOCJ9fX0=");


  private final String base64;
  private ItemStack item;

  public ItemStack getItem() {
    return this.item == null ? this.createHead() : this.item.clone();
  }

  private ItemStack createHead() {
    this.item = UtilItem.produceHead(this.base64);
    return this.item.clone();
  }

}
