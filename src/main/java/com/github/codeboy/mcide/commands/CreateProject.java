package com.github.codeboy.mcide.commands;

import com.github.codeboy.mcide.config.Message;
import com.github.codeboy.mcide.ide.CodeProject;
import com.github.codeboy.mcide.ide.OwnedCodeProject;
import com.github.codeboy.mcide.ide.gui.LanguageSelector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateProject implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.CMD_PLAYER_ONLY);
            return true;
        }
        Player p = (Player) sender;
        if (args.length == 0) {
            p.sendMessage(Message.NAME_REQUIRED);
            return true;
        }
        String name = String.join(" ", args);
        new LanguageSelector(runtime -> {
            CodeProject project = new OwnedCodeProject(runtime.getLanguage(), name, p.getUniqueId());
            project.save();
            p.sendMessage(Message.PROJECT_CREATE_SUCCESS);
            project.open(p);
        }).open(p);
        return true;
    }
}
