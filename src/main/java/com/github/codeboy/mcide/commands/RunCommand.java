package com.github.codeboy.mcide.commands;

import com.github.codeboy.mcide.Mcide;
import com.github.codeboy.piston4j.api.ExecutionResult;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RunCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "requires two arguments: language and code");
            return true;
        }
        List<String> list = new ArrayList<>(Arrays.asList(args));
        list.remove(0);
        String language = args[0], code = String.join(" ", list);
        ExecutionResult result = Mcide.getPiston().execute(language, code);
        ExecutionResult.ExecutionOutput output = result.getOutput();
        sender.sendMessage(output.getOutput());
        System.out.println(result);
        return true;
    }
}
