package dev.customitem.command;

import dev.customitem.custom.ArrowAssaultRifle;
import dev.customitem.custom.CustomItemManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArrowAssaultRifleCommand implements CommandExecutor {

    public static final String COMMAND = "arrow-assault-rifle";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player) {

            ItemStack itemStack = CustomItemManager.createCustomItemStack(ArrowAssaultRifle.class);

            if (itemStack != null) {

                ((Player) commandSender).getInventory().addItem(itemStack);
            }
        }

        return false;
    }

}
