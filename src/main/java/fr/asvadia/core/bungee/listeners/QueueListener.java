package fr.asvadia.core.bungee.listeners;

import fr.asvadia.core.bungee.module.queue.QueueModule;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class QueueListener implements Listener {

    QueueModule queueModule;

    public QueueListener(QueueModule queueModule) {
        this.queueModule = queueModule;
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        if(event.getReason() == ServerConnectEvent.Reason.PLUGIN_MESSAGE
            && queueModule.getQueue(event.getTarget()) != null) {
            event.setCancelled(true);
            queueModule.request(event.getPlayer(), event.getTarget());
        }
    }

}
