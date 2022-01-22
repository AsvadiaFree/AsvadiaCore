package fr.asvadia.core.bungee.module.maintenance;

import fr.asvadia.core.bungee.AsvadiaCore;
import fr.asvadia.core.bungee.commands.MaintenanceCommand;
import fr.asvadia.core.bungee.listeners.MaintenanceListener;
import fr.asvadia.core.bungee.module.Module;
import fr.asvadia.core.util.PlatForm;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MaintenanceModule extends Module {

    HashMap<ServerInfo, MaintenanceServer> servers;

    boolean enable;
    List<String> whitelist;

    public MaintenanceModule(AsvadiaCore main) {
        super(main, "maintenance");
    }

    @Override
    public void onEnable() {
        servers = new HashMap<>();

        register(new MaintenanceCommand(this, "maintenance"));
        register(new MaintenanceListener(this));


        enable = false;
        if(getMain().getData().getResource().contains("maintenance.global")) enable = getMain().getData().get("maintenance.global");
        else getMain().getData().getResource().set("maintenance.global", false);

        List<String> whitelist = getMain().getData().get("maintenance.whitelist");
        if(whitelist != null) this.whitelist = whitelist;
        else this.whitelist = new ArrayList<>();


        Configuration sectionFilter = getConf().get("platform");
        Configuration sectionServer = getMain().getData().get("maintenance.server");

        boolean hasFloodgate = getMain().getProxy().getPluginManager().getPlugin("floodgate") != null;

        getMain().getProxy().getServers().values().forEach(server -> {
            PlatForm filter = PlatForm.ALL;
            boolean enable = false;

            if(hasFloodgate && sectionFilter != null && sectionFilter.contains(server.getName())) filter = PlatForm.valueOf(sectionFilter.getString(server.getName()).toUpperCase());
            if(sectionServer != null && sectionServer.contains(server.getName())) enable = sectionServer.getBoolean(server.getName());

            servers.put(server, new MaintenanceServer(server, enable, filter));
        });
    }

    @Override
    public void onDisable() {
        getMain().getData().getResource().set("maintenance.whitelist", whitelist);
        getMain().getData().getResource().set("maintenance.global", enable);
        getServerList().values().forEach(this::save);
        getMain().getData().save();
    }

    public MaintenanceServer getServer(ServerInfo serverInfo) {
        return servers.get(serverInfo);
    }

    public HashMap<ServerInfo, MaintenanceServer> getServerList() {
        return servers;
    }

    public void setMaintenance(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnableMaintenance() {
        return enable;
    }

    public List<String> getWhitelist() {
        return whitelist;
    }

    public void save(MaintenanceServer maintenanceServer) {
        getMain().getData().getResource().set("maintenance.server." + maintenanceServer.getServerInfo().getName(), enable);
    }

}
