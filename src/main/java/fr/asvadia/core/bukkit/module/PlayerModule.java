package fr.asvadia.core.bukkit.module;


import fr.asvadia.core.bukkit.AsvadiaCore;
import fr.asvadia.core.bukkit.listeners.PlayerListener;
import fr.asvadia.core.bukkit.module.vote.VoteSite;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.UUID;

public class PlayerModule extends Module {

    HashMap<Integer, AsvadiaPlayer> playersID;
    HashMap<UUID, AsvadiaPlayer> playersUUID;

    HashMap<Integer, AsvadiaPlayer> playersWebID;

    public PlayerModule(AsvadiaCore main) {
        super(main, "player", false);
    }

    @Override
    public void onEnable() {
        playersID = new HashMap<>();
        playersUUID = new HashMap<>();
        playersWebID = new HashMap<>();
        try {
            getMain().getSQLServer().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS users (" +
                    "id integer NOT NULL AUTO_INCREMENT," +
                    "uuid varchar(50) NOT NULL," +
                    "name varchar(24)," +
                    "vote_total int," +
                    "vote_waiting int," +
                    "PRIMARY KEY(id, uuid));").execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        register(new PlayerListener(this));
        Bukkit.getScheduler().runTaskLater(getMain(), () -> {
            for(Player player : Bukkit.getOnlinePlayers()) {
                load(player.getUniqueId(), player.getName());
            }
        }, 2);
        register(Bukkit.getScheduler().runTaskTimer(getMain(), this::task, 20, 400));
     }

    public void load(UUID uuid, String name) {
        try {

            PreparedStatement get = getMain().getSQLServer().getConnection().prepareStatement("SELECT * FROM users WHERE uuid = ?;");
            get.setString(1, uuid.toString());
            ResultSet result = get.executeQuery();

            if(result.getFetchSize() == 0) {
                PreparedStatement insert = getMain().getSQLServer().getConnection().prepareStatement("INSERT INTO users (uuid, name, vote_total, vote_waiting) VALUES (?, ?, 0, 0)");
                insert.setString(1, uuid.toString());
                insert.setString(2, name);
                insert.executeUpdate();

                result = get.executeQuery();
            }

            result.first();

            int id = result.getInt(1);
            AsvadiaPlayer player = new AsvadiaPlayer(id, uuid, name);
            playersID.put(id, player);
            playersUUID.put(uuid, player);
            if (getMain().getLobbyModule().isEnable()) player.arenaLevel = -1;
            if (getMain().getCrownModule().isEnable()) {
                PreparedStatement select = getMain().getSQLWeb().getConnection().prepareStatement("SELECT id, money FROM users WHERE pseudo = ?");
                select.setString(1, name);
                ResultSet selectResult = select.executeQuery();
                if (selectResult.first() && selectResult.getString(2).equals(name)) {
                    player.webId = selectResult.getInt(1);
                    player.crown = selectResult.getInt(2);
                    playersWebID.put(player.webId, player);
                }
            }
            if (getMain().getVoteModule().isEnable()) {
                player.totalVote = result.getInt(4);
                player.waitingVote = result.getInt(5);
                for (VoteSite voteSite : getMain().getVoteModule().getVoteSites().values())
                    player.lastVote.put(voteSite, result.getTimestamp("vote_last_" + voteSite.getId()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public AsvadiaPlayer getPlayer(UUID uuid) {
        return playersUUID.get(uuid);
    }


    public AsvadiaPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public AsvadiaPlayer getPlayer(OfflinePlayer offlinePlayer) {
        return getPlayer(offlinePlayer.getUniqueId());
    }

    public HashMap<UUID, AsvadiaPlayer> getPlayers() {
        return playersUUID;
    }

    public void task() {
        Runnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    StringBuilder column = new StringBuilder("id");
                    if(getMain().getVoteModule().isEnable()) {
                        column.append(", vote_total, vote_waiting");
                        getMain().getVoteModule().getVoteSites().keySet().forEach(id -> column.append(", vote_last_").append(id));
                   }
                   ResultSet resultSet = getMain().getSQLServer().getConnection().prepareStatement("SELECT " + column + " FROM users").executeQuery();
                   while (resultSet.next()) {
                       if (playersID.containsKey(resultSet.getInt(1))) {
                           AsvadiaPlayer player = playersID.get(resultSet.getInt(1));
                           if (getMain().getVoteModule().isEnable()) {
                               player.totalVote = resultSet.getInt(2);
                               player.waitingVote = resultSet.getInt(3);
                               for (VoteSite voteSite : getMain().getVoteModule().getVoteSites().values())
                                   player.lastVote.put(voteSite, resultSet.getTimestamp("vote_last_" + voteSite.getId()));
                           }
                       }
                   }
                    if (getMain().getCrownModule().isEnable()) {
                        resultSet = getMain().getSQLWeb().getConnection().prepareStatement("SELECT id, money FROM users").executeQuery();
                        while (resultSet.next()) {
                            int webId = resultSet.getInt(1);
                            if (playersWebID.containsKey(webId)) playersWebID.get(webId).crown = (int) resultSet.getDouble(6);
                        }
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }




    public class AsvadiaPlayer {

        int id;

        UUID uuid;
        String name;

        int webId;

        public AsvadiaPlayer(int id, UUID uuid, String name) {
            this.id = id;
            this.uuid = uuid;
            this.name = name;;
        }

        public int getId() {
            return id;
        }

        public UUID getUUID() {
            return uuid;
        }

        public int getWebId() {
            return webId;
        }

        public String getName() {
            return name;
        }


        public Player getPlayer() {
            return Bukkit.getPlayer(uuid);
        }

        //////////////////////////// VOTE MODULE ///////////////////////////////////
        HashMap<VoteSite, Timestamp> lastVote = new HashMap<>();
        int waitingVote = 0;
        int totalVote = 0;


        public Integer getTotalVote(boolean force) {
            if(force) {
                try {
                    PreparedStatement preparedStatement = getMain().getSQLServer().getConnection().prepareStatement("SELECT vote_total FROM users WHERE id = ?");
                    preparedStatement.setInt(1, id);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.first()) totalVote = resultSet.getInt(1);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            return totalVote;
        }

        public int getWaitingVote(boolean force) {
            if(force) {
                try {
                    PreparedStatement preparedStatement = getMain().getSQLServer().getConnection().prepareStatement("SELECT vote_waiting FROM users WHERE id = ?;");
                    preparedStatement.setInt(1, id);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if(resultSet.first()) waitingVote = resultSet.getInt(1);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            return waitingVote;
        }

        public Timestamp getLastVote(VoteSite voteSite) {
            return lastVote.get(voteSite);
        }

        public boolean canVote(VoteSite voteSite) {
            if(getLastVote(voteSite) != null) return System.currentTimeMillis()-getLastVote(voteSite).getTime() >= voteSite.getDelay();
            else return true;
        }

        public void clearWaitingVote() {
            waitingVote = 0;
           try {
               PreparedStatement preparedStatement = getMain().getSQLServer().getConnection().prepareStatement("UPDATE users SET vote_waiting = 0 WHERE id = ?;");
               preparedStatement.setInt(1, id);
               preparedStatement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }





        //////////////////////////// CROWN MODULE ////////////////////////////////////////
        int crown = 0;

        public Integer getCrown(boolean force) {
            if(force) {
                try {
                    PreparedStatement preparedStatement = getMain().getSQLWeb().getConnection().prepareStatement("SELECT money FROM users WHERE id = ?;");
                    preparedStatement.setInt(1, webId);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.first()) crown = (int) resultSet.getDouble(1);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return crown;
        }

        public void setCrown(int crown) {
            this.crown = crown;
            try {
                PreparedStatement update = getMain().getSQLWeb().getConnection().prepareStatement("UPDATE users SET money = ? WHERE id = ?;");
                update.setDouble(1, crown);
                update.setInt(2, webId);
                update.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //////////////////////////// LOBBY MODULE ////////////////////////////////////////
        int arenaLevel = -1;
        AsvadiaPlayer lastDamager;

        public AsvadiaPlayer getLastDamager() {
            return lastDamager;
        }

        public int getArenaLevel() {
            return arenaLevel;
        }

        public void setLastDamager(AsvadiaPlayer player) {
            lastDamager = player;
        }

        public void setArenaLevel(Integer level) {
            this.arenaLevel = level;
        }


    }

}