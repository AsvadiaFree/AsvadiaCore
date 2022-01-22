package fr.asvadia.core.bukkit;

import fr.asvadia.api.bukkit.config.YMLConfig;
import fr.asvadia.api.common.sql.SQLConnection;
import fr.asvadia.core.bukkit.module.Module;
import fr.asvadia.core.bukkit.module.crown.CrownModule;
import fr.asvadia.core.bukkit.module.function.FunctionModule;
import fr.asvadia.core.bukkit.module.itemControl.ItemControlModule;
import fr.asvadia.core.bukkit.module.lobby.LobbyModule;
import fr.asvadia.core.bukkit.module.PlayerModule;
import fr.asvadia.core.bukkit.module.vote.VoteModule;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class AsvadiaCore extends JavaPlugin {

    YMLConfig config;

    YMLConfig data;

    SQLConnection sqlConnectionServer;

    SQLConnection sqlConnectionWeb;

    PlayerModule playerModule;

    CrownModule crownModule;

    LobbyModule lobbyModule;

    ItemControlModule itemModule;

    VoteModule voteModule;

    FunctionModule functionModule;


    @Override
    public void onEnable() {
        this.config = new YMLConfig(getDataFolder().toString(), "config", this);
        this.data = new YMLConfig(getDataFolder().toString(), "data", this);

        sqlConnectionServer = new SQLConnection(getConf().get("sql.server.host"),
                getConf().get("sql.server.port"),
                getConf().get("sql.server.user"),
                getConf().get("sql.server.password"),
                getConf().get("sql.server.dataBase"));
        sqlConnectionWeb = new SQLConnection(getConf().get("sql.web.host"),
                getConf().get("sql.web.port"),
                getConf().get("sql.web.user"),
                getConf().get("sql.web.password"),
                getConf().get("sql.web.dataBase"));

        playerModule = new PlayerModule(this);
        crownModule = new CrownModule(this);
        lobbyModule = new LobbyModule(this);
        itemModule = new ItemControlModule(this);
        voteModule = new VoteModule(this);
        functionModule = new FunctionModule(this);
        getModules().forEach(s -> { if (((List<?>) getConf().get("module", false)).contains(s.getName())) s.enable(); });
    }

    @Override
    public void onDisable() {
        getModules().forEach(module -> {
            if(module.isEnable()) module.disable();
        });
        this.data.save();
    }


    public YMLConfig getConf() {
        return this.config;
    }

    public YMLConfig getData() {
        return this.data;
    }

    public SQLConnection getSQLServer() {
        return sqlConnectionServer;
    }

    public SQLConnection getSQLWeb() {
        return sqlConnectionWeb;
    }

    public PlayerModule getPlayerModule() {
        return playerModule;
    }


    public CrownModule getCrownModule() {
        return crownModule;
    }

    public LobbyModule getLobbyModule() {
        return lobbyModule;
    }

    public VoteModule getVoteModule() {
        return voteModule;
    }

    public List<Module> getModules() {
        return Arrays.asList(playerModule, crownModule, lobbyModule, itemModule, voteModule, functionModule);
    }

}
