package com.gestankbratwurst.eproaddons.addons.christmas.christmasboss;

import com.gestankbratwurst.eproaddons.addons.christmas.ChristmasPresentProvider;
import com.gestankbratwurst.eproaddons.addons.customitems.CustomFood;
import com.gestankbratwurst.eproaddons.addons.customitems.CustomThrowPotion;
import com.gestankbratwurst.eproaddons.resourcepack.skins.Model;
import com.gestankbratwurst.eproaddons.util.items.ItemBuilder;
import com.gestankbratwurst.eproaddons.util.tasks.TaskManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.server.v1_16_R3.EntityZombie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/*******************************************************
 * Copyright (C) Gestankbratwurst suotokka@gmail.com
 *
 * This file is part of EproAddons and was created at the 26.11.2020
 *
 * EproAddons can not be copied and/or distributed without the express
 * permission of the owner.
 *
 */
public class SnowBoss implements SnowBossTask {

  private static final EnumSet<BlockFace> TORNADO_DIRECTIONS = EnumSet.of(
      BlockFace.NORTH,
      BlockFace.NORTH_EAST,
      BlockFace.EAST,
      BlockFace.SOUTH_EAST,
      BlockFace.SOUTH,
      BlockFace.SOUTH_WEST,
      BlockFace.WEST,
      BlockFace.NORTH_WEST
  );

  @Getter(AccessLevel.PROTECTED)
  private final double maxHp;
  @Getter(AccessLevel.PROTECTED)
  private double currentHp;
  @Getter(AccessLevel.PROTECTED)
  private final Set<UUID> targets;
  private final UUID baseTarget;
  private boolean dead;
  private int ticksAlive = 0;
  @Getter(AccessLevel.PROTECTED)
  private final BossBar bossBar;
  private final List<ItemStack> drops;
  private final ThreadLocalRandom random = ThreadLocalRandom.current();
  private int dropsLeft = 1;
  private Location deathLoc;
  int ticksDead = 0;
  private boolean preparingTornadoes = false;

  public SnowBoss(final Location location) {
    final EntityZombie bottom = new EntityZombie(((CraftWorld) location.getWorld()).getHandle());
    bottom.setSilent(true);
    bottom.setInvisible(true);
    final TargetEntity mid = new TargetEntity(location);
    final TargetEntity top = new TargetEntity(location);

    this.drops = new ArrayList<>();
    this.initDrops();

    bottom.setLocation(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    mid.setLocation(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    top.setLocation(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());

    ((CraftWorld) location.getWorld()).getHandle().addEntity(bottom, SpawnReason.CUSTOM);
    ((CraftWorld) location.getWorld()).getHandle().addEntity(mid, SpawnReason.CUSTOM);
    ((CraftWorld) location.getWorld()).getHandle().addEntity(top, SpawnReason.CUSTOM);

    mid.startRiding(bottom);
    top.startRiding(mid);
    this.targets = new HashSet<>();

    this.targets.add(bottom.getUniqueID());
    this.targets.add(mid.getUniqueID());
    this.targets.add(top.getUniqueID());

    this.targets.stream().map(Bukkit::getEntity).map(LivingEntity.class::cast).filter(Objects::nonNull).forEach(target -> {
      final PotionEffect potionEffect = new PotionEffect(PotionEffectType.INVISIBILITY, (int) 1E6, 1, false);
      target.addPotionEffect(potionEffect);
    });

    this.baseTarget = bottom.getUniqueID();

    this.maxHp = 200;
    this.currentHp = this.maxHp;

    final Zombie zombie = (Zombie) Bukkit.getEntity(this.baseTarget);
    if (zombie != null) {
      zombie.getEquipment()
          .setHelmet(new ItemBuilder(Model.SNOWMAN_BOSS.getItem())
              .setUnbreakable(true)
              .attribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE,
                  new AttributeModifier(UUID.randomUUID(), "XMSN", 8D, Operation.ADD_NUMBER))
              .attribute(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "PSFG", 14D, Operation.ADD_NUMBER))
              .attribute(Attribute.GENERIC_MOVEMENT_SPEED,
                  new AttributeModifier(UUID.randomUUID(), "SPD", .1D, Operation.MULTIPLY_SCALAR_1))
              .build());
    }

    this.bossBar = Bukkit.createBossBar("", BarColor.YELLOW, BarStyle.SEGMENTED_10);
    this.updateBossBar();
    this.bossBar.setVisible(true);
    Bukkit.getOnlinePlayers().forEach(this.bossBar::addPlayer);

    SnowBossManager.getInstance().addSnowBoss(this);
  }

  private void initDrops() {
    this.drops.add(ChristmasPresentProvider.getCandyCane());
    this.drops.add(new ItemBuilder(Material.DIAMOND).amount(4).build());
    this.drops.add(new ItemStack(Material.DIAMOND_BLOCK));
    this.drops.add(new ItemStack(Material.EMERALD_BLOCK));
    this.drops.add(new ItemBuilder(Material.EMERALD).amount(4).build());
    this.drops.add(new ItemStack(Material.TOTEM_OF_UNDYING));
    this.drops.add(new ItemBuilder(Material.IRON_INGOT).amount(4).build());
    this.drops.add(new ItemBuilder(Material.GOLD_INGOT).amount(4).build());
    this.drops.add(new ItemBuilder(Material.LAPIS_LAZULI).amount(16).build());
    this.drops.add(new ItemBuilder(Material.ENDER_PEARL).amount(16).build());
    this.drops.add(new ItemBuilder(Material.SLIME_BALL).amount(16).build());
    this.drops.add(new ItemBuilder(Material.GOLDEN_APPLE).build());
    this.drops.add(new ItemStack(Material.NETHER_STAR));
    this.drops.add(new ItemStack(Material.SHULKER_BOX));
    this.drops.add(new ItemBuilder(Material.DIAMOND_PICKAXE)
        .enchant(Enchantment.DIG_SPEED, 4)
        .enchant(Enchantment.DURABILITY, 3)
        .enchant(Enchantment.SILK_TOUCH, 1).build());
    this.drops.add(new ItemBuilder(Material.DIAMOND_PICKAXE)
        .enchant(Enchantment.DIG_SPEED, 5)
        .enchant(Enchantment.DURABILITY, 8).build());
    this.drops.add(new ItemBuilder(Material.DIAMOND_PICKAXE)
        .enchant(Enchantment.DIG_SPEED, 4)
        .enchant(Enchantment.DURABILITY, 3)
        .enchant(Enchantment.LOOT_BONUS_BLOCKS, 4).build());
    this.drops.add(new ItemBuilder(Material.DIAMOND_SWORD)
        .enchant(Enchantment.DAMAGE_ALL, 3)
        .enchant(Enchantment.LOOT_BONUS_MOBS, 5).build());
    this.drops.add(new ItemBuilder(Material.NETHERITE_AXE).build());
    this.drops.add(new ItemBuilder(Material.NETHERITE_BOOTS).build());
    this.drops.add(new ItemBuilder(Material.NETHERITE_LEGGINGS).build());
    this.drops.add(new ItemBuilder(Material.NETHERITE_HOE).build());
    this.drops.add(new ItemBuilder(Material.NETHERITE_INGOT).build());
    this.drops.add(new ItemBuilder(Material.NETHERITE_CHESTPLATE).build());
    this.drops.add(new ItemBuilder(Material.NETHERITE_HELMET).build());
    this.drops.add(new ItemBuilder(Material.NETHERITE_PICKAXE).build());
    this.drops.add(new ItemBuilder(Material.NETHERITE_SCRAP).build());
    this.drops.add(new ItemBuilder(Material.NETHERITE_SHOVEL).build());
    this.drops.add(new ItemBuilder(Material.NETHERITE_SWORD).build());
    this.drops.add(new ItemBuilder(CustomThrowPotion.CHRISTMAS_SNOW.getItem()).amount(2).build());
    this.drops.add(new ItemBuilder(CustomThrowPotion.CHRISTMAS_SNOW.getItem()).amount(3).build());
    this.drops.add(new ItemBuilder(CustomThrowPotion.CHRISTMAS_SNOW.getItem()).amount(4).build());
    this.drops.add(new ItemBuilder(CustomFood.GINGERBREAD.getItem()).amount(4).build());
    this.drops.add(new ItemBuilder(CustomFood.GINGERBREAD.getItem()).amount(6).build());
    this.drops.add(new ItemBuilder(CustomFood.GINGERBREAD.getItem()).amount(8).build());
    this.drops.add(new ItemBuilder(Material.IRON_PICKAXE)
        .name("§eZwergenfluch")
        .enchant(Enchantment.DIG_SPEED, 6)
        .enchant(Enchantment.DURABILITY, 5)
        .enchant(Enchantment.LOOT_BONUS_BLOCKS, 4)
        .enchant(Enchantment.KNOCKBACK, 3)
        .enchant(Enchantment.DAMAGE_ALL, 3)
        .enchant(Enchantment.DAMAGE_ARTHROPODS, 3)
        .enchant(Enchantment.DAMAGE_UNDEAD, 3)
        .enchant(Enchantment.MENDING, 1)
        .enchant(Enchantment.BINDING_CURSE, 1)
        .build());
    this.drops.add(new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, 6).enchant(Enchantment.ARROW_INFINITE, 1).build());
    this.drops.add(new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, 6).enchant(Enchantment.ARROW_FIRE, 3).build());
    this.drops.add(new ItemBuilder(Material.ELYTRA)
        .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
        .enchant(Enchantment.DURABILITY, 4)
        .enchant(Enchantment.BINDING_CURSE, 1)
        .build());
    Collections.shuffle(this.drops);
  }

  private void updateBossBar() {
    double scalar = this.getHpScalar();
    this.bossBar.setTitle("§eWinterboss §a" + this.currentHp + "§f / §a" + this.maxHp + " §f[§e" + (int) (scalar * 100) + "%" + "§f]");
    scalar = Math.max(0, scalar);
    this.bossBar.setProgress(scalar);
  }

  public void damage(final double amount) {
    this.currentHp -= amount;
    this.roundHp();
    if (this.currentHp <= 0) {
      this.deathLoc = Objects.requireNonNull(Bukkit.getEntity(this.baseTarget)).getLocation();
      this.onDeath();
    }

    if (this.random.nextDouble() > 0.55) {
      final Location location = Objects.requireNonNull(Bukkit.getEntity(this.baseTarget)).getLocation();
      final World world = location.getWorld();
      world.playSound(location, Sound.ENTITY_PHANTOM_HURT, 1F, 0.2F);
    }

    this.updateBossBar();
  }

  private void roundHp() {
    this.currentHp = ((int) (this.currentHp * 10D)) / 10D;
  }

  public boolean isTarget(final UUID id) {
    return this.targets.contains(id);
  }

  public double getHpScalar() {
    return ((int) (1000.0 / this.maxHp * this.currentHp)) / 1000D;
  }

  private void onDeath() {
    this.dropLoot();
    this.dead = true;
    this.deathEffect();
    this.clear();
  }

  private void deathEffect() {
    final Location location = Objects.requireNonNull(Bukkit.getEntity(this.baseTarget)).getLocation();
    final World world = location.getWorld();
    world.spawnParticle(Particle.SNOW_SHOVEL, location, 128, 4, 4, 4, 1);
    world.spawnParticle(Particle.FIREWORKS_SPARK, location, 160, 2.5, 2.5, 2.5, 0.33);
    world.playSound(location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 2F, 0.33F);
    world.playSound(location, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 2F, 0.33F);
    world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 2F, 0.33F);
    world.playSound(location, Sound.ENTITY_PHANTOM_DEATH, 2F, 0.15F);
    world.playSound(location, Sound.ENTITY_GHAST_DEATH, 2F, 0.09F);
    world.playSound(location, Sound.ENTITY_PHANTOM_HURT, 2F, 0.15F);
    world.playSound(location, Sound.ENTITY_GHAST_HURT, 2F, 0.09F);
  }

  private ItemStack getRandomDrop() {
    return this.drops.get(this.random.nextInt(this.drops.size()));
  }

  private void dropLoot() {
    for (final Player player : Bukkit.getOnlinePlayers()) {
      for (int i = 0; i < 5; i++) {
        ChristmasPresentProvider.getNormal(player);
      }
    }
    this.dropsLeft = Bukkit.getOnlinePlayers().size() * 100;
  }

  private void prepareMeteors() {
    if (this.ticksAlive % 1150 != 0) {
      return;
    }
    final Zombie base = (Zombie) Objects.requireNonNull(Bukkit.getEntity(this.baseTarget));
    final Location location = base.getLocation();
    location.getWorld().playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2.5F, 0.3F);
    this.fireMeteors();
  }

  private void fireMeteors() {
    final int amount = this.random.nextInt(7, 14);
    final Zombie base = (Zombie) Objects.requireNonNull(Bukkit.getEntity(this.baseTarget));
    final Location location = base.getLocation();
    final SnowBossThread thread = SnowBossManager.getInstance().getSnowBossThread();
    final TaskManager taskManager = TaskManager.getInstance();
    for (int i = 0; i < amount; i++) {
      taskManager.runBukkitSyncDelayed(() -> {
        final Location dropLoc = location.clone().add(this.random.nextDouble(-22, 22), 0, this.random.nextDouble(-22, 22));
        final SnowMeteor meteor = new SnowMeteor(dropLoc, this::filterAttackedEntities);
        thread.addTask(meteor);
      }, i * 10);
    }
  }

  private void pullEnemies() {
    if (this.ticksAlive % 500 != 0) {
      return;
    }
    final Zombie base = (Zombie) Bukkit.getEntity(this.baseTarget);
    if (base == null) {
      return;
    }
    final Location location = base.getLocation();
    final List<LivingEntity> pullTargets = new ArrayList<>();
    location.getNearbyLivingEntities(64, this::filterAttackedEntities).forEach(hit -> {
      if (hit.getLocation().distanceSquared(location) > 100) {
        pullTargets.add(hit);
      }
    });
    final TaskManager taskManager = TaskManager.getInstance();
    for (int i = 0; i < 4; i++) {
      taskManager.runBukkitSyncDelayed(() -> {
        for (final LivingEntity target : pullTargets) {
          final Vector vec = location.toVector().subtract(target.getLocation().toVector());
          final Vector pullVec = vec.clone().normalize().multiply(3).add(new Vector(0, 1, 0));
          target.setVelocity(pullVec);
          final Location partLoc = target.getLocation();
          final Vector partVec = vec.clone().normalize();
          final int dist = (int) location.distance(target.getLocation());
          final World world = target.getWorld();
          for (int j = 0; j < dist; j++) {
            world.spawnParticle(Particle.CRIT, partLoc, 1);
            partLoc.add(partVec);
          }
        }
      }, i * 30);
    }
  }

  private void prepareTornadoes() {
    if (this.ticksAlive % 600 != 0) {
      return;
    }
    this.preparingTornadoes = true;
    final Zombie base = (Zombie) Bukkit.getEntity(this.baseTarget);
    if (base == null) {
      return;
    }
    final Location location = base.getLocation();
    base.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 140, 2));
    location.getWorld().playSound(location, Sound.ENTITY_GHAST_DEATH, 2.5F, 0.01F);
    TaskManager.getInstance().runBukkitSyncDelayed(this::fireTornadoes, 80);
  }

  private void fireTornadoes() {
    this.preparingTornadoes = false;
    final Zombie base = (Zombie) Objects.requireNonNull(Bukkit.getEntity(this.baseTarget));
    final Location location = base.getLocation();
    location.getWorld().playSound(location, Sound.ENTITY_BLAZE_SHOOT, 2F, 1.8F);
    location.getWorld().playSound(location, Sound.ENTITY_BLAZE_SHOOT, 2F, 0.2F);
    location.getWorld().playSound(location, Sound.BLOCK_SNOW_BREAK, 2F, 0.2F);
    location.getWorld().playSound(location, Sound.BLOCK_SNOW_BREAK, 2F, 1.8F);
    location.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, location.clone().add(0, 1, 0), 40, 1, 2, 1, .25);
    final SnowBossThread thread = SnowBossManager.getInstance().getSnowBossThread();
    TORNADO_DIRECTIONS.stream().map(BlockFace::getDirection).forEach(direction -> {
      final SnowTornado tornado = new SnowTornado(location.clone(), direction.multiply(0.15), this::filterAttackedEntities);
      thread.addTask(tornado);
    });
  }

  private Vector getRandomDropVec() {
    final double x = this.random.nextDouble(-.33, .33);
    final double y = 1;
    final double z = this.random.nextDouble(-.33, .33);
    return new Vector(x, y, z).normalize().multiply(this.random.nextDouble(0.9, 1.5));
  }

  private void dropNext() {
    this.dropsLeft--;
    final Item item = this.deathLoc.getWorld().dropItemNaturally(this.deathLoc, this.getRandomDrop());
    final Vector vector = this.getRandomDropVec();
    item.setVelocity(vector);
  }

  private void dropExp() {
    final Location expLoc = this.deathLoc.clone().add(this.random.nextDouble(-3, 3), 0, this.random.nextDouble(-3, 3));
    final ExperienceOrb orb = this.deathLoc.getWorld().spawn(expLoc, ExperienceOrb.class);
    orb.setExperience(this.random.nextInt(15, 25));
    orb.setVelocity(this.getRandomDropVec());
  }

  public void clear() {
    SnowBossManager.getInstance().removeBoss(this);
    for (final UUID id : this.targets) {
      final Entity entity = Bukkit.getEntity(id);
      if (entity != null) {
        entity.remove();
      }
    }
    Bukkit.getOnlinePlayers().forEach(this::removePlayerFromBossBar);
  }

  private void attack() {
    if (this.ticksAlive % 80 != 0) {
      return;
    }
    final Zombie entity = (Zombie) Bukkit.getEntity(this.baseTarget);
    if (entity == null) {
      return;
    }
    final Location baseLoc = entity.getLocation();
    baseLoc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, baseLoc, 70, 2.8, 1, 2.8, 0.05);
    for (final LivingEntity target : baseLoc.getNearbyLivingEntities(6, this::filterAttackedEntities)) {
      final Vector throwDir = target.getLocation().toVector().subtract(baseLoc.toVector()).add(new Vector(0, 1, 0));
      target.setVelocity(throwDir.normalize());
      target.damage(12);
    }
  }

  private boolean filterAttackedEntities(final LivingEntity entity) {
    return !this.targets.contains(entity.getUniqueId());
  }

  public void removePlayerFromBossBar(final Player player) {
    this.bossBar.removePlayer(player);
  }

  public void addPlayerToBossBar(final Player player) {
    this.bossBar.addPlayer(player);
  }

  private void playEffects() {
    final Zombie entity = (Zombie) Bukkit.getEntity(this.baseTarget);
    if (entity == null) {
      return;
    }
    final Location location = entity.getLocation();
    if (this.preparingTornadoes) {
      location.getWorld().spawnParticle(Particle.SNOW_SHOVEL, location.clone().add(0, 1, 0), 64, 1, 3, 1);
    }
  }

  @Override
  public boolean tick() {
    this.ticksAlive++;
    this.attack();
    this.prepareTornadoes();
    this.prepareMeteors();
    this.pullEnemies();
    this.playEffects();
    if (this.dead) {
      this.ticksDead++;
      if (this.ticksDead == 40) {
        Bukkit.broadcastMessage("§eDer Weihnachtsboss wurde besiegt.");
        Bukkit.getOnlinePlayers().forEach(pl -> pl.sendTitle("", "§eGewonnen", 15, 50, 15));
        this.deathLoc.getWorld().playSound(this.deathLoc, Sound.UI_TOAST_CHALLENGE_COMPLETE, 10F, 0.8F);
      }
      if (this.dropsLeft > 0) {
        this.dropExp();
        if (this.ticksAlive % 5 == 0) {
          this.dropNext();
        }
      } else {
        return true;
      }
    }
    return false;
  }

}
