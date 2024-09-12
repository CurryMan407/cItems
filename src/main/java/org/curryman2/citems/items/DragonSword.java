package org.curryman2.citems.items;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.curryman2.citems.CItems;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class DragonSword implements Listener {

    private final JavaPlugin plugin;
    private final HashMap<UUID, Long> dashCooldowns = new HashMap<>();
    private final HashMap<UUID, Long> breathCooldowns = new HashMap<>();

    private final long dashCooldownTime = 30 * 1000;
    private final long breathCooldownTime = 45 * 1000;

    public DragonSword(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static ItemStack createDragonSword() {
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Dragon Sword");

        sword.setItemMeta(meta);
        return sword;
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item != null && item.getType() == Material.NETHERITE_SWORD && item.getItemMeta() != null &&
                item.getItemMeta().getDisplayName().equals(ChatColor.DARK_PURPLE + "Dragon Sword")) {

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                if (player.isSneaking()) {

                    activateDragonBreathReplacement(player);
                } else {

                    activateDash(player);
                }
                event.setCancelled(true);
            }
        }
    }


    public void activateDragonBreathReplacement(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        int radius = 6;
        if (breathCooldowns.containsKey(playerId) && (currentTime - breathCooldowns.get(playerId)) < breathCooldownTime) {
            player.sendMessage(ChatColor.RED + "Dragon Breath ability is on cooldown!");
            return;
        }
        player.sendMessage(ChatColor.DARK_PURPLE + "You have unleashed a powerful aura!");
        new BukkitRunnable() {

            int duration = 20;

            @Override
            public void run() {
                if (duration <= 0) {
                    this.cancel();
                    return;
                }

                Location location = player.getLocation();
                World world = player.getWorld();


                world.spawnParticle(Particle.SPELL_WITCH, location, 100, radius, radius, radius, 0.1);


                for (Entity entity : world.getNearbyEntities(location, radius, radius, radius)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        ((LivingEntity) entity).damage(1.0);
                    }
                }

                duration--;
            }
        }.runTaskTimer(CItems.getInstance(), 0, 20);
        breathCooldowns.put(playerId, currentTime);
    }


    private void activateDash(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (dashCooldowns.containsKey(playerId) && (currentTime - dashCooldowns.get(playerId)) < dashCooldownTime) {
            player.sendMessage(ChatColor.RED + "Dragon Dash ability is on cooldown!");
            return;
        }


        Vector direction = player.getLocation().getDirection().multiply(4);
        player.setVelocity(direction);
        player.sendMessage(ChatColor.GREEN + "You used " + ChatColor.DARK_PURPLE + "Dragon Dash!");


        dashCooldowns.put(playerId, currentTime);
    }

    private void activateDragonBreath(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();

        if (breathCooldowns.containsKey(playerId) && (currentTime - breathCooldowns.get(playerId)) < breathCooldownTime) {
            player.sendMessage(ChatColor.RED + "Dragon Breath ability is on cooldown!");
            return;
        }

        Location playerLocation = player.getLocation();
        int radius = 15;


        new BukkitRunnable() {
            int duration = 20 * 20;
            @Override
            public void run() {
                if (duration <= 0) {
                    cancel();
                    return;
                }

                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        Location loc = playerLocation.clone().add(x, 0, z);
                        if (loc.distance(playerLocation) <= radius) {
                            player.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc.add(0, 1, 0), 1);

                            player.getWorld().getNearbyEntities(loc, 1, 1, 1).stream()
                                    .filter(entity -> entity instanceof Player && !entity.equals(player))
                                    .forEach(entity -> ((Player) entity).damage(2)); // Same damage as dragon breath
                        }
                    }
                }

                duration -= 10;
            }
        }.runTaskTimer(plugin, 0, 10);

        player.sendMessage(ChatColor.GREEN + "You flooded the area with " + ChatColor.DARK_PURPLE + "Dragon Breath!");


        breathCooldowns.put(playerId, currentTime);
    }
}
