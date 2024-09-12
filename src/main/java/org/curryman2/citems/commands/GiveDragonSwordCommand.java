package org.curryman2.citems.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.curryman2.citems.items.DragonSword;

public class GiveDragonSwordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack dragonSword = DragonSword.createDragonSword();
            player.getInventory().addItem(dragonSword);
            player.sendMessage("You have been given the Dragon Sword!");
            return true;
        }
        return false;
    }
}
