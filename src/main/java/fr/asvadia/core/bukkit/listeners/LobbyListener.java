package fr.asvadia.core.bukkit.listeners;

import fr.asvadia.core.bukkit.module.lobby.LobbyModule;
import fr.asvadia.core.util.PlatForm;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class LobbyListener implements Listener {

    LobbyModule lobbyModule;

    public LobbyListener(LobbyModule lobbyModule) {
        this.lobbyModule = lobbyModule;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        lobbyModule.spawn(lobbyModule.getMain().getPlayerModule().getPlayer(event.getPlayer()));
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(event.getEntity() instanceof Player) {
            event.setCancelled(true);
            if (event.getFoodLevel() < 20) {
                ((Player) event.getEntity()).setFoodLevel(20);
            }
        }
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        if(event.toWeatherState()) event.setCancelled(true);
    }

    @EventHandler
    public void onThunderChange(ThunderChangeEvent event) {
        if(event.toThunderState()) event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(event.getItem() != null) {
            if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                    && event.getItem().isSimilar(lobbyModule.getItemJoin())) {
                PlatForm platForm = PlatForm.getByUUID(event.getPlayer().getUniqueId());
                event.getPlayer().chat("/" + lobbyModule.getConf().get("itemjoin.executeCommand." + platForm.toString().toLowerCase()));
            }
        }
    }

    @EventHandler
    public void onMobSpawning(EntitySpawnEvent event) {
        if(event.getEntity() instanceof Creature || event.getEntity() instanceof Animals) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }


}
