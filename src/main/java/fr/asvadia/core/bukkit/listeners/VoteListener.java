package fr.asvadia.core.bukkit.listeners;

import fr.asvadia.api.common.util.PlaceHolder;
import fr.asvadia.core.bukkit.module.PlayerModule;
import fr.asvadia.core.bukkit.module.vote.VoteModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class VoteListener implements Listener {

    VoteModule voteModule;

    public VoteListener(VoteModule voteModule) {
        this.voteModule = voteModule;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerModule.AsvadiaPlayer player = voteModule.getMain().getPlayerModule().getPlayer(event.getPlayer());
        if(player.getWaitingVote(false) > 0)
            event.getPlayer().sendMessage(PlaceHolder.replace(voteModule.getConf().get("chat.join"),
                    new PlaceHolder("amount", Integer.toString(player.getWaitingVote(false)))));
    }

}
