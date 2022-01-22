package fr.asvadia.core.bungee.listeners;

import fr.asvadia.api.common.util.PlaceHolder;
import fr.asvadia.core.bungee.module.maintenance.MaintenanceModule;
import fr.asvadia.core.bungee.module.maintenance.MaintenanceServer;
import fr.asvadia.core.util.PlatForm;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class MaintenanceListener implements Listener {

    MaintenanceModule maintenanceModule;

    public MaintenanceListener(MaintenanceModule maintenanceModule) {
        this.maintenanceModule = maintenanceModule;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerConnect(ServerConnectEvent event) {
        if (!maintenanceModule.getWhitelist().contains(event.getPlayer().getName())) {
            PlaceHolder placeHolder = new PlaceHolder("server");

            MaintenanceServer maintenanceServer = maintenanceModule.getServer(event.getTarget());

            if (maintenanceModule.isEnableMaintenance()) placeHolder.setValue("global");
            else if (maintenanceServer.isEnableMaintenance()) placeHolder.setValue(event.getTarget().getName());
            else {
                if(maintenanceServer.getPlatform() != PlatForm.ALL && PlatForm.getByUUID(event.getPlayer().getUniqueId()) != maintenanceServer.getPlatform()) {
                    event.getPlayer().disconnect(TextComponent.fromLegacyText(PlaceHolder.replace(maintenanceModule.getConf().get("chat.kick.platform"))));
                    event.setCancelled(true);
                }
                return;
            }

            event.getPlayer().disconnect(TextComponent.fromLegacyText(PlaceHolder.replace(maintenanceModule.getConf().get("chat.kick.maintenance"),
                    new PlaceHolder("server", event.getTarget().getName()))));
            event.setCancelled(true);
        }
    }

}


