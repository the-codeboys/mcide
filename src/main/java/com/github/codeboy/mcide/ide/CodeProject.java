package com.github.codeboy.mcide.ide;

import com.github.codeboy.mcide.Mcide;
import com.github.codeboy.mcide.config.Message;
import com.github.codeboy.mcide.ide.gui.ProjectMenu;
import com.github.codeboy.piston4j.api.*;
import com.github.codeboy.piston4j.api.Runtime;
import com.github.codeboy.piston4j.exceptions.PistonException;
import ml.codeboy.bukkitbootstrap.gui.Gui;
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
    private final MCCodeFile input;
    private final MCCodeFile args;

    private transient ProjectMenu projectMenu;

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
        args = new MCCodeFile("Args");
        input = new MCCodeFile("Input");
    }

    public void init() {
        for (MCCodeFile file : getMCCodeFiles())
            file.setProject(this);
    }

    private void openProjectMenu(Player player) {
        getProjectMenu().open(player);
    }

    //region getter and setter


    private ProjectMenu getProjectMenu() {
        return projectMenu == null ? (projectMenu = new ProjectMenu(this)) : projectMenu;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<MCCodeFile> getMCCodeFiles() {
        return MCCodeFiles;
    }


    public MCCodeFile getInput() {
        return input;
    }

    public MCCodeFile getArgs() {
        return args;
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

    public ExecutionResult runToChat() {
        Piston piston = Mcide.getPiston();
        Runtime runtime = piston.getRuntimeUnsafe(getLanguage());
        if (runtime == null)
            throw new PistonException("Runtime not found");

        ExecutionRequest request = new ExecutionRequest(runtime.getLanguage(), runtime.getVersion(), getMCCodeFiles().stream().map(MCCodeFile::toCodeFile).toArray(CodeFile[]::new));
        if (input != null) {
            request.setStdin(String.join("", input.getContent()));
        }
        if (args != null) {
            request.setArgs(args.getContent());
        }
        return piston.execute(request);
    }

    private ExecutionResult beforeRun(CodeProject project, Player player) {
        player.sendMessage(Message.createMessage(Message.EXECUTION_START, getTitle()));
        return project.runToChat();
    }

    private boolean onlyErrorResult(ExecutionResult result) {
        ExecutionOutput output = result.getOutput();
        ExecutionOutput compileOutput = result.getCompileOutput();
        return output.getStderr().length() != 0
                || (compileOutput != null && compileOutput.getStderr().length() != 0);
    }

    public void runToChat(Player player) {

        CodeProject project = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                ExecutionResult result = beforeRun(project, player);
                ExecutionOutput output = result.getOutput();
                ExecutionOutput compileOutput = result.getCompileOutput();
                String combinedOutput = compileOutput != null ? compileOutput.getOutput() : "" + output.getOutput();
                boolean failed = onlyErrorResult(result);
                if (failed) {
                    player.sendMessage(Message.createMessage(Message.RUN_PROJECT_ERROR_OUTPUT, getTitle()));
                } else {
                    player.sendMessage(Message.createMessage(Message.RUN_PROJECT_SUCCESS, getTitle()));
                }
                player.sendMessage(combinedOutput);
            }
        }.runTaskLater(Mcide.getPlugin(Mcide.class), 0);
    }

    public void runToBook(Player player) {
        CodeProject project = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                ExecutionResult result = beforeRun(project, player);
                ExecutionOutput output = result.getOutput();
                ExecutionOutput compileOutput = result.getCompileOutput();
                String combinedOutput = compileOutput != null ? compileOutput.getOutput() : "" + output.getOutput();
                boolean failed = onlyErrorResult(result);

                ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
                ItemMeta meta = book.getItemMeta();

                BookMeta bookMeta = (BookMeta) meta;

                StringBuilder builder = new StringBuilder();
                int lineNumber = 0;
                int charCount = 0;
                for (String world : combinedOutput.split(" ")) {
                    charCount += world.length() + 1;
                    if (charCount < 19) {
                        builder.append(world).append(' ');
                    } else {
                        if (lineNumber > 12) {
                            lineNumber = 0;
                            bookMeta.addPage(builder.toString());
                            builder.setLength(0);
                        }
                        builder.append("\n").append(world).append(' ');
                        charCount = 0;
                        lineNumber++;
                    }
                }
                if (builder.length() > 0) {
                    bookMeta.addPage(builder.toString());
                }
                bookMeta.setTitle(failed ? "FAILED" : "OUTPUT");
                bookMeta.setAuthor("Piston");
                book.setItemMeta(bookMeta);

                player.getInventory().addItem(book);
            }
        }.runTaskLater(Mcide.getPlugin(Mcide.class), 0);
    }

    public void runDialog(Player player) {
        Gui options = new Gui(Mcide.getPlugin(Mcide.class), 9, Message.RUN_OPTION_TITLE);
        options.addItem(Gui.createItem(Material.LOG, Message.RUN_CHAT_OPTION, Message.RUN_CHAT_OPTION_LORE), this::runToChat);
        options.addItem(Gui.createItem(Material.BOOK, Message.RUN_BOOK_OPTION, Message.RUN_BOOK_OPTION_LORE), this::runToBook);
        options.addItem(Gui.createItem(Material.COMMAND, Message.RUN_AND_EXECUTE_OPTION, Message.RUN_AND_EXECUTE_OPTION_LORE), this::runAndExecute);
        options.open(player);
    }

    private void runAndExecute(Player player) {
        CodeProject project = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                ExecutionResult result = beforeRun(project, player);
                ExecutionOutput output = result.getOutput();
                ExecutionOutput compileOutput = result.getCompileOutput();
                String combinedOutput = compileOutput != null ? compileOutput.getOutput() : "" + output.getOutput();
                boolean failed = onlyErrorResult(result);
                if(failed){
                    player.sendMessage(Message.createMessage(Message.RUN_PROJECT_ERROR_OUTPUT,project.getTitle())+combinedOutput);
                }else{
                    player.performCommand(combinedOutput.trim());
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
        openProjectMenu(player);
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
                        file.getProject().open(player);
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
        getProjectMenu().addFile(file, false);
    }

    public void removeFile(Player p) {
        int size = getMCCodeFiles().size();
        if (size > 1) {
            getMCCodeFiles().remove(size - 1);
            getProjectMenu().removeFile();
        }
    }
}
