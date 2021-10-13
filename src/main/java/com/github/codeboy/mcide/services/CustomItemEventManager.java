package com.github.codeboy.mcide.services;

import ml.codeboy.bukkitbootstrap.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.function.Consumer;

public class CustomItemEventManager implements Listener {

    private static final HashMap<CustomItem, Consumer<PlayerInteractEvent>> interactions = new HashMap<>();

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        for(CustomItem customItem:interactions.keySet()){
            if (customItem.itemIsInstance(item)) {
                interactions.get(customItem).accept(event);
            }
        }
    }

    public static boolean addInteraction(CustomItem customItem,Consumer<PlayerInteractEvent> interaction){
        if(interactions.containsKey(customItem)){
            return false;
        }
        interactions.put(customItem,interaction);
        return true;
    }
}
