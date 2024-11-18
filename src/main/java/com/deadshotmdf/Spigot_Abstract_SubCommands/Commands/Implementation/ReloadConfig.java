package com.deadshotmdf.Spigot_Abstract_SubCommands.Commands.Implementation;

import com.deadshotmdf.Spigot_Abstract_SubCommands.Commands.Enums.CommandType;
import com.deadshotmdf.Spigot_Abstract_SubCommands.Commands.SubCommand;
import com.deadshotmdf.Spigot_Abstract_SubCommands.SASC;
import org.bukkit.command.CommandSender;

public class ReloadConfig extends SubCommand {

    private final SASC main;

    public ReloadConfig(SASC main) {
        super("sasc.reload", CommandType.BOTH, 0, "/sasc reload - Reloads the config", "" /*No extra arguments, no need for a syntax message since it will never get called*/);
        this.main = main;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        this.main.saveDefaultConfig();
        this.main.reloadConfig();
        sender.sendMessage("You have reloaded the config.");
    }
}
