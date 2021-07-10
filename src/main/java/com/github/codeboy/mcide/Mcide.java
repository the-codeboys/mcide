package com.github.codeboy.mcide;

import com.github.codeboy.mcide.commands.CreateProject;
import com.github.codeboy.mcide.commands.IdeCommand;
import com.github.codeboy.mcide.commands.RunCommand;
import com.github.codeboy.mcide.config.Config;
import com.github.codeboy.mcide.config.ConfigReader;
import com.github.codeboy.mcide.config.Message;
import com.github.codeboy.piston4j.api.ExecutionResult;
import com.github.codeboy.piston4j.api.Piston;
import com.google.gson.Gson;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public final class Mcide extends JavaPlugin {

    private static Gson gson;
    public static Gson gson(){
        return gson==null?(gson=new Gson()):gson;
    }

    private final Piston piston = Piston.getDefaultApi();

    public static Piston getPiston() {
        return getPlugin(Mcide.class).piston;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        readConfigs();
        getCommand("run").setExecutor(new RunCommand());
        getCommand("ide").setExecutor(new IdeCommand());
        getCommand("create-project").setExecutor(new CreateProject());

    }

    private void readConfigs(){
        File configFile=new File(getDataFolder().getPath()+File.separator+"config.yml");
        ConfigReader.readConfig(Config.class,configFile);

        File languageFile=new File(getDataFolder().getPath()+File.separator+"languages"+File.separator+Config.language+".yml");
        ConfigReader.readConfig(Message.class,languageFile);
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

            ExecutionResult result = Piston.getDefaultApi().execute(language, code);
            ExecutionResult.ExecutionOutput output = result.getOutput();
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
