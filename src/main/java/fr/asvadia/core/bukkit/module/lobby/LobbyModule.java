package fr.asvadia.core.bukkit.module.lobby;

import fr.asvadia.api.bukkit.util.Creator;
import fr.asvadia.api.common.util.PlaceHolder;
import fr.asvadia.core.bukkit.module.Module;
import fr.asvadia.core.bukkit.module.PlayerModule;
import fr.asvadia.core.bukkit.AsvadiaCore;
import fr.asvadia.core.bukkit.commands.SpawnCommand;
import fr.asvadia.core.bukkit.listeners.ArenaListener;
import fr.asvadia.core.bukkit.listeners.LobbyListener;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class LobbyModule extends Module {

    ItemStack itemJoin;
    HashMap<Integer, KitPvp> kits;
    Map.Entry<Location, Location> arena;
    Location spawn;

    public LobbyModule(AsvadiaCore main) {
        super(main, "lobby");
    }


    @Override
    public void onEnable() {
        kits = new HashMap<>();
        
        if(!getMain().getPlayerModule().isEnable()) getMain().getPlayerModule().enable();

        if(getConf().get("itemjoin.enable")) itemJoin = Creator.item(getConf().getResource().getConfigurationSection("itemjoin.item"));

        registerCommand("spawn", new SpawnCommand(this));
        register(new LobbyListener(this));

        spawn = new Location(Bukkit.getWorld((String) getConf().get("spawn.world")), getConf().get("spawn.x"), getConf().get("spawn.y"), getConf().get("spawn.z"));

        if(getConf().get("arena.enable")) {
            register(new ArenaListener(this));
            World world = Bukkit.getWorld((String) getConf().get("arena.area.world"));
            arena = new AbstractMap.SimpleEntry<>(
                    new Location(world, getConf().get("arena.area.A.x"), getConf().get("arena.area.A.y"), getConf().get("arena.area.A.z")),
                    new Location(world, getConf().get("arena.area.B.x"), getConf().get("arena.area.B.y"), getConf().get("arena.area.B.z")));

            ConfigurationSection section = getConf().getResource().getConfigurationSection("arena.level");
            for (String levelString : section.getKeys(false)) {
                try {
                    Integer level = Integer.parseInt(levelString);
                    HashMap<String, ItemStack> items = new HashMap<>();
                    ConfigurationSection levelSection = section.getConfigurationSection(levelString);
                    for (String item : levelSection.getKeys(false)) {
                        items.put(item, Creator.item(levelSection.getConfigurationSection(item)));
                    }
                    kits.put(level, new KitPvp(items));
                } catch (NumberFormatException ignored) { }
            }
            register(Bukkit.getScheduler().runTaskTimer(getMain(), this::taskArena, 5, 10));
        }
    }

    public KitPvp getKit(Integer level) {
        return kits.get(level);
    }

    public HashMap<Integer, KitPvp> getKits() {
        return kits;
    }

    public Map.Entry<Location, Location> getArena() {
        return arena;
    }

    public Location getSpawn() {
        return spawn;
    }

    public ItemStack getItemJoin() {
        return itemJoin;
    }

    public void spawn(PlayerModule.AsvadiaPlayer player) {
        player.setArenaLevel(-1);
        player.getPlayer().teleport(getSpawn());
        player.getPlayer().getInventory().clear();
        player.getPlayer().getInventory().setArmorContents(null);
        player.getPlayer().setGameMode(GameMode.ADVENTURE);
        if (getConf().get("itemjoin.enable")) player.getPlayer().getInventory().setItem(getConf().get("itemjoin.slot"), getItemJoin());
    }

    public void levelUpArena(PlayerModule.AsvadiaPlayer player) {
        player.setArenaLevel(player.getArenaLevel() + 1);

        double health = player.getPlayer().getHealth();
        if(health > 16) health = 20;
        else health += 4;
        player.getPlayer().setHealth(health);

        KitPvp kitPvp = getKit(player.getArenaLevel());
        if (kitPvp != null) {
            kitPvp.apply(player);

            PlaceHolder pLevel = new PlaceHolder("level", Integer.toString(player.getArenaLevel()));
            player.getPlayer().sendMessage(PlaceHolder.replace(getConf().get("chat.arena.levelUp.message"), pLevel));
            Creator.title(PlaceHolder.replace(getConf().get("chat.arena.levelUp.title"), pLevel),
                    PlaceHolder.replace(getConf().get("chat.arena.levelUp.subtitle"), pLevel)).send(player.getPlayer());
        }
    }

    public boolean isInArena(PlayerModule.AsvadiaPlayer player) {
        if (getArena().getKey().getWorld() == player.getPlayer().getWorld()) {
            List<Integer> position = new ArrayList<>(Arrays.asList(
                    player.getPlayer().getLocation().getBlockX(),
                    player.getPlayer().getLocation().getBlockY(),
                    player.getPlayer().getLocation().getBlockZ()
            ));

            List<Map.Entry<Integer, Integer>> area = new ArrayList<>(Arrays.asList(
                    new AbstractMap.SimpleEntry<>(getArena().getKey().getBlockX(), getArena().getValue().getBlockX()),
                    new AbstractMap.SimpleEntry<>(getArena().getKey().getBlockY(), getArena().getValue().getBlockY()),
                    new AbstractMap.SimpleEntry<>(getArena().getKey().getBlockZ(), getArena().getValue().getBlockZ())
            ));

            for (int i = 0; i < area.size(); i++) {
                if (area.get(i).getKey() < area.get(i).getValue()) {
                    area.set(i, new AbstractMap.SimpleEntry<>(area.get(i).getValue(), area.get(i).getKey()));
                }
                int pos = position.get(i);
                if (!(pos < area.get(i).getKey() && pos > area.get(i).getValue())) return false;
            }
            return true;
        }
        return false;
    }

    public void taskArena() {
        for (PlayerModule.AsvadiaPlayer player : getMain().getPlayerModule().getPlayers().values()) {
            if (isInArena(player)) {
                if (player.getArenaLevel() < 0) {
                    player.setArenaLevel(0);
                    getKit(0).apply(player);
                    player.getPlayer().sendMessage((String) getConf().get("chat.arena.enter.message"));
                    Creator.title(getConf().get("chat.arena.enter.title"), getConf().get("chat.arena.enter.subtitle")).send(player.getPlayer());
                }
            } else {
                if (player.getArenaLevel() >= 0) {
                    spawn(player);
                    player.getPlayer().sendMessage((String) getConf().get("chat.arena.leave.message"));
                }
            }
        }
    }
}
