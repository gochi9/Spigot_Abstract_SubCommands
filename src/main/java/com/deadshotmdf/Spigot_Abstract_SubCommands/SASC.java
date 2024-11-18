package com.deadshotmdf.Spigot_Abstract_SubCommands;

import com.deadshotmdf.Spigot_Abstract_SubCommands.Commands.MainCommand;
import com.deadshotmdf.Spigot_Abstract_SubCommands.Listeners.PlayerJoin;
import com.deadshotmdf.Spigot_Abstract_SubCommands.Manager.SomeManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SASC extends JavaPlugin {

    private SomeManager someManager;

    @Override
    public void onEnable() {
        this.someManager = new SomeManager();

        Bukkit.getPluginManager().registerEvents(new PlayerJoin(someManager), this);

        this.getCommand("sasc").setExecutor(new MainCommand(this, someManager));
    }

}
