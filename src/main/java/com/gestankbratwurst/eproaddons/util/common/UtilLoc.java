package com.gestankbratwurst.eproaddons.util.common;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of LaLaLand-CorePlugin and was created at the 19.11.2019
 *
 * LaLaLand-CorePlugin can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class UtilLoc {

  public static String locToByteEncodedString(final Location location) {
    return new String(toBytes(location), StandardCharsets.UTF_8);
  }

  public static Location locFromByteEncodedString(final String string) {
    return fromBytes(string.getBytes(StandardCharsets.UTF_8));
  }

  public static List<Location> generateSphere(final Location centerBlock, final int radius, final boolean hollow) {
    if (centerBlock == null) {
      return new ArrayList<>();
    }

    final List<Location> circleBlocks = new ArrayList<>();

    final int bx = centerBlock.getBlockX();
    final int by = centerBlock.getBlockY();
    final int bz = centerBlock.getBlockZ();

    for (int x = bx - radius; x <= bx + radius; x++) {
      for (int y = by - radius; y <= by + radius; y++) {
        for (int z = bz - radius; z <= bz + radius; z++) {

          final double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)) + ((by - y) * (by - y)));

          if (distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))) {

            final Location loc = new Location(centerBlock.getWorld(), x, y, z);

            circleBlocks.add(loc);

          }
        }
      }
    }

    return circleBlocks;
  }

  public static Set<Block> getBlocksInSphere(final Location centerBlock, final int radius, final boolean hollow) {
    if (centerBlock == null) {
      return new HashSet<>();
    }

    final Set<Block> circleBlocks = new HashSet<>();

    final int bx = centerBlock.getBlockX();
    final int by = centerBlock.getBlockY();
    final int bz = centerBlock.getBlockZ();

    for (int x = bx - radius; x <= bx + radius; x++) {
      for (int y = by - radius; y <= by + radius; y++) {
        for (int z = bz - radius; z <= bz + radius; z++) {

          final double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)) + ((by - y) * (by - y)));

          if (distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))) {

            final Location loc = new Location(centerBlock.getWorld(), x, y, z);

            circleBlocks.add(loc.getBlock());

          }
        }
      }
    }

    return circleBlocks;
  }

  public static Location locFromString(final String locationString) {
    final String[] split = locationString.split("#-#");
    final World world = Bukkit.getWorld(UUID.fromString(split[0]));
    final double x = Double.parseDouble(split[1]);
    final double y = Double.parseDouble(split[2]);
    final double z = Double.parseDouble(split[3]);
    final float pitch = Float.parseFloat(split[4]);
    final float yaw = Float.parseFloat(split[5]);
    if (world == null) {
      return null;
    }
    return new Location(world, x, y, z, pitch, yaw);
  }

  public static String locToString(final Location location) {
    return location.getWorld().getUID() + "#-#" + location.getX() + "#-#" + location.getY() + "#-#" + location.getZ() + "#-#" + location
        .getPitch()
        + "#-#" + location.getYaw();
  }

  public static byte[] toBytes(final Location location) {
    final ByteBuffer buffer = ByteBuffer.allocate(48);
    buffer.rewind();
    final UUID worldID = location.getWorld().getUID();
    final double x = location.getX();
    final double y = location.getY();
    final double z = location.getZ();
    final float pitch = location.getPitch();
    final float yaw = location.getYaw();

    buffer.putLong(worldID.getMostSignificantBits());
    buffer.putLong(worldID.getLeastSignificantBits());
    buffer.putDouble(x);
    buffer.putDouble(y);
    buffer.putDouble(z);
    buffer.putFloat(pitch);
    buffer.putFloat(yaw);

    return buffer.array();
  }

  public static Location fromBytes(final byte[] bytes) {
    final ByteBuffer buffer = ByteBuffer.wrap(bytes);
    buffer.rewind();
    final long idMSB = buffer.getLong();
    final long idLSB = buffer.getLong();
    final double x = buffer.getDouble();
    final double y = buffer.getDouble();
    final double z = buffer.getDouble();
    final float pitch = buffer.getFloat();
    final float yaw = buffer.getFloat();

    final World world = Bukkit.getWorld(new UUID(idMSB, idLSB));
    if (world == null) {
      return null;
    }
    return new Location(world, x, y, z, yaw, pitch);
  }

}
