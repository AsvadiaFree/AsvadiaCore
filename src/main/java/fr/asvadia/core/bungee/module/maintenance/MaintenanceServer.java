package fr.asvadia.core.bungee.module.maintenance;

import fr.asvadia.core.util.PlatForm;
import net.md_5.bungee.api.config.ServerInfo;

public class MaintenanceServer {

    ServerInfo serverInfo;

    boolean enable;
    PlatForm platform;

    public MaintenanceServer(ServerInfo serverInfo, boolean enable, PlatForm platform) {
        this.serverInfo = serverInfo;
        this.enable = enable;
        this.platform = platform;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public void setMaintenance(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnableMaintenance() {
        return enable;
    }

    public PlatForm getPlatform() {
        return platform;
    }

}