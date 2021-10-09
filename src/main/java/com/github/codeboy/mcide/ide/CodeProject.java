package com.github.codeboy.mcide.ide;

import com.github.codeboy.mcide.Mcide;
import com.github.codeboy.mcide.config.Message;
import com.github.codeboy.mcide.ide.gui.ProjectMenu;
import com.github.codeboy.piston4j.api.*;
import com.github.codeboy.piston4j.api.Runtime;
import com.github.codeboy.piston4j.exceptions.PistonException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class CodeProject {

    private final ArrayList<MCCodeFile> MCCodeFiles;
    private String language;
    private String title;
    private UUID ownerId;

    public CodeProject(String language, String title, UUID ownerId, MCCodeFile... MCCodeFiles) {
        this.language = language;
        this.title = title;
        this.MCCodeFiles = new ArrayList<>(Arrays.asList(MCCodeFiles));
        if (MCCodeFiles.length == 0) {
            this.MCCodeFiles.add(new MCCodeFile());
        }
        this.ownerId = ownerId;
        getOwner().getProjects().add(this);
    }

    public void init() {
        for (MCCodeFile file : getMCCodeFiles())
            file.setProject(this);
    }

    //region getter and setter
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<MCCodeFile> getMCCodeFiles() {
        return MCCodeFiles;
    }

    public CodePlayer getOwner() {
        return CodePlayer.getCodePlayer(ownerId);
    }

    public void setOwner(CodePlayer owner) {
        this.ownerId = owner.getPlayerId();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
    //endregion

    public ExecutionResult run() {
        Piston piston = Mcide.getPiston();
        Runtime runtime = piston.getRuntimeUnsafe(getLanguage());
        if (runtime == null)
            throw new PistonException("Runtime not found");
        return runtime.execute(getMCCodeFiles().stream().map(MCCodeFile::toCodeFile).toArray(CodeFile[]::new));
    }

    public void run(Player player) {
        CodeProject project = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendMessage(Message.createMessage(Message.EXECUTION_START, getTitle()));
                ExecutionResult result = project.run();
                ExecutionOutput output = result.getOutput();
                if (output.getOutput() == null && output.getOutput().length() == 0) {
                    player.sendMessage(Message.createMessage(Message.RUN_PROJECT_ERROR_OUTPUT, getTitle()));
                    player.sendMessage(output.getStderr());
                } else {
                    player.sendMessage(Message.createMessage(Message.RUN_PROJECT_SUCCESS, getTitle()));
                    player.sendMessage(output.getOutput());
                }
            }
        }.runTaskLater(Mcide.getPlugin(Mcide.class), 0);
    }

    public void save() {
        getOwner().save();
    }

    public boolean isOwner(Player player) {
        return player.getUniqueId().equals(getOwner().getPlayerId());
    }

    public void open(Player player) {
        if (!isOwner(player)) {
            player.sendMessage(Message.NOT_PROJECT_OWNER);
            return;
        }
        ProjectMenu menu = new ProjectMenu(this);
        menu.open(player);
    }

    public void editFile(MCCodeFile file, Player player) {
        if (!isOwner(player)) {
            player.sendMessage(Message.NOT_PROJECT_OWNER);
            return;
        }
        ItemStack book = new ItemStack(Material.BOOK_AND_QUILL);
        ItemMeta meta = book.getItemMeta();

        BookMeta bookMeta = (BookMeta) meta;
        for (String page : file.getContent())
            bookMeta.addPage(page);
        bookMeta.setTitle(file.getName());
        bookMeta.setAuthor(player.getName());
        book.setItemMeta(bookMeta);

        ItemStack oldItem = player.getInventory().getItemInHand();

        player.getInventory().setItemInHand(book);
        player.sendMessage(Message.RIGHT_CLICK_TO_EDIT);
        player.closeInventory();

        Bukkit.getPluginManager().registerEvents(new Listener() {
            //region methods
            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return super.equals(obj);
            }

            @Override
            protected Object clone() throws CloneNotSupportedException {
                return super.clone();
            }

            @Override
            public String toString() {
                return super.toString();
            }

            @Override
            protected void finalize() throws Throwable {
                super.finalize();
            }
            //endregion

            @EventHandler
            void onClick(InventoryClickEvent e) {
                if (book.equals(e.getCurrentItem()))
                    e.setCancelled(true);
            }

            @EventHandler
            void onDrop(PlayerDropItemEvent e) {
                if (book.equals(e.getItemDrop().getItemStack()))
                    e.setCancelled(true);
            }

            @EventHandler
            void onClick(PlayerMoveEvent e) {
                if (player.equals(e.getPlayer()) && !e.getFrom().getBlock().equals(e.getTo().getBlock())) {
                    reset();
                    player.sendMessage(Message.createMessage(Message.EDIT_CANCELLED, file.getName()));
                }
            }

            void reset() {
                Listener listener = this;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.getInventory().setItemInHand(oldItem);
                        HandlerList.unregisterAll(listener);
                        new ProjectMenu(file.getProject()).open(player);
                    }
                }.runTaskLater(Mcide.getPlugin(Mcide.class), 1);
            }

            @EventHandler
            void onEdit(PlayerEditBookEvent e) {
                if (e.getPreviousBookMeta().equals(bookMeta)) {
                    BookMeta newMeta = e.getNewBookMeta();
                    file.setContent(newMeta.getPages().toArray(new String[0]));
                    file.setName(newMeta.getTitle());
                    save();
                    player.sendMessage(Message.createMessage(Message.EDIT_SUCCESS, file.getName()));
                    reset();
                }
            }
        }, Mcide.getPlugin(Mcide.class));
    }

    public void addFile(Player p) {
        MCCodeFile file = new MCCodeFile();
        getMCCodeFiles().add(file);
        file.setProject(this);
        new ProjectMenu(this).open(p);
    }

    public void removeFile(Player p) {
        int size = getMCCodeFiles().size();
        if (size > 1) {
            getMCCodeFiles().remove(size - 1);
            new ProjectMenu(this).open(p);
        }
    }
}
