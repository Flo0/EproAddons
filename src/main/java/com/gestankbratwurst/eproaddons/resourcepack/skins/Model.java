package com.gestankbratwurst.eproaddons.resourcepack.skins;

import com.gestankbratwurst.eproaddons.resourcepack.packing.BoxedFontChar;
import com.gestankbratwurst.eproaddons.util.common.UtilItem;
import com.gestankbratwurst.eproaddons.util.items.ItemBuilder;
import com.gestankbratwurst.eproaddons.util.nbtapi.NBTItem;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.File;
import lombok.Getter;
import lombok.Setter;
import net.crytec.libs.protocol.skinclient.data.Skin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of avarioncore and was created at the 24.11.2019
 *
 * avarioncore can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public enum Model {

  GREEN_CHECK(Material.STICK, 1000, false, false),
  RED_X(Material.STICK, 1001, false, false),
  DOUBLE_GRAY_ARROW_UP(Material.STICK, 1002, false, false),
  DOUBLE_GRAY_ARROW_DOWN(Material.STICK, 1003, false, false),
  DOUBLE_GRAY_ARROW_LEFT(Material.STICK, 1004, false, false),
  DOUBLE_GRAY_ARROW_RIGHT(Material.STICK, 1005, false, false),
  GREEN_PLUS(Material.STICK, 1006, false, false),
  PRESENT_BLUE(Material.STICK, 1007, false, false),
  PRESENT_GREEN(Material.STICK, 1008, false, false),
  PRESENT_LIGHT_BLUE(Material.STICK, 1009, false, false),
  PRESENT_ORANGE(Material.STICK, 1010, false, false),
  PRESENT_PURPLE(Material.STICK, 1011, false, false),
  PRESENT_RED(Material.STICK, 1012, false, false),
  ADVENT_CALENDAR(Material.STICK, 2000, false, true),
  ADVENT_CALENDAR_TWO(Material.STICK, 2001, false, true),
  SNOWMAN_BOSS(Material.STICK, 2002, false, true),
  GINGERBREAD(Material.COOKIE, 3000, false, false),
  CANDY_CANE(Material.TOTEM_OF_UNDYING, 3001, false, false);

  Model(final Material baseMaterial, final int modelID, final boolean headEnabled, final boolean customModelDataEnabled) {
    this.baseMaterial = baseMaterial;
    this.modelID = modelID;
    this.modelData = ModelData.defaultGenerated();
    this.fontMeta = FontMeta.common();
    this.boxedFontChar = new BoxedFontChar();
    this.headSkinEnabled = headEnabled;
    this.customModelDataEnabled = customModelDataEnabled;
  }

  Model(final Material baseMaterial, final int modelID, final ModelData modelData, final FontMeta fontMeta, final boolean headEnabled,
      final boolean customModelDataEnabled) {
    this.baseMaterial = baseMaterial;
    this.modelID = modelID;
    this.modelData = modelData;
    this.fontMeta = fontMeta;
    this.boxedFontChar = new BoxedFontChar();
    this.headSkinEnabled = headEnabled;
    this.customModelDataEnabled = customModelDataEnabled;
  }

  @Getter
  private final Material baseMaterial;
  @Getter
  private final int modelID;
  @Getter
  private final ModelData modelData;
  @Getter
  private final FontMeta fontMeta;
  @Getter
  private final BoxedFontChar boxedFontChar;
  @Getter
  private final boolean headSkinEnabled;
  @Getter
  private final boolean customModelDataEnabled;
  @Getter
  @Setter
  private Skin skin;
  @Getter
  @Setter
  private File linkedImageFile;
  @Getter
  private GameProfile gameProfile;

  private ItemStack head;

  private ItemStack item;

  private void initProfile() {
    if (this.gameProfile == null && this.skin != null) {
      this.gameProfile = new GameProfile(this.skin.data.uuid, this.skin.name);
      this.gameProfile.getProperties()
          .put("textures", new Property("textures", this.skin.data.texture.value, this.skin.data.texture.signature));
    }
  }

  public char getChar() {
    return this.boxedFontChar.getAsCharacter();
  }

  public ItemStack getItem() {
    if (this.item == null) {
      this.item = new ItemBuilder(this.baseMaterial)
          .modelData(this.modelID)
          .name(this.toString())
          .build();
      final NBTItem nbt = new NBTItem(this.item);
      nbt.setString("Model", this.toString());
      this.item = nbt.getItem();
    }
    return this.item.clone();
  }

  public ItemStack getHead() {
    if (this.head != null) {
      return this.head.clone();
    }
    this.initProfile();

    this.head = UtilItem.produceHead(this.gameProfile);

    final NBTItem nbt = new NBTItem(this.head);
    nbt.setString("ModelHead", this.toString());
    this.head = nbt.getItem();
    return this.head.clone();
  }

}