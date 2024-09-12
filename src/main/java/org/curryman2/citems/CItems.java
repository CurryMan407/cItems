package org.curryman2.citems;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.curryman2.citems.commands.*;
import org.curryman2.citems.items.*;
import org.curryman2.citems.recipes.CraftingRecipes;

public class CItems extends JavaPlugin {
    private static CItems instance;
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new DragonSword(this), this);
        getServer().getPluginManager().registerEvents(new WaterSword(this), this);
        getServer().getPluginManager().registerEvents(new FireSword(this), this);
        getServer().getPluginManager().registerEvents(new EarthSword(this), this);
        getServer().getPluginManager().registerEvents(new ElementSword(this), this);
        getServer().getPluginManager().registerEvents(new DevilScythe(this), this);
        getServer().getPluginManager().registerEvents(new KingsCrown(this), this);
        getServer().getPluginManager().registerEvents(new KingsSword(this), this);
        getServer().getPluginManager().registerEvents(new CraftingRecipes(this), this);
        instance = this;
        getCommand("givedragonsword").setExecutor(new GiveDragonSwordCommand());
        getCommand("givefiresword").setExecutor(new GiveFireSwordCommand());
        getCommand("givewatersword").setExecutor(new GiveWaterSwordCommand());
        getCommand("giveearthsword").setExecutor(new GiveEarthSwordCommand());
        getCommand("giveelementsword").setExecutor(new GiveElementSwordCommand());
        getLogger().info("cItems has been enabled!");

        saveDefaultConfig();
        CraftingRecipes.loadRecipes(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("cItems plugin has been disabled.");
    }
    public static CItems getInstance() {
        return instance;
    }



}
