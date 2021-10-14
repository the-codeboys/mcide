package com.github.codeboy.mcide.commands;

import com.github.codeboy.mcide.config.Message;
import com.github.codeboy.mcide.ide.gui.ProjectSelector;
import com.github.codeboy.mcide.services.CustomItemEventManager;
import ml.codeboy.bukkitbootstrap.CustomItem;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

public class BoundItemCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        CustomItem customItem =  CustomItem.createItemOrGet(player.getPlayerListName(), Material.GOLD_HOE, (short) 0);
        player.getInventory().addItem(customItem.getItem());

        CustomItemEventManager.addInteraction(customItem, event -> {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Player p = event.getPlayer();
                event.setCancelled(true);
                ProjectSelector menu = new ProjectSelector(p);
                menu.open(p);
                event.setCancelled(true);
            }
        });
        return true;
    }
}
