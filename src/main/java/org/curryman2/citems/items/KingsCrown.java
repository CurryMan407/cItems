package org.curryman2.citems.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.plugin.java.JavaPlugin;

public class KingsCrown implements Listener {

    private final JavaPlugin plugin;
    private boolean crafted = false;

    public KingsCrown(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack helmet = player.getInventory().getHelmet();

        if (helmet != null && helmet.getType() == Material.GOLDEN_HELMET && helmet.getItemMeta() != null &&
                helmet.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "King's Crown")) {

            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 2, true, false, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 2, true, false, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 40, 2, true, false, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 3, true, false, true));
            if (player.getMaxHealth() == 20.0) {
                player.setMaxHealth(30.0);
            }
        } else if (player.getMaxHealth() == 30.0) {
            player.setMaxHealth(20.0);
        }
    }

    @EventHandler
    public void onPlayerHold(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack helmet = player.getInventory().getHelmet();

        if (helmet != null && helmet.getType() == Material.GOLDEN_HELMET && helmet.getItemMeta() != null &&
                helmet.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "King's Crown")) {


            ItemMeta meta = helmet.getItemMeta();
            if (meta != null) {
                meta.setUnbreakable(true);
                helmet.setItemMeta(meta);
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ItemStack helmet = player.getInventory().getHelmet();

            if (helmet != null && helmet.getType() == Material.GOLDEN_HELMET && helmet.getItemMeta() != null &&
                    helmet.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "King's Crown")) {
                event.setDamage(event.getDamage() * 0.5);
            }
        }
    }
}
