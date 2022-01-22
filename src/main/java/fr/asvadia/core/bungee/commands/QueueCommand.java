package fr.asvadia.core.bungee.commands;

import fr.asvadia.api.common.util.PlaceHolder;
import fr.asvadia.core.bungee.module.queue.Queue;
import fr.asvadia.core.bungee.module.queue.QueueModule;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class QueueCommand extends Command {

    QueueModule queueModule;

    public QueueCommand(QueueModule queueModule) {
        super("queue");
        this.queueModule = queueModule;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            if(args.length > 0) {
                if(args[0].equalsIgnoreCase("help")) {
                    if (sender.hasPermission("queue.help")) {
                        sender.sendMessage(TextComponent.fromLegacyText(queueModule.getConf().get("chat.help")));
                        return;
                    }
                }
                if (args.length > 1) {
                    if (args[0].equalsIgnoreCase("on") || args[0].equals("off")) {
                        if (sender.hasPermission("queue.manage")) {
                            Queue queue = queueModule.getQueue(queueModule.getMain().getProxy().getServerInfo(args[1]));
                            if (queue != null) setQueue(sender, args[0].equalsIgnoreCase("on"), queue);
                            else sender.sendMessage(TextComponent.fromLegacyText(queueModule.getConf().get("chat.unknownQueue")));
                            return;
                        }
                    }

                }
            }
            if(sender.hasPermission("queue.help")) {
                sender.sendMessage(TextComponent.fromLegacyText(queueModule.getConf().get("chat.help")));
                return;
            }
            sender.sendMessage(TextComponent.fromLegacyText(queueModule.getMain().getConf().get("chat.permission")));
        } else sender.sendMessage(TextComponent.fromLegacyText(queueModule.getMain().getConf().get("chat.noPlayer")));
    }

    public void setQueue(CommandSender sender, boolean enable, Queue queue) {
        String key;
        if(enable) key = "enable";
        else key = "disable";

        if(queue.isEnable() != enable) {

            queue.setEnable(enable);

            PlaceHolder pServer = new PlaceHolder("server", queue.getName());

            BaseComponent[] message = TextComponent.fromLegacyText(PlaceHolder.replace(queueModule.getConf().get("chat." + key + ".broadcast"), pServer));
            queue.getLists().keySet().forEach(level ->
                    queue.getLists().get(level).forEach(player -> player.sendMessage(message)));


            sender.sendMessage(TextComponent.fromLegacyText(PlaceHolder.replace(queueModule.getConf().get("chat." + key + ".message"), pServer)));
        } else sender.sendMessage(TextComponent.fromLegacyText(queueModule.getConf().get("chat." + key + ".already")));
    }


}



