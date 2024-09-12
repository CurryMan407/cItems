package org.curryman2.citems.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.curryman2.citems.items.ElementSword;

public class GiveElementSwordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack elementSword = ElementSword.createElementSword();
            player.getInventory().addItem(elementSword);
            player.sendMessage("You have been given the Element Sword!");
            return true;
        }
        return false;
    }
}
