package fr.asvadia.core.bungee.module;

import fr.asvadia.api.bungee.config.YMLConfig;
import fr.asvadia.core.bungee.AsvadiaCore;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;

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
        if(enableConfig) config = new YMLConfig(main.getDataFolder() + "/module", getName(), "module/" + getName(), main);
        onEnable();
    }

    public void disable() {
        enable = false;
        for (Object object : objects) unRegister(object);
        objects.clear();
        onDisable();
    }

    public void onEnable() {

    }

    public void onDisable() {

    }

    public <T> T register(T object) {
        if (object instanceof Listener) main.getProxy().getPluginManager().registerListener(main, (Listener) object);
        else if (object instanceof Command) main.getProxy().getPluginManager().registerCommand(main, (Command) object);
        objects.add(object);
        return object;
    }


    public void unRegister(Object object) {
        if (object instanceof Listener) main.getProxy().getPluginManager().unregisterListener((Listener) object);
        else if (object instanceof Command) main.getProxy().getPluginManager().unregisterCommand((Command) object);
        else if (object instanceof ScheduledTask) ((ScheduledTask) object).cancel();
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
