package com.github.codeboy.mcide.commands;

import com.github.codeboy.mcide.ide.gui.ProjectSelector;
import ml.codeboy.bukkitbootstrap.CustomItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BoundItemCommand implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        CustomItem item = CustomItem.getItem("Open menu");
        if (item != null) {
            player.getInventory().addItem(item.getItem());
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        CustomItem customItem = CustomItem.getItem("Open menu");
        if (customItem != null && customItem.itemIsInstance(item) && player.getOpenInventory() != null) {
            ProjectSelector menu = new ProjectSelector(player);
            menu.open(player);
            event.setCancelled(true);
        }
    }
}
