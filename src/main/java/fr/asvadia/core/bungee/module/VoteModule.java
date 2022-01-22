package fr.asvadia.core.bungee.module;

import fr.asvadia.core.bungee.AsvadiaCore;
import fr.asvadia.core.bungee.commands.VoteCommand;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class VoteModule  extends Module {

    public VoteModule(AsvadiaCore main) {
        super(main, "vote");
    }

    @Override
    public void onEnable() {
        register(new VoteCommand(this, "asvadiavote"));
        if(getConf().get("reset.enable")) register(getMain().getProxy().getScheduler().schedule(getMain(), this::taskCheckReset, 5, TimeUnit.MINUTES));
    }

    public void addVote(String name, int voteSiteId, int amount) {
        try {
            ResultSet resultSet = getMain().getSQLServer().getConnection().prepareStatement("SELECT id FROM users WHERE name = '" + name + "'").executeQuery();
            if(resultSet.first()) {
                int id = resultSet.getInt(1);
                Statement preparedStatement = getMain().getSQLServer().getConnection().createStatement();
                preparedStatement.addBatch("UPDATE users SET vote_total = vote_total+" + amount + " WHERE id = " + id + ";");
                preparedStatement.addBatch("UPDATE users SET vote_waiting = vote_waiting+" + amount + " WHERE id = " + id + ";");
                preparedStatement.addBatch("UPDATE users SET vote_last_" + voteSiteId + " = '" + new Timestamp(System.currentTimeMillis()) + "' WHERE id = " + id + ";");
                preparedStatement.executeBatch();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void taskCheckReset() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        try {
            int value = (int) Calendar.class.getField(((String) getConf().get("reset.delay")).toUpperCase()).get(null);

            int now = calendar.get(value);

                calendar.setTimeInMillis(Timestamp.valueOf((String) getConf().get("reset.last")).getTime());
                int last = calendar.get(value);

                if (now != last) {
                    try {
                        getMain().getSQLServer().getConnection().prepareStatement("UPDATE users SET vote_total = 0").executeUpdate();
                    } catch(SQLException e) {
                        e.printStackTrace();
                    }
                } else return;

        } catch (IllegalAccessException | NoSuchFieldException | NullPointerException ignored) { }
        getConf().getResource().set("reset.last", new Timestamp(System.currentTimeMillis()).toString());
        getConf().getResource().set("reset.delay", "MONTH");
        getConf().save();
    }


}
