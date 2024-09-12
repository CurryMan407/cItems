package org.curryman2.citems.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.curryman2.citems.items.FireSword;

public class GiveFireSwordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack fireSword = FireSword.createFireSword();
            player.getInventory().addItem(fireSword);
            player.sendMessage("You have been given the Fire Sword!");
            return true;
        }
        return false;
    }
}
