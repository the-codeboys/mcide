package com.github.codeboy.mcide.gui;

import org.bukkit.entity.Player;

public interface Action {
    Action none = (p) -> {
    };

    void execute(Player player);
}
