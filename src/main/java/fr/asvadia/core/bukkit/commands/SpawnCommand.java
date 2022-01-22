package fr.asvadia.core.bukkit.commands;

import fr.asvadia.core.bukkit.module.lobby.LobbyModule;
import fr.asvadia.core.bukkit.module.PlayerModule;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnCommand implements CommandExecutor {

    LobbyModule lobbyModule;

    public SpawnCommand(LobbyModule lobbyModule) {
        this.lobbyModule = lobbyModule;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (sender.hasPermission("lobby.teleport")) spawn(lobbyModule.getMain().getPlayerModule().getPlayer((Player) sender));
            else sender.sendMessage((String) lobbyModule.getMain().getConf().get("chat.permission"));
        } else sender.sendMessage((String) lobbyModule.getMain().getConf().get("chat.notPlayer"));
        return false;
    }


    public void spawn(PlayerModule.AsvadiaPlayer player) {
        if (player.getArenaLevel() < 0) {
            lobbyModule.spawn(player);
            player.getPlayer().sendMessage((String) lobbyModule.getConf().get("chat.spawn.teleported"));
        } else {
            player.getPlayer().sendMessage((String) lobbyModule.getConf().get("chat.spawn.start"));
            new BukkitRunnable() {

                int amount = lobbyModule.getConf().get("spawn.cooldown");
                final Location location = player.getPlayer().getLocation();

                @Override
                public void run() {
                    amount--;
                    Location playerLocation = player.getPlayer().getLocation();
                    if (location.getBlockX() != playerLocation.getBlockX()
                            || location.getBlockY() != playerLocation.getBlockY()
                            || location.getBlockZ() != playerLocation.getBlockZ()) {
                        cancel();
                        player.getPlayer().sendMessage((String) lobbyModule.getConf().get("chat.spawn.cancel"));
                    } else if (amount <= 0) {
                        cancel();
                        lobbyModule.spawn(player);
                        player.getPlayer().sendMessage((String) lobbyModule.getConf().get("chat.spawn.teleported"));
                    }
                }
            }.runTaskTimer(lobbyModule.getMain(), 0, 20);
        }
    }
}
