package org.curryman2.citems.items;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.curryman2.citems.CItems;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ElementSword implements Listener {

    private final JavaPlugin plugin;

    private final HashMap<UUID, Long> fireFloorCooldown = new HashMap<>();
    private final HashMap<UUID, Long> cageCooldowns = new HashMap<>();
    private final long cageCooldownTime = 60 * 1000;
    private final long fireFloorCooldownTime = 60 * 1000;
    private final HashMap<UUID, Long> healingAuraCooldown = new HashMap<>();
    private final HashMap<UUID, Long> earthSpikeCooldown = new HashMap<>();
    private final long healingAuraCooldownTime = 35 * 1000;
    private final long earthSpikeCooldownTime = 60 * 1000;
    private final HashMap<UUID, Long> fireballCooldown = new HashMap<>();
    private final long fireballCooldownTime = 15 * 1000;

    public ElementSword(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static ItemStack createElementSword() {
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GREEN + "Element Sword");

        sword.setItemMeta(meta);
        return sword;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item != null && item.getType() == Material.IRON_SWORD && item.getItemMeta() != null &&
                item.getItemMeta().getDisplayName().equals(ChatColor.DARK_GREEN + "Element Sword")) {

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                if (player.isSneaking()) {
                    activateLavaToObsidian(player);
                } else {
                    Entity target = getNearestEntityInSight(player, 10);
                    if (target != null) {
                        encaseInIceCage(player, target);
                        player.sendMessage(ChatColor.AQUA + "You have trapped " + target.getName() + " in an ice cage!");
                        event.setCancelled(true);
                    } else {
                        player.sendMessage(ChatColor.RED + "No target found within range.");
                    }
                }
                event.setCancelled(true);
            } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK ) {
                if (player.isSneaking()) {
                    createHealingAura(player);
                } else {
                    shootFireball(player);
                }
            }
        }
    }
    private Entity getNearestEntityInSight(Player player, int range) {
        Location eye = player.getEyeLocation();
        for (int i = 0; i < range; i++) {
            eye.add(eye.getDirection().multiply(1));
            for (Entity entity : player.getWorld().getNearbyEntities(eye, 1, 1, 1)) {
                if (!entity.equals(player)) {
                    return entity;
                }
            }
        }
        return null;
    }
    private void encaseInIceCage(Player player, Entity target) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (cageCooldowns.containsKey(playerId) && (currentTime - cageCooldowns.get(playerId)) < cageCooldownTime) {
            player.sendMessage(ChatColor.RED + "Ice Cage ability is on cooldown!");
            return;
        }

        Location targetLocation = target.getLocation();
        int cageRadius = 2;


        target.teleport(targetLocation.clone().add(0, 1, 0));


        for (int x = -cageRadius; x <= cageRadius; x++) {
            for (int y = 0; y <= cageRadius * 2; y++) {
                for (int z = -cageRadius; z <= cageRadius; z++) {
                    Location loc = targetLocation.clone().add(x, y, z);
                    if (Math.abs(x) == cageRadius || Math.abs(z) == cageRadius || y == 0 || y == cageRadius * 2) {
                        loc.getBlock().setType(Material.PACKED_ICE);
                    } else {
                        loc.getBlock().setType(Material.WATER);
                    }
                }
            }
        }

        player.sendMessage(ChatColor.GREEN + "You have trapped the entity in an Ice Cage!");


        cageCooldowns.put(playerId, currentTime);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (int x = -cageRadius; x <= cageRadius; x++) {
                    for (int y = 0; y <= cageRadius * 2; y++) {
                        for (int z = -cageRadius; z <= cageRadius; z++) {
                            Location loc = targetLocation.clone().add(x, y, z);
                            loc.getBlock().setType(Material.AIR);
                        }
                    }
                }
                player.sendMessage(ChatColor.GREEN + "The Ice Cage has melted away.");
            }
        }.runTaskLater(plugin, 600);
    }
    public void activateLavaToObsidian(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        if (fireFloorCooldown.containsKey(playerId) && (currentTime - fireFloorCooldown.get(playerId)) < fireFloorCooldownTime) {
            player.sendMessage(ChatColor.RED + "Your " + ChatColor.DARK_RED + "Fiery Floor" + ChatColor.RED + "ability is on cooldown!");
            return;
        }
        int radius = 10;
        World world = player.getWorld();
        Location location = player.getLocation();

        Set<Block> affectedBlocks = new HashSet<>();
        fireFloorCooldown.put(playerId, currentTime);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block block = location.clone().add(x, -1, z).getBlock();
                if (block.getType().isSolid()) {
                    block.setType(Material.LAVA);
                    affectedBlocks.add(block);
                }
            }
        }
        player.sendMessage(ChatColor.GREEN + "You melt the floor with your" + ChatColor.DARK_RED + "Fiery Floor ability");

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block block : affectedBlocks) {
                    if (block.getType() == Material.LAVA) {
                        block.setType(Material.MAGMA_BLOCK);
                    }
                }
            }
        }.runTaskLater(CItems.getInstance(), 20 * 10);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block block : affectedBlocks) {
                    if (block.getType() == Material.LAVA) {
                        block.setType(Material.OBSIDIAN);
                    }
                }
            }
        }.runTaskLater(CItems.getInstance(), 20 * 5);

    }
    public void shootFireball(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        if (fireballCooldown.containsKey(playerId) && (currentTime - fireballCooldown.get(playerId)) < fireballCooldownTime) {
            player.sendMessage(ChatColor.RED + "Your " + ChatColor.DARK_RED + "Fireball" + ChatColor.RED + "ability is on cooldown!");
            return;
        }
        player.launchProjectile(Fireball.class);
        fireballCooldown.put(playerId, currentTime);
        player.sendMessage(ChatColor.GREEN + "You have launched a " + ChatColor.DARK_RED + "Fireball!");

    }
    private void createHealingAura(Player player) {
        Location loc = player.getLocation();
        World world = player.getWorld();
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        if (healingAuraCooldown.containsKey(playerId) && (currentTime - healingAuraCooldown.get(playerId)) < healingAuraCooldownTime) {
            player.sendMessage(ChatColor.RED + "Your " + ChatColor.DARK_GREEN + "Healing Aura" + ChatColor.RED + "ability is on cooldown!");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "A healing aura surrounds you!");
        healingAuraCooldown.put(playerId, currentTime);
        new BukkitRunnable() {
            int duration = 25;

            @Override
            public void run() {
                if (duration <= 0) {
                    this.cancel();
                    return;
                }


                world.spawnParticle(Particle.VILLAGER_HAPPY, loc, 100, 6, 6, 6, 0.1);


                for (Entity entity : world.getNearbyEntities(loc, 6, 6, 6)) {
                    if (entity instanceof LivingEntity) {
                        ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1));
                    }
                }

                duration--;
            }
        }.runTaskTimer(plugin, 0, 20);
    }

}
