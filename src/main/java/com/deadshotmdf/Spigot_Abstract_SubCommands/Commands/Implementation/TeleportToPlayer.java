package com.deadshotmdf.Spigot_Abstract_SubCommands.Commands.Implementation;

import com.deadshotmdf.Spigot_Abstract_SubCommands.Commands.Enums.CommandType;
import com.deadshotmdf.Spigot_Abstract_SubCommands.Commands.MainCommand;
import com.deadshotmdf.Spigot_Abstract_SubCommands.Commands.SubCommand;
import com.deadshotmdf.Spigot_Abstract_SubCommands.Manager.SomeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TeleportToPlayer extends SubCommand {

    private final SomeManager someManager;

    public TeleportToPlayer(SomeManager someManager) {
        super("sasc.teleport", CommandType.PLAYER, 1, "/sasc teleport {player} - Teleports to a player", "Invalid syntax, use: /sasc teleport {player}");
        this.someManager = someManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        //We do not need to check for the argument size because we specifically tell the SubCommand that we need at least one extra argument after the sub command
        //If the arg length condition is not met then this method won't get called
        Player player = Bukkit.getPlayer(args[1]);

        if(player == null){
            sender.sendMessage("Player " + args[1] + " is offline.");
            return;
        }

        ((Player)sender).teleport(player);
    }

    @Override
    public List<String> tabCompleter(CommandSender sender, String[] args) {
        if(!canExecute(sender, 0, false) || args.length != 2)
            return MainCommand.EMPTY;

        if(!args[1].isEmpty())
            return someManager.getName(args[1]);

        else
            return someManager.getNames();
    }
}
