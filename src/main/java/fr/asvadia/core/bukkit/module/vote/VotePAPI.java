package fr.asvadia.core.bukkit.module.vote;

import fr.asvadia.core.bukkit.module.PlayerModule;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class VotePAPI extends PlaceholderExpansion {

    VoteModule voteModule;

    public VotePAPI(VoteModule voteModule) {
        this.voteModule = voteModule;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "vote";
    }

    @Override
    public @NotNull String getAuthor() {
        return "AziRixX";
    }

    @Override
    public @NotNull String getVersion() {
        return "0.5";
    }


    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        PlayerModule.AsvadiaPlayer asvadiaPlayer = voteModule.getMain().getPlayerModule().getPlayer(player.getUniqueId());
        switch (params) {
            case "party_amount": return Integer.toString(voteModule.getPartyAmount());
            case "party_max": return voteModule.getConf().get("party.max");
            case "total": return asvadiaPlayer.getTotalVote(false).toString();
            case "total_exact": return asvadiaPlayer.getTotalVote(true).toString();
            case "waiting": return Integer.toString(asvadiaPlayer.getWaitingVote(false));
            case "waiting_exact": return Integer.toString(asvadiaPlayer.getWaitingVote(true));
        }
        if(params.startsWith("can_")) {
            try {
                VoteSite voteSite = voteModule.getVoteSite(Integer.parseInt(params.split("_")[1]));
                if(voteSite != null) return Boolean.toString(asvadiaPlayer.canVote(voteSite));
                else return "false";
            } catch(NumberFormatException ignored) { }
        }
        return "";
    }
}
