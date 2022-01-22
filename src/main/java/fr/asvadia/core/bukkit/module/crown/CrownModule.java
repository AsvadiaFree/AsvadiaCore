package fr.asvadia.core.bukkit.module.crown;


import fr.asvadia.core.bukkit.AsvadiaCore;
import fr.asvadia.core.bukkit.commands.CrownCommand;
import fr.asvadia.core.bukkit.module.Module;
import fr.asvadia.core.bukkit.module.PlayerModule;
import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class CrownModule extends Module {

    public CrownModule(AsvadiaCore main) {
        super(main, "crown");
    }

    @Override
    public void onEnable() {
        if(!getMain().getPlayerModule().isEnable()) getMain().getPlayerModule().enable();

        registerCommand("crown", new CrownCommand(this));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) register(new CrownPAPI(this));

    }

    public void logTransfer(PlayerModule.AsvadiaPlayer sender, PlayerModule.AsvadiaPlayer target, Integer amount) {
        try {
            PreparedStatement history = getMain().getSQLWeb().getConnection().prepareStatement("INSERT INTO users__points_history VALUES (NULL, ?, ?, ?, ?, NULL)");
            history.setInt(1, sender.getWebId());
            history.setInt(2, target.getWebId());
            history.setDouble(3, amount);
            history.setTimestamp(4, new Timestamp(new Date().getTime()));
            history.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





}
