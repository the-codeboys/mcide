package com.github.codeboy.mcide.ide.gui;

import com.github.codeboy.mcide.Mcide;
import com.github.codeboy.mcide.config.Message;
import com.github.codeboy.mcide.ide.CodePlayer;
import com.github.codeboy.mcide.ide.CodeProject;
import ml.codeboy.bukkitbootstrap.gui.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ProjectSelector extends Gui {
    public ProjectSelector(Player player) {
        this(CodePlayer.getCodePlayer(player));
    }

    public ProjectSelector(CodePlayer player) {
        super(Mcide.getPlugin(Mcide.class), 54, Message.PROJECTS);
        for (CodeProject project : player.getProjects()) {
            addProject(project);
        }
    }

    private void addProject(CodeProject project) {
        ItemStack projectItem = createItem(Material.BOOKSHELF, project.getTitle(), project.getLanguage());
        addItem(projectItem, project::open);
    }
}
