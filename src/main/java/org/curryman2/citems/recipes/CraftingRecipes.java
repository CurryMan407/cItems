package org.curryman2.citems.recipes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.curryman2.citems.CItems;

public class CraftingRecipes implements Listener {

    private final JavaPlugin plugin;

    public CraftingRecipes(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    public static void loadRecipes(JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();


        addRecipe(plugin, "water_sword", Material.DIAMOND_SWORD, "&9Water Sword", 1000);
        addRecipe(plugin, "fire_sword", Material.GOLDEN_SWORD, "&cFire Sword", 1001);
        addRecipe(plugin, "earth_sword", Material.STONE_SWORD, "&aEarth Sword", 1002);
        addRecipe(plugin, "dragon_sword", Material.NETHERITE_SWORD, "&5Dragon Sword", 1003);
        addRecipe(plugin, "element_sword", Material.IRON_SWORD, "&2Element Sword", 1004);
        addRecipe(plugin, "devil_scythe", Material.NETHERITE_AXE, "&4Devil's Scythe", 1005);
        addRecipe(plugin, "kings_crown", Material.GOLDEN_HELMET, "&6King's Crown", 1006);
        addRecipe(plugin, "kings_sword", Material.DIAMOND_SWORD, "&6King's Sword", 1007);
    }

    private static void addRecipe(JavaPlugin plugin, String path, Material material, String displayName, int modelData) {
        FileConfiguration config = plugin.getConfig();
        String[] shape = config.getStringList("recipes." + path + ".shape").toArray(new String[0]);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, path), createItem(material, displayName, modelData));
        recipe.shape(shape[0], shape[1], shape[2]);

        for (String key : config.getConfigurationSection("recipes." + path + ".ingredients").getKeys(false)) {
            Material ingredient = Material.matchMaterial(config.getString("recipes." + path + ".ingredients." + key));
            recipe.setIngredient(key.charAt(0), ingredient);
        }

        Bukkit.addRecipe(recipe);
    }

    private static ItemStack createItem(Material material, String displayName, int modelData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
            meta.setUnbreakable(true);
            meta.setCustomModelData(modelData);
            item.setItemMeta(meta);
        }

        return item;
    }
    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getRecipe().getResult();
        if (result != null && result.hasItemMeta() && result.getItemMeta().hasDisplayName()) {
            String displayName = ChatColor.stripColor(result.getItemMeta().getDisplayName()).replace(" ", "_").toLowerCase();

            if (isItemAlreadyCrafted(displayName)) {
                event.getInventory().setResult(null);
                event.getViewers().forEach(viewer -> viewer.sendMessage(ChatColor.RED + "This item can only be crafted once, and has already been crafted!"));
            }
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        ItemStack result = event.getRecipe().getResult();
        if (result != null && result.hasItemMeta() && result.getItemMeta().hasDisplayName()) {
            String displayName = ChatColor.stripColor(result.getItemMeta().getDisplayName()).replace(" ", "_").toLowerCase();

            if (!isItemAlreadyCrafted(displayName)) {
                markItemAsCrafted(displayName);
            }
        }
    }

    private boolean isItemAlreadyCrafted(String itemName) {
        FileConfiguration config = plugin.getConfig();
        return config.getBoolean("crafted_items." + itemName, false);
    }

    private void markItemAsCrafted(String itemName) {
        FileConfiguration config = plugin.getConfig();
        config.set("crafted_items." + itemName, true);
        plugin.saveConfig();
    }
}

