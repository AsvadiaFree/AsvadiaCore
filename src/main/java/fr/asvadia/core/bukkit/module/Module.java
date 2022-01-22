package fr.asvadia.core.bukkit.module;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import fr.asvadia.api.bukkit.config.YMLConfig;
import fr.asvadia.api.bukkit.reflection.Reflector;
import fr.asvadia.core.bukkit.AsvadiaCore;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class Module {

    AsvadiaCore main;

    List<Object> objects = new ArrayList<>();;

    boolean enableConfig;
    YMLConfig config;

    boolean enable;
    String name;

    public Module(AsvadiaCore main, String name) {
        this(main, name, true);
    }

    public Module(AsvadiaCore main, String name, boolean enableConfig) {
        this.main = main;
        this.name = name;
        this.enableConfig = enableConfig;
        this.enable = false;
    }


    public void enable() {
        enable = true;
        if (enableConfig) config = new YMLConfig(main.getDataFolder() + "/module", getName(), "module/" + getName(), main);
        onEnable();
    }

    public void disable() {
        enable = false;
        objects.forEach(this::unRegister);
        objects.clear();
        onDisable();
    }

    public void onEnable() {

    }

    public void onDisable() {

    }

    //LISTENER, PROTOCOL LIB PACKET, PLACEHOLDER_EXPANSION
    public <T> T register(T object) {
        if (object instanceof Listener) Bukkit.getPluginManager().registerEvents((Listener) object, main);
        if (object instanceof PacketAdapter)
            ProtocolLibrary.getProtocolManager().addPacketListener((PacketAdapter) object);
        if (object instanceof PlaceholderExpansion) ((PlaceholderExpansion) object).register();
        objects.add(object);
        return object;
    }


    public void registerCommand(String name, Object executor) {
        PluginCommand pluginCommand = main.getCommand(name);
        if (pluginCommand == null) pluginCommand = Reflector.registerPluginCommand(main, name);
        if (executor instanceof CommandExecutor) pluginCommand.setExecutor((CommandExecutor) executor);
        if (executor instanceof TabCompleter) pluginCommand.setTabCompleter((TabCompleter) executor);
        objects.add(pluginCommand);
    }


    public void unRegister(Object object) {
        if (object instanceof Listener) HandlerList.unregisterAll((Listener) object);
        else if (object instanceof Integer) Bukkit.getScheduler().cancelTask((Integer) object);
        else if(object instanceof PluginCommand) {
            Reflector.unRegisterPluginCommand((PluginCommand) object);
        }
        else if(object instanceof PlaceholderExpansion) ((PlaceholderExpansion)object).unregister();
    }

    public boolean isEnable() {
        return enable;
    }

    public AsvadiaCore getMain() {
        return main;
    }

    public String getName() {
        return name;
    }

    public YMLConfig getConf() {
        return config;
    }

}
