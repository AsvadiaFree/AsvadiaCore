package fr.asvadia.core.bungee.module.status;

import net.md_5.bungee.api.config.ServerInfo;

public class StatusServer {

    ServerInfo serverInfo;
    Status status;

    public StatusServer(ServerInfo serverInfo, Status status) {
        this.serverInfo = serverInfo;
        this.status = status;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
