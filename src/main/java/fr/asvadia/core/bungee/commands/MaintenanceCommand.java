package fr.asvadia.core.bungee.commands;


import fr.asvadia.api.common.util.PlaceHolder;
import fr.asvadia.core.bungee.module.maintenance.MaintenanceModule;
import fr.asvadia.core.bungee.module.maintenance.MaintenanceServer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class MaintenanceCommand extends Command {

    MaintenanceModule maintenanceModule;

    public MaintenanceCommand(MaintenanceModule maintenanceModule, String name) {
        super(name);
        this.maintenanceModule = maintenanceModule;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("on") || args[0].equals("off")) {
                if (sender.hasPermission("maintenance.manage")) {
                    boolean enable = args[0].equalsIgnoreCase("on");
                    if (args.length == 1) setMaintenance(sender, enable, null);
                    else {
                        MaintenanceServer server = maintenanceModule.getServer(maintenanceModule.getMain().getProxy().getServerInfo(args[1]));
                        if (server != null) setMaintenance(sender, enable, server);
                        else sender.sendMessage(TextComponent.fromLegacyText(maintenanceModule.getConf().get("chat.unknownServer")));
                    }
                    return;
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                if (sender.hasPermission("maintenance.whitelist.list")) {
                    listWhitelist(sender);
                    return;
                }
            } else if(args[0].equalsIgnoreCase("help")) {
                if(sender.hasPermission("maintenance.help")) {
                    sender.sendMessage(TextComponent.fromLegacyText(maintenanceModule.getConf().get("chat.help")));
                    return;
                }
            }
            if (args.length > 1) {
                if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
                    if (sender.hasPermission("maintenance.whitelist.manage")) {
                        whitelist(sender, args[1], args[0].equalsIgnoreCase("add"));
                        return;
                    }
                }
            }

        }
        if(sender.hasPermission("maintenance.help")) {
            sender.sendMessage(TextComponent.fromLegacyText(maintenanceModule.getConf().get("chat.help")));
            return;
        }
        sender.sendMessage(TextComponent.fromLegacyText(maintenanceModule.getMain().getConf().get("chat.permission")));

    }


    public void setMaintenance(CommandSender sender, boolean enable, MaintenanceServer server) {
        PlaceHolder pServer = new PlaceHolder("server");
        if(server != null) {
            server.setMaintenance(enable);
            pServer.setValue(server.getServerInfo().getName());
        } else {
            maintenanceModule.setMaintenance(enable);
            pServer.setValue("global");
        }

        if(enable) {
            BaseComponent[] kickMessage = TextComponent.fromLegacyText(PlaceHolder.replace(maintenanceModule.getConf().get("chat.kick.maintenance"), pServer));
            maintenanceModule.getMain().getProxy().getPlayers().forEach(player -> {
                if (!maintenanceModule.getWhitelist().contains(player.getName()) && (server == null || player.getServer().getInfo() == server.getServerInfo()))
                    player.disconnect(kickMessage);
            });
        }

        String key;
        if(enable) key = "enable";
        else key = "disable";

        sender.sendMessage(TextComponent.fromLegacyText(PlaceHolder.replace(maintenanceModule.getConf().get("chat." + key), pServer)));
    }

    public void whitelist(CommandSender sender, String name, boolean add) {
        String action;
        if(!maintenanceModule.getWhitelist().contains(name)) {
            if(add) {
                maintenanceModule.getWhitelist().add(name);
                action = "add";
            } else action = "unknown";
        } else {
            if(!add) {
                maintenanceModule.getWhitelist().remove(name);
                action = "remove";
            } else action = "already";
        }
        sender.sendMessage(TextComponent.fromLegacyText(PlaceHolder.replace(maintenanceModule.getConf().get("chat.whitelist." + action),
                new PlaceHolder("player", name))));
    }

    public void listWhitelist(CommandSender sender) {
        sender.sendMessage(TextComponent.fromLegacyText(PlaceHolder.replace(maintenanceModule.getConf().get("chat.whitelist.list"),
                new PlaceHolder("list", maintenanceModule.getWhitelist().toString()))));
    }
}
