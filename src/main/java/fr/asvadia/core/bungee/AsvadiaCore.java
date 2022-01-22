package fr.asvadia.core.bungee;

import fr.asvadia.api.bungee.config.YMLConfig;
import fr.asvadia.api.common.sql.SQLConnection;
import fr.asvadia.core.bungee.commands.CoreCommand;
import fr.asvadia.core.bungee.module.*;
import fr.asvadia.core.bungee.module.Module;
import fr.asvadia.core.bungee.module.maintenance.MaintenanceModule;
import fr.asvadia.core.bungee.module.queue.QueueModule;
import fr.asvadia.core.bungee.module.status.StatusModule;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class AsvadiaCore extends Plugin {

    YMLConfig config;

    YMLConfig data;

    SQLConnection sqlManagerServer;

    SQLConnection sqlManagerWeb;

    StatusModule statusModule;

    QueueModule queueModule;

    MaintenanceModule maintenanceModule;

    VoteModule voteModule;

    @Override
    public void onEnable() {

        config = new YMLConfig(getDataFolder().toString(), "config", this);
        data = new YMLConfig(getDataFolder().toString(), "data", this);

        sqlManagerServer = new SQLConnection(getConf().get("sql.server.host"),
                getConf().get("sql.server.port"),
                getConf().get("sql.server.user"),
                getConf().get("sql.server.password"),
                getConf().get("sql.server.dataBase"));
        sqlManagerWeb = new SQLConnection(getConf().get("sql.web.host"),
                getConf().get("sql.web.port"),
                getConf().get("sql.web.user"),
                getConf().get("sql.web.password"),
                getConf().get("sql.web.dataBase"));

        statusModule = new StatusModule(this);
        queueModule = new QueueModule(this);
        maintenanceModule = new MaintenanceModule(this);
        voteModule = new VoteModule(this);

        getModules().forEach(s -> {
            if (((List<?>) getConf().get("module", false)).contains(s.getName())) s.enable();
        });
        getProxy().getPluginManager().registerCommand(this, new CoreCommand(this, "bcore"));

    }

    @Override
    public void onDisable() {
        getModules().forEach(module -> {
            if (module.isEnable()) module.disable();
        });
        data.save();
    }


    public YMLConfig getConf() {
        return config;
    }

    public YMLConfig getData() {
        return data;
    }

    public SQLConnection getSQLServer() {
        return sqlManagerServer;
    }

    public SQLConnection getSQLWeb() {
        return sqlManagerWeb;
    }

    public StatusModule getStatusModule() {
        return statusModule;
    }

    public QueueModule getQueueModule() {
        return queueModule;
    }

    public MaintenanceModule getMaintenanceModule() {
        return maintenanceModule;
    }

    public VoteModule getVoteModule() {
        return voteModule;
    }

    public List<Module> getModules() {
        return Arrays.asList(statusModule, queueModule, maintenanceModule, voteModule);
    }

}
