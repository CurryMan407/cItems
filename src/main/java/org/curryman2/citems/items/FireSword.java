package org.curryman2.citems.items;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.EventHandler;
import org.curryman2.citems.CItems;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class FireSword implements Listener {

    private final JavaPlugin plugin;
    private final HashMap<UUID, Long> fireballCooldown = new HashMap<>();
    private final HashMap<UUID, Long> fireFloorCooldown = new HashMap<>();
    private final long fireballCooldownTime = 15 * 1000;
    private final long fireFloorCooldownTime = 60 * 1000;

    public FireSword(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    public static ItemStack createFireSword() {
        ItemStack sword = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Fire Sword");

        sword.setItemMeta(meta);
        return sword;
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item != null && item.getType() == Material.GOLDEN_SWORD && item.getItemMeta() != null && item.getItemMeta().getDisplayName().equals(ChatColor.RED + "Fire Sword")) {

            if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
                if (player.isSneaking()) {

                    activateLavaToObsidian(player);

                } else {

                    shootFireball(player);

                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.NETHERITE_SWORD && item.getItemMeta().getDisplayName().equals(ChatColor.RED + "Fire Sword")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200, 0, true, false));
        }
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



}
