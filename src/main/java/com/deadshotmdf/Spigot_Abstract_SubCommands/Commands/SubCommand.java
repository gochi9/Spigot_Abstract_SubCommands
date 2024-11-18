package com.deadshotmdf.Spigot_Abstract_SubCommands.Commands;

import com.deadshotmdf.Spigot_Abstract_SubCommands.Commands.Enums.CommandType;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class SubCommand {

    private final String permission;
    private final CommandType commandType;
    private final int argsRequired;
    private final String commandHelpMessage;
    private final String commandWrongSyntax;
    private final boolean visible;

    public SubCommand(String permission, CommandType commandType, int argsRequired, String commandHelpMessage, String commandWrongSyntax){
        this(permission, commandType, argsRequired, commandHelpMessage, commandWrongSyntax, true);
    }

    public SubCommand(String permission, CommandType commandType, int argsRequired, String commandHelpMessage, String commandWrongSyntax, boolean visible) {
        this.permission = permission;
        this.commandType = commandType;
        this.argsRequired = ++argsRequired;
        this.commandHelpMessage = commandHelpMessage;
        this.commandWrongSyntax = commandWrongSyntax;
        this.visible = visible;
    }

    protected boolean canExecute(CommandSender sender, int argsLength, boolean sendMessage){
        boolean isPlayer = sender instanceof Player;
        if(commandType == CommandType.PLAYER && !isPlayer){
            if(sendMessage)
                sender.sendMessage("Only a player may execute this command.");
            return false;
        }

        if(isPlayer && !sender.hasPermission(permission)){
            if(sendMessage)
                sender.sendMessage("You do not have the permission to execute this command.");
            return false;
        }

        if(commandType == CommandType.CONSOLE && !(sender instanceof ConsoleCommandSender)){
            if(sendMessage)
                sender.sendMessage("Command can only be executed by the console.");
            return false;
        }

        if(sendMessage && argsRequired > 1 && argsLength < argsRequired){
            sender.sendMessage(commandWrongSyntax);
            return false;
        }

        return true;
    }

    public abstract void execute(CommandSender sender, String[] args);
    public List<String> tabCompleter(CommandSender sender, String[] args){
        return MainCommand.EMPTY;
    }

    public String getCommandHelpMessage(){
        return commandHelpMessage;
    }

    public boolean isVisible(){
        return visible;
    }

}
