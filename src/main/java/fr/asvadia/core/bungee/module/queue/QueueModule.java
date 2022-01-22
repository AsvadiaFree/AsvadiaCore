package fr.asvadia.core.bungee.module.queue;

import fr.asvadia.api.bungee.util.Creator;
import fr.asvadia.api.common.util.PlaceHolder;
import fr.asvadia.core.bungee.AsvadiaCore;
import fr.asvadia.core.bungee.module.Module;
import fr.asvadia.core.bungee.module.status.Status;
import fr.asvadia.core.bungee.commands.QueueCommand;
import fr.asvadia.core.bungee.commands.QueueRequestCommand;
import fr.asvadia.core.bungee.listeners.QueueListener;
import fr.asvadia.core.util.PlatForm;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class QueueModule extends Module {

    HashMap<ServerInfo, Queue> queues;

    public QueueModule(AsvadiaCore main) {
        super(main, "queue");
    }

    @Override
    public void onEnable() {
        queues = new HashMap<>();
        Configuration sectionServer = getConf().getResource().getSection("server");
        Configuration sectionData = getMain().getData().get("queue.server");

        if(!getMain().getStatusModule().isEnable()) getMain().getStatusModule().enable();

        for(String server : sectionServer.getKeys()) {
            ServerInfo serverInfo = getMain().getProxy().getServerInfo(server);
            if(serverInfo != null) {
                String name = sectionServer.getString(server + ".name");

                List<String> commands = new ArrayList<>(sectionServer.getStringList(server + ".commands"));
                if(!commands.isEmpty())
                    register(new QueueRequestCommand(this, commands.get(0), commands.subList(0, commands.size()-1).toArray(new String[0]), serverInfo));

                PlatForm platForm = PlatForm.ALL;
                if(sectionData != null && sectionServer.contains(server + ".platform")) platForm = PlatForm.valueOf(sectionServer.getString(server + ".platform").toUpperCase());

                boolean enable = true;
                if(sectionData != null && sectionData.contains(server + ".lock")) enable = sectionData.getBoolean(server + ".enable");

                queues.put(serverInfo, new Queue(serverInfo, name, platForm, enable));

            }
        }
        register(new QueueCommand(this));
        register(new QueueListener(this));
        register(ProxyServer.getInstance().getScheduler().schedule(getMain(), this::task, 5, 2, TimeUnit.SECONDS));
    }

    @Override
    public void onDisable() {
        queues.values().forEach(this::save);
        getMain().getData().save();
    }

    public Queue getQueue(ServerInfo server) {
        return queues.get(server);
    }

    public HashMap<ServerInfo, Queue> getQueues() {
        return queues;
    }

    public void save(Queue queue) {
        getMain().getData().getResource().set("queue.server." + queue.getServerInfo().getName() + ".enable", queue.isEnable());
    }

    public Queue getPlayerQueue(ProxiedPlayer player) {
        for(Queue queue : queues.values())
            for(List<ProxiedPlayer> list : queue.getLists().values()) if (list.contains(player)) return queue;
        return null;
    }

    public Level getPlayerLevel(ProxiedPlayer player) {
        if(player.hasPermission("queue.bypass")) return Level.BYPASS;
        else if(player.hasPermission("queue.premium")) return Level.PREMIUM;
        else return Level.NORMAL;
    }

    public void task() {
        for (Queue queue : getQueues().values()) {
            PlaceHolder pServer = new PlaceHolder("server", queue.getName());
            for(Level level : queue.getLists().keySet()) {
                ArrayList<ProxiedPlayer> list = queue.getLists().get(level);
                if (list.size() > 0) {
                    PlaceHolder pPosition = new PlaceHolder("position");
                    PlaceHolder pSize = new PlaceHolder("size", Integer.toString(list.size()));
                    if (queue.isEnable() && getMain().getStatusModule().getServer(queue.getServerInfo()).getStatus() == Status.OPEN) {
                        ProxiedPlayer player = list.get(0);

                        list.remove(player);
                        player.connect(queue.getServerInfo());

                        player.sendMessage(TextComponent.fromLegacyText(PlaceHolder.replace(getConf().get("chat.connect.message"), pServer)));
                        Creator.title(PlaceHolder.replace(getConf().get("chat.connect.title"), pServer),
                                PlaceHolder.replace(getConf().get("chat.connect.subtitle"), pServer)).send(player);
                    }
                    for (ProxiedPlayer queuePlayer : list) {
                        pPosition.setValue(Integer.toString(list.indexOf(queuePlayer) + 1));

                        String conf;
                        if(queue.isEnable()) conf = "enable";
                        else conf = "disable";

                        queuePlayer.sendMessage(ChatMessageType.ACTION_BAR,  TextComponent.fromLegacyText(PlaceHolder.replace(getConf().get("chat." + conf + ".actionbar"), pServer, pPosition, pSize)));

                    }
                }
            }
        }
    }

    public void request(ProxiedPlayer player, ServerInfo serverInfo) {
        Queue queue = getQueue(serverInfo);
        if (queue != null) {
            if (serverInfo != player.getServer().getInfo()) {
                if(queue.getPlatform() == PlatForm.ALL || PlatForm.getByUUID(player.getUniqueId()) == queue.getPlatform()) {
                    Level level;
                    PlaceHolder pLevel = new PlaceHolder("level");
                    PlaceHolder pServer = new PlaceHolder("server");
                    Queue playerQueue = getPlayerQueue(player);
                    if (playerQueue != null) {
                        level = playerQueue.getPlayerLevel(player);
                        pServer.setValue(playerQueue.getName());
                        pLevel.setValue(level.toString());
                        playerQueue.getLists().get(level).remove(player);
                        player.sendMessage(TextComponent.fromLegacyText(PlaceHolder.replace(getConf().get("chat.leave.message"), pServer, pLevel)));
                        if (queue == playerQueue) {
                            Creator.title(PlaceHolder.replace(getConf().get("chat.leave.title"), pServer, pLevel), PlaceHolder.replace(getConf().get("chat.leave.subtitle"), pServer, pLevel)).send(player);
                            return;
                        }
                    }
                    level = getPlayerLevel(player);
                    pLevel.setValue(level.toString());
                    pServer.setValue(queue.getName());
                    queue.getLists().get(level).add(player);
                    player.sendMessage(TextComponent.fromLegacyText(PlaceHolder.replace(getConf().get("chat.join.message"), pServer, pLevel)));
                    Creator.title(PlaceHolder.replace(getConf().get("chat.join.title"), pServer, pLevel), PlaceHolder.replace(getConf().get("chat.join.subtitle"), pServer, pLevel)).send(player);
                } else player.sendMessage(TextComponent.fromLegacyText((String) getConf().get("chat.platform")));
            } else player.sendMessage(TextComponent.fromLegacyText((String) getConf().get("chat.already")));
        }
    }


}
