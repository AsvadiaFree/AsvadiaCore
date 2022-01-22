package fr.asvadia.core.bukkit.module.vote;

import fr.asvadia.api.bukkit.reflection.Title;
import fr.asvadia.api.bukkit.util.Creator;
import fr.asvadia.core.bukkit.AsvadiaCore;
import fr.asvadia.core.bukkit.commands.VoteCommand;
import fr.asvadia.core.bukkit.listeners.VoteListener;
import fr.asvadia.core.bukkit.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class VoteModule extends Module {

    HashMap<Integer, VoteSite> voteSites;

    int partyAmount = 0;

    public VoteModule(AsvadiaCore main) {
        super(main, "vote");
    }

    @Override
    public void onEnable() {
        voteSites = new HashMap<>();

        if(!getMain().getPlayerModule().isEnable()) getMain().getPlayerModule().enable();

        ConfigurationSection voteSection = getConf().getResource().getConfigurationSection("site");

        try {
            Statement statement = getMain().getSQLServer().getConnection().createStatement();
            for(String name : voteSection.getKeys(false)) {
                int id = voteSection.getInt(name + ".id");
                voteSites.put(id, new VoteSite(id, name, voteSection.getInt(name + ".delay")));
                statement.addBatch("ALTER TABLE users ADD COLUMN IF NOT EXISTS vote_last_" + id + " timestamp null;");
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        registerCommand("avote", new VoteCommand(this));
        register(new VoteListener(this));
        register(new VotePAPI(this));

    }

    @Override
    public void onDisable() {
        getMain().getData().getResource().set("vote.party", partyAmount);
        getMain().getData().save();
    }

    public VoteSite getVoteSite(int id) {
        return voteSites.get(id);
    }

    public HashMap<Integer, VoteSite> getVoteSites() {
        return voteSites;
    }

    public int getPartyAmount() {
        return partyAmount;
    }

    public void setPartyAmount(int value) {
        this.partyAmount = value;
    }

    public void startParty() {
        int max = getConf().get("party.max");
        partyAmount = Math.max(partyAmount - max, 0);

        Bukkit.broadcastMessage(getConf().get("chat.party.start.broadcast"));

        Title title = Creator.title(getConf().get("chat.party.start.title"), getConf().get("chat.party.start.subtitle"));
        Bukkit.getOnlinePlayers().forEach(title::send);


        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getConf().get("command.party"));

    }
}
