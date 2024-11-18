package com.deadshotmdf.Spigot_Abstract_SubCommands.Listeners;

import com.deadshotmdf.Spigot_Abstract_SubCommands.Manager.SomeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    private final SomeManager someManager;

    public PlayerJoin(SomeManager someManager) {
        this.someManager = someManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent ev) {
        someManager.onJoin(ev.getPlayer());
    }

}
