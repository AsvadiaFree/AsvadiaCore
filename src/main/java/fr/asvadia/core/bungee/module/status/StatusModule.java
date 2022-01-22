package fr.asvadia.core.bungee.module.status;

import fr.asvadia.core.bungee.AsvadiaCore;
import fr.asvadia.core.bungee.module.Module;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class StatusModule extends Module {

    HashMap<ServerInfo, StatusServer> servers;

    public StatusModule(AsvadiaCore main) {
        super(main, "status", false);
    }

    @Override
    public void onEnable() {
        servers = new HashMap<>();
        getMain().getProxy().getServers().values().forEach(serverInfo -> servers.put(serverInfo, new StatusServer(serverInfo, Status.UNKNOWN)));
        register(ProxyServer.getInstance().getScheduler().schedule(getMain(), this::chekStatus, 1, 10, TimeUnit.SECONDS));
    }

    public StatusServer getServer(ServerInfo serverInfo) {
        return servers.get(serverInfo);
    }

    public HashMap<ServerInfo, StatusServer> getServers() {
        return servers;
    }

    public void chekStatus() {
        servers.values().forEach(server -> {
            if (getMain().getMaintenanceModule().isEnable() && getMain().getMaintenanceModule().getServer(server.getServerInfo()).isEnableMaintenance())
                server.setStatus(Status.MAINTENANCE);
            else {
                try {
                    Socket s = new Socket();
                    s.connect(server.getServerInfo().getSocketAddress(), 15);
                    s.close();
                    server.setStatus(Status.OPEN);
                } catch (IOException e) {
                    server.setStatus(Status.CLOSE);
                }
            }
        });
    }











}
