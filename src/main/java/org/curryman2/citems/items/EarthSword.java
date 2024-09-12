package org.curryman2.citems.items;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;


public class EarthSword implements Listener {

    private final JavaPlugin plugin;
    private final HashMap<UUID, Long> healingAuraCooldown = new HashMap<>();
    private final HashMap<UUID, Long> earthSpikeCooldown = new HashMap<>();
    private final long healingAuraCooldownTime = 35 * 1000;
    private final long earthSpikeCooldownTime = 60 * 1000;

    public EarthSword(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    public static ItemStack createEarthSword() {
        ItemStack sword = new ItemStack(Material.STONE_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Earth Sword");

        sword.setItemMeta(meta);
        return sword;
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.STONE_SWORD && item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Earth Sword")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 0, true, false));
        }
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();


        if (item != null && item.getType() == Material.STONE_SWORD && item.getItemMeta() != null &&
                item.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Earth Sword")) {

            if (event.getAction().toString().contains("RIGHT_CLICK_BLOCK") || event.getAction().toString().contains("RIGHT_CLICK_AIR")) {
                if (player.isSneaking()) {
                    createHealingAura(player);

                } else {
                    Entity target = getNearestEntityInSight(player, 10);
                    if (target != null) {
                        launchWithEarthSpike(player, target);
                    } else {
                        player.sendMessage(ChatColor.RED + "No target found within range.");
                    }
                }
                event.setCancelled(true);
            }
        }
    }


    private void launchWithEarthSpike(Player player, Entity target) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        Location loc = target.getLocation();
        Block block = loc.getBlock();
        if (earthSpikeCooldown.containsKey(playerId) && (currentTime - earthSpikeCooldown.get(playerId)) < earthSpikeCooldownTime) {
            player.sendMessage(ChatColor.RED + "Your " + ChatColor.DARK_GREEN + "Earth Spike" + ChatColor.RED + "ability is on cooldown!");
            return;
        }

        target.setVelocity(new Vector(-4, 4, -4));
        block.setType(Material.DIRT);
        block.getRelative(0, 1, 0).setType(Material.DIRT);
        block.getRelative(0, 2, 0).setType(Material.DIRT);
        block.getRelative(2, 2, 3).setType(Material.DIRT);

        player.sendMessage(ChatColor.GREEN + "You manipulate the ground to launch " + target.getName() + "!");
        earthSpikeCooldown.put(playerId, currentTime);
        new BukkitRunnable() {
            @Override
            public void run() {
                block.setType(Material.AIR);
                block.getRelative(0, 1, 0).setType(Material.AIR);
                block.getRelative(0, 2, 0).setType(Material.AIR);
            }
        }.runTaskLater(plugin, 200);

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
}
