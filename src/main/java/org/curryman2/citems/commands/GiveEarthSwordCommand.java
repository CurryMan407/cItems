package org.curryman2.citems.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.curryman2.citems.items.EarthSword;

public class GiveEarthSwordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack earthSword = EarthSword.createEarthSword();
            player.getInventory().addItem(earthSword);
            player.sendMessage("You have been given the Earth Sword!");
            return true;
        }
        return false;
    }
}
