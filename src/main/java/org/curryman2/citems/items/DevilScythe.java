package org.curryman2.citems.items;

import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class DevilScythe implements Listener {

    private final JavaPlugin plugin;
    private final HashMap<UUID, Long> dashCooldowns = new HashMap<>();
    private final HashMap<UUID, Long> auraCooldowns = new HashMap<>();
    private final long dashCooldownTime = 30 * 1000;
    private final long auraCooldownTime = 45 * 1000;

    public DevilScythe(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item != null && item.getType() == Material.NETHERITE_AXE && item.getItemMeta() != null &&
                item.getItemMeta().getDisplayName().equals(ChatColor.DARK_RED + "Devil's Scythe")) {

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                if (player.isSneaking()) {
                    createDevilAura(player);
                } else {
                    activateDash(player);
                }
                event.setCancelled(true);
            }
        }
    }

    private void activateDash(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        if (dashCooldowns.containsKey(playerId) && (currentTime - dashCooldowns.get(playerId)) < dashCooldownTime) {
            player.sendMessage(ChatColor.RED + "Dragon Breath ability is on cooldown!");
            return;
        }
        player.setVelocity(player.getLocation().getDirection().multiply(4).setY(0.5));
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.0f);
        player.sendMessage(ChatColor.DARK_RED + "You dash forward!");
        dashCooldowns.put(playerId, currentTime);
    }

    private void createDevilAura(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        if (auraCooldowns.containsKey(playerId) && (currentTime - auraCooldowns.get(playerId)) < auraCooldownTime) {
            player.sendMessage(ChatColor.RED + "Devil's Aura ability is on cooldown!");
            return;
        }
        player.sendMessage(ChatColor.DARK_RED + "You release the Devil's Aura into the world");
        auraCooldowns.put(playerId, currentTime);
        new BukkitRunnable() {
            int duration = 15;
            int radius = 7;

            @Override
            public void run() {
                if (duration <= 0) {
                    this.cancel();
                    return;
                }
                for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 7, 7, 7)) {
                    if (entity instanceof Player && !entity.equals(player)) {
                        Player target = (Player) entity;
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, 1));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
                    }
                }


                player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, player.getLocation(), 100, 7, 7, 7, 0.1);

                duration--;
            }
        }.runTaskTimer(plugin, 0, 20);
    }
}
