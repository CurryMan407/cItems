package org.curryman2.citems.items;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class KingsSword implements Listener {
    private JavaPlugin plugin;
    private static final String SWORD_NAME = ChatColor.GOLD + "King's Sword";
    private static final int MAX_HEARTS = 25;

    public KingsSword(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.DIAMOND_SWORD && item.getItemMeta().getDisplayName().equals(SWORD_NAME)) {
            if (event.getAction().toString().contains("RIGHT_CLICK_AIR") || event.getAction().toString().contains("RIGHT_CLICK_BLOCK")) {
                if (player.isSneaking()) {

                    activateDash(player);
                }
            }
        }
    }
    private void activateDash(Player player) {

        player.setVelocity(player.getLocation().getDirection().multiply(4));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1f, 1f);
    }
    @EventHandler
    public void onPlayerKill(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player killed = (Player) entity;
            Player killer = killed.getKiller();

            if (killer != null) {
                ItemStack itemInHand = killer.getInventory().getItemInMainHand();
                if (itemInHand != null && itemInHand.getType() == Material.DIAMOND_SWORD
                        && itemInHand.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "King's Sword")) {


                    double currentHealth = killer.getMaxHealth();
                    if (currentHealth < 50.0) {
                        killer.setMaxHealth(currentHealth + 2.0);
                    }


                }
            }
        }
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();


        boolean hasKingsSword = player.getInventory().containsAtLeast(new ItemStack(Material.DIAMOND_SWORD), 1);
        if (hasKingsSword) {

            double currentHealth = player.getMaxHealth();
            if (currentHealth > 20.0) {
                player.setMaxHealth(currentHealth - 2.0);
            }


            event.getDrops().removeIf(item -> item.getType() == Material.DIAMOND_SWORD
                    && item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "King's Sword"));
        }
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.DIAMOND_SWORD && item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "King's Sword")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 4, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 1, true, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 2, true, false));
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if (item != null && item.getType() == Material.DIAMOND_SWORD
                && item.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "King's Sword")) {


            if (!player.getInventory().contains(item)) {
                player.setMaxHealth(20.0);
            }
        }
    }

}
