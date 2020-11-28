package com.gestankbratwurst.eproaddons.addons.christmas.christmasboss;

import net.minecraft.server.v1_16_R3.EntitySlime;
import net.minecraft.server.v1_16_R3.EntityTypes;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of EproAddons and was created at the 26.11.2020
 *
 * EproAddons can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class TargetEntity extends EntitySlime {

  private final Location spawnLoc;

  public TargetEntity(final Location location) {
    super(EntityTypes.SLIME, ((CraftWorld) location.getWorld()).getHandle());
    this.spawnLoc = location.clone();
    this.setSilent(true);
    this.setSize(3, false);
    this.setHealth(100F);
    this.setInvisible(true);
    this.setCanPickupLoot(false);
    this.setPersistent();
  }

}
