package com.github.codeboy.mcide;

import com.github.codeboy.mcide.commands.BoundItemCommand;
import com.github.codeboy.mcide.commands.CreateProject;
import com.github.codeboy.mcide.commands.IdeCommand;
import com.github.codeboy.mcide.commands.RunCommand;
import com.github.codeboy.mcide.config.Config;
import com.github.codeboy.mcide.config.Message;
import com.github.codeboy.mcide.services.CustomItemEventManager;
import com.github.codeboy.piston4j.api.ExecutionOutput;
import com.github.codeboy.piston4j.api.ExecutionResult;
import com.github.codeboy.piston4j.api.Piston;
import com.google.gson.Gson;
import ml.codeboy.bukkitbootstrap.CustomItem;
import ml.codeboy.bukkitbootstrap.config.ConfigReader;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.management.InstanceAlreadyExistsException;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public final class Mcide extends JavaPlugin {

    private static Gson gson;
    private Piston piston;

    public static Gson gson() {
        return gson == null ? (gson = new Gson()) : gson;
    }

    public static Piston getPiston() {
        return getPlugin(Mcide.class).piston;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        readConfigs();

        piston = Piston.getInstance(Config.pistonEndPoint);
        piston.setApiKey(Config.pistonApiKey);

        getCommand("run").setExecutor(new RunCommand());
        getCommand("ide").setExecutor(new IdeCommand());
        getCommand("create-project").setExecutor(new CreateProject());
        getCommand("bound-item").setExecutor(new BoundItemCommand());
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent event) {
                event.getPlayer().setResourcePack("https://github.com/the-codeboy/mcide/releases/download/latest/mcide.zip");
            }
        }, this);

        getServer().getPluginManager().registerEvents(new CustomItemEventManager(), Mcide.getPlugin(Mcide.class));

    }

    private void readConfigs() {
        File configFile = new File(getDataFolder().getPath() + File.separator + "config.yml");
        ConfigReader.readConfig(Config.class, configFile);

        File languageFile = new File(getDataFolder().getPath() + File.separator + "languages" + File.separator + Config.language + ".yml");
        ConfigReader.readConfig(Message.class, languageFile);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equals("run")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "requires two arguments: language and code");
                return true;
            }
            String language = args[0];

            List<String> arguments = Arrays.asList(args);
            arguments.remove(0);

            String code = String.join("", arguments);

            ExecutionResult result = Mcide.getPiston().execute(language, code);
            ExecutionOutput output = result.getOutput();
            sender.sendMessage(output.getOutput());
            System.out.println(result);
        }
        return true;
    }

    public String getProjectsPath() {
        String pluginFolder = getDataFolder().getPath();
        String projectsFolder = pluginFolder + File.separator + "projects" + File.separator;
        return projectsFolder;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
