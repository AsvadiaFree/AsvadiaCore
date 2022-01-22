package fr.asvadia.core.bukkit.listeners;

import fr.asvadia.api.common.util.PlaceHolder;
import fr.asvadia.core.bukkit.module.lobby.LobbyModule;
import fr.asvadia.core.bukkit.module.PlayerModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ArenaListener implements Listener {

    LobbyModule lobbyModule;

    public ArenaListener(LobbyModule lobbyModule) {
        this.lobbyModule = lobbyModule;
    }

    public void onQuit(PlayerQuitEvent event) {
        PlayerModule.AsvadiaPlayer player = lobbyModule.getMain().getPlayerModule().getPlayer(event.getPlayer());
        if(player.getArenaLevel() < 0 && player.getLastDamager() != null) event.getPlayer().setHealth(0);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            PlayerModule.AsvadiaPlayer player = lobbyModule.getMain().getPlayerModule().getPlayer((Player) event.getEntity());
            if(player.getArenaLevel() >= 0)
                event.setCancelled(false);
                player.setLastDamager(lobbyModule.getMain().getPlayerModule().getPlayer((Player) event.getDamager()));
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
        PlayerModule.AsvadiaPlayer player = lobbyModule.getMain().getPlayerModule().getPlayer(event.getEntity());
        PlayerModule.AsvadiaPlayer attacker = player.getLastDamager();
        PlaceHolder pVictim = new PlaceHolder("victim", event.getEntity().getName());
        if(attacker != null && attacker.getPlayer().isOnline()) {
            lobbyModule.levelUpArena(attacker);
            event.setDeathMessage(PlaceHolder.replace(lobbyModule.getConf().get("chat.arena.death.broadcastKiller"), pVictim,
                    new PlaceHolder("killer", attacker.getPlayer().getName())));
        } else event.setDeathMessage(PlaceHolder.replace(lobbyModule.getConf().get("chat.arena.death.broadcast"), pVictim));
        player.setArenaLevel(-1);
        event.getEntity().spigot().respawn();
        lobbyModule.spawn(player);
    }

}
