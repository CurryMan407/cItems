package org.curryman2.citems.items;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class WaterSword implements Listener {

    private final JavaPlugin plugin;
    private final HashMap<UUID, Long> cageCooldowns = new HashMap<>();
    private final long cageCooldownTime = 60 * 1000;

    public WaterSword(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static ItemStack createWaterSword() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE + "Water Sword");

        sword.setItemMeta(meta);
        return sword;
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.DIAMOND_SWORD && item.getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Water Sword")) {
            if (player.getLocation().getBlock().getType() == Material.WATER) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 200, 0, true, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 0, true, false));
            }
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();


        if (item != null && item.getType() == Material.DIAMOND_SWORD && item.getItemMeta() != null &&
                item.getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Water Sword")) {


            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {


                if (player.isSneaking()) {


                    Entity target = getNearestEntityInSight(player, 10);

                    if (target != null) {
                        encaseInIceCage(player, target);
                        player.sendMessage(ChatColor.AQUA + "You have trapped " + target.getName() + " in an ice cage!");
                        event.setCancelled(true);
                    } else {
                        player.sendMessage(ChatColor.RED + "No target found within range.");
                    }
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
}
