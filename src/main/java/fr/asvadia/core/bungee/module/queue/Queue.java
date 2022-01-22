package fr.asvadia.core.bungee.module.queue;

import fr.asvadia.api.common.util.PlatForm;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;

import java.util.ArrayList;
import java.util.HashMap;

public class Queue {

    ServerInfo serverInfo;
    PlatForm platform;

    String name;
    HashMap<Level, ArrayList<ProxiedPlayer>> lists;
    boolean enable;

    public Queue(ServerInfo serverInfo, String name, PlatForm platForm, boolean enable) {
        this.lists = new HashMap<>();
        this.serverInfo = serverInfo;
        this.name = name;
        this.platform = platForm;
        this.enable = enable;

        for(Level level : Level.values()) lists.put(level, new ArrayList<>());

    }

    public boolean isEnable() {
        return enable;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public PlatForm getPlatform() {
        return platform;
    }

    public String getName() {
        return name;
    }

    public HashMap<Level, ArrayList<ProxiedPlayer>> getLists() {
        return lists;
    }

    public Level getPlayerLevel(ProxiedPlayer player) {
        for(Level level : lists.keySet()) if(lists.get(level).contains(player)) return level;
        return null;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

}