package com.github.codeboy.mcide.commands;

import com.github.codeboy.mcide.config.Message;
import com.github.codeboy.mcide.ide.CodePlayer;
import com.github.codeboy.mcide.ide.CodeProject;
import com.github.codeboy.mcide.ide.gui.ProjectSelector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IdeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            sender.sendMessage(Message.CMD_PLAYER_ONLY);
            return true;
        }

        if (args.length == 0) {
            ProjectSelector menu = new ProjectSelector(player);
            menu.open(player);
        } else
            switch (args[0]) {
                case "create": {
                    if (args.length < 3) {
                        player.sendMessage(Message.LANGUAGE_AND_NAME_REQUIRED);
                        return true;
                    }
                    List<String> list= new ArrayList<>(Arrays.asList(args));
                    list.remove(0);
                    list.remove(0);
                    String language=args[1],title=String.join(" ",list);
                    CodeProject project=new CodeProject(language,title,player.getUniqueId());
                    project.save();
                    player.sendMessage(Message.PROJECT_CREATE_SUCCESS);
                    break;
                }
            }

        return true;
    }
}
