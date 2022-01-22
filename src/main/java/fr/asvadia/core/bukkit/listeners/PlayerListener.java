package fr.asvadia.core.bukkit.listeners;

import fr.asvadia.core.bukkit.module.PlayerModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerListener implements Listener {

    PlayerModule playerModule;

    public PlayerListener(PlayerModule playerModule) {
        this.playerModule = playerModule;
    }

    @EventHandler
    private void onConnect(PlayerJoinEvent event) {
        playerModule.load(event.getPlayer().getUniqueId(), event.getPlayer().getName());
    }

    private void onQuit(PlayerQuitEvent event) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                playerModule.getPlayers().remove(event.getPlayer().getUniqueId());
            }
        }, 1);
    }
}
