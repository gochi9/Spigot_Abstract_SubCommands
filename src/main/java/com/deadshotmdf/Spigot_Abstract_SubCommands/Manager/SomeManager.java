package com.deadshotmdf.Spigot_Abstract_SubCommands.Manager;

import com.deadshotmdf.Spigot_Abstract_SubCommands.Commands.MainCommand;
import com.deadshotmdf.Spigot_Abstract_SubCommands.Manager.Helpers.NameSearcher;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class SomeManager {

    private final NameSearcher nameSearcher;
    private final HashMap<String, UUID> uuids;

    public SomeManager() {
        this.nameSearcher = new NameSearcher();
        this.uuids = new HashMap<>();

        for(OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            String name = offlinePlayer.getName();
            if (name == null)
                continue;

            name = name.toLowerCase();
            uuids.put(name, offlinePlayer.getUniqueId());
            nameSearcher.addName(name);
        }
    }

    public List<String> getName(String index){
        return index == null || index.isBlank() ? MainCommand.EMPTY : nameSearcher.search(index.toLowerCase());
    }

    public List<String> getNames(){
        return new LinkedList<>(uuids.keySet());
    }

    public UUID getOfflineUUID(String name){
        return name != null ? uuids.get(name.toLowerCase()) : null;
    }

    public void onJoin(Player player){
        String name = player.getName().toLowerCase();
        uuids.put(name, player.getUniqueId());
        nameSearcher.addName(name);
    }

}
