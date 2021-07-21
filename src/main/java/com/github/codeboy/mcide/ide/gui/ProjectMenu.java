package com.github.codeboy.mcide.ide.gui;

import com.github.codeboy.mcide.Mcide;
import com.github.codeboy.mcide.config.Message;
import com.github.codeboy.mcide.gui.Gui;
import com.github.codeboy.mcide.ide.CodeProject;
import com.github.codeboy.mcide.ide.MCCodeFile;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ProjectMenu extends Gui {
    private final CodeProject project;

    public ProjectMenu(CodeProject project) {
        super(Mcide.getPlugin(Mcide.class), 54, project.getTitle());
        this.project = project;
        ArrayList<MCCodeFile> mcCodeFiles = project.getMCCodeFiles();
        for (int i = 0, mcCodeFilesLength = mcCodeFiles.size(); i < mcCodeFilesLength; i++) {
            MCCodeFile file = mcCodeFiles.get(i);
            addFile(file, i == 0);
        }

        addItem(createItem(Material.ARROW, Message.BACK), 45, p -> new ProjectSelector(p).open(p));
        addItem(createItem(Material.BANNER, Message.RUN), 46, project::run);
        addItem(createItem(Material.WORKBENCH, Message.CREATE_FILE), 47, project::addFile);
        addItem(createItem(Material.BARRIER, Message.DELETE_FILE), 48, project::removeFile);
    }

    private void addFile(MCCodeFile file, boolean mainFile) {
        ItemStack fileItem = createItem(Material.BOOK_AND_QUILL, file.getName(), mainFile);
        addItem(fileItem, p -> project.editFile(file, p));
    }
}
