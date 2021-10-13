package com.github.codeboy.mcide.ide.gui;

import com.github.codeboy.mcide.Mcide;
import com.github.codeboy.mcide.config.Message;
import com.github.codeboy.mcide.ide.CodeProject;
import com.github.codeboy.mcide.ide.MCCodeFile;
import ml.codeboy.bukkitbootstrap.gui.Gui;
import ml.codeboy.bukkitbootstrap.gui.MultiPageGui;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ProjectMenu extends MultiPageGui {
    private final CodeProject project;
    private final int filesPerPage = 9 * 4;

    public ProjectMenu(CodeProject project) {
        super(Mcide.getPlugin(Mcide.class), 54, project.getTitle(), (Gui page) -> {
            page.addItem(createItem(Material.ARROW, Message.BACK), 45, p -> new ProjectSelector(p).open(p));
            page.addItem(createItem(Material.BANNER, Message.RUN), 46, project::runDialog);
            page.addItem(createItem(Material.WORKBENCH, Message.CREATE_FILE), 47, project::addFile);
            page.addItem(createItem(Material.BARRIER, Message.DELETE_FILE), 48, project::removeFile);

            page.addItemLast(createItem(Material.BOOK_AND_QUILL, "STDIN"), p -> project.editFile(project.getInput(), p));
            page.addItemLast(createItem(Material.BOOK_AND_QUILL, "ARGS"), p -> project.editFile(project.getArgs(), p));
        });
        this.project = project;
        ArrayList<MCCodeFile> mcCodeFiles = project.getMCCodeFiles();
        for (int i = 0, mcCodeFilesLength = mcCodeFiles.size(); i < mcCodeFilesLength; i++) {
            MCCodeFile file = mcCodeFiles.get(i);
            addFile(file, i == 0);
        }
    }

    @Override
    protected int getNextButtonIndex() {
        return 51;
    }

    @Override
    protected int getPrevButtonIndex() {
        return 50;
    }

    public void addFile(MCCodeFile file, boolean mainFile) {
        ItemStack fileItem = createItem(Material.BOOK_AND_QUILL, file.getName(), mainFile);
        int files = project.getMCCodeFiles().size() - 1;
        int pageNumber = files / filesPerPage;
        int slotNumber = files % filesPerPage;
        ArrayList<Gui> pages = getPages();
        while (pageNumber >= pages.size()) {
            addNextButton(pages.get(pages.size() - 1));
            pages.add(createPage());
            addPrevButton(pages.get(pages.size() - 1));
        }
        Gui page = pages.get(pageNumber);
        page.addItem(fileItem, slotNumber, p -> project.editFile(file, p));
    }

    public void removeFile() {
        int files = project.getMCCodeFiles().size() - 1;
        int pageNumber = files / filesPerPage;
        int slotNumber = files % filesPerPage;
        Gui page = getPages().get(pageNumber);
        page.removeItem(slotNumber);
    }

    private boolean isProjectFile(ItemStack itemStack) {
        return itemStack != null && itemStack.getType() == Material.BOOK_AND_QUILL;
    }
}
