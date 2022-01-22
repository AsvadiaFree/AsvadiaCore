package fr.asvadia.core.bungee.commands;

import fr.asvadia.core.bungee.module.queue.QueueModule;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;


public class QueueRequestCommand extends Command {


    QueueModule queueModule;

    ServerInfo serverInfo;

    public QueueRequestCommand(QueueModule queueModule, String name, String[] aliases, ServerInfo serverInfo) {
        super(name, null, aliases);
        this.queueModule = queueModule;
        this.serverInfo = serverInfo;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
       if(sender instanceof ProxiedPlayer) {
           if(sender.hasPermission("queue.request")) {
               queueModule.request((ProxiedPlayer) sender, serverInfo);
           } else sender.sendMessage(TextComponent.fromLegacyText(queueModule.getMain().getConf().get("chat.permission")));
       } else sender.sendMessage(TextComponent.fromLegacyText(queueModule.getMain().getConf().get("chat.noPlayer")));
    }







}
