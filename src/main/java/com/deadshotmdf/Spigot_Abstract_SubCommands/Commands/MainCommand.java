package com.deadshotmdf.Spigot_Abstract_SubCommands.Commands;

import com.deadshotmdf.Spigot_Abstract_SubCommands.Commands.Implementation.ReloadConfig;
import com.deadshotmdf.Spigot_Abstract_SubCommands.Commands.Implementation.TeleportToPlayer;
import com.deadshotmdf.Spigot_Abstract_SubCommands.Manager.SomeManager;
import com.deadshotmdf.Spigot_Abstract_SubCommands.SASC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {

    public final static List<String> EMPTY = Collections.emptyList();

    private final HashMap<String, SubCommand> subCommands;

    public MainCommand(SASC main, SomeManager someManager) {
        this.subCommands = new HashMap<>();
        this.subCommands.put("teleport", new TeleportToPlayer(someManager));
        this.subCommands.put("reload", new ReloadConfig(main));
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length < 1 || args[0].equalsIgnoreCase("help")){
            List<String> allowedCommands = getAllowedCommands(sender);

            if(allowedCommands.isEmpty())
                sender.sendMessage("No available commands.");
            else
                allowedCommands.forEach(allowedCommand -> sender.sendMessage(subCommands.get(allowedCommand).getCommandHelpMessage()));

            return true;
        }

        SubCommand subCommand = subCommands.get(args[0]);

        if(subCommand == null){
            sender.sendMessage("Invalid command.");
            return true;
        }

        if(!subCommand.canExecute(sender, args.length, true))
            return true;

        subCommand.execute(sender, args);
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 0)
            return EMPTY;

        if(args.length == 1)
            return getAllowedCommands(sender);

        SubCommand subCommand = subCommands.get(args[0]);
        return subCommand != null && subCommand.isVisible() && subCommand.canExecute(sender, 0, false) ? subCommand.tabCompleter(sender, args) : EMPTY;
    }

    private List<String> getAllowedCommands(CommandSender sender) {
        List<String> allowedCommands = new LinkedList<>();
        subCommands.forEach((k, v) -> {
            if(v.isVisible() && v.canExecute(sender, 0, false))
                allowedCommands.add(k);
        });

        return allowedCommands;
    }

}