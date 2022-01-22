package fr.asvadia.core.bukkit.commands;

import fr.asvadia.api.bukkit.reflection.ActionBar;
import fr.asvadia.api.bukkit.util.Creator;
import fr.asvadia.api.common.util.PlaceHolder;
import fr.asvadia.core.bukkit.module.PlayerModule;
import fr.asvadia.core.bukkit.module.vote.VoteModule;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteCommand implements CommandExecutor {

    VoteModule voteModule;

    public VoteCommand(VoteModule voteModule) {
        this.voteModule = voteModule;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            if(args.length > 0) {
                if(args[0].equalsIgnoreCase("party")) {
                    if (args.length == 3) {
                        if (args[1].equalsIgnoreCase("set")) {
                            if (sender.hasPermission("vote.party.edit")) {
                                try {
                                    setParty(sender, Integer.parseInt(args[2]));
                                    return false;
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        }
                    } else if (args.length == 2) {
                        if (args[1].equalsIgnoreCase("start")) {
                            if (sender.hasPermission("vote.party.start")) {
                                voteModule.startParty();
                                return false;
                            }
                        } else if(args[1].equalsIgnoreCase("add")) {
                            if(sender.hasPermission("vote.party.edit")) {
                                setParty(sender, voteModule.getPartyAmount()+1);
                                return false;
                            }
                        } else if(args[1].equalsIgnoreCase("help")) {
                            if(sender.hasPermission("vote.party.help")) {
                                sender.sendMessage((String) voteModule.getConf().get("chat.party.help"));
                                return false;
                            }
                        }
                    }
                    if (sender.hasPermission("vote.party.show")) {
                        showParty(sender);
                        return false;
                    }
                }
            }
            if (sender.hasPermission("vote.reward")) reward(voteModule.getMain().getPlayerModule().getPlayer((Player) sender));
            else sender.sendMessage((String) voteModule.getMain().getConf().get("chat.permission"));
        }
        return false;
    }

    public void reward(PlayerModule.AsvadiaPlayer player) {
        if (player.getWaitingVote(false) > 0) {
            int waitingVote = player.getWaitingVote(true);
            for (int i = 0; i < waitingVote; i++)
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), PlaceHolder.replace(voteModule.getConf().get("command.reward"), new PlaceHolder("player", player.getPlayer().getName())));
            PlaceHolder pAmount = new PlaceHolder("amount", Integer.toString(waitingVote));
            player.getPlayer().sendMessage(PlaceHolder.replace(voteModule.getConf().get("chat.reward.sender"), pAmount));

            ActionBar actionBar = Creator.actionBar(PlaceHolder.replace(voteModule.getConf().get("chat.reward.broadcast"), pAmount, new PlaceHolder("player", player.getName())));
            Bukkit.getOnlinePlayers().forEach(actionBar::send);

            player.clearWaitingVote();
        } else player.getPlayer().sendMessage((String) voteModule.getConf().get("chat.noVote"));
    }

    public void setParty(CommandSender sender, int amount) {
        voteModule.setPartyAmount(amount);
        int max = voteModule.getConf().get("party.max");

        sender.sendMessage(PlaceHolder.replace(voteModule.getConf().get("chat.party.set"),
                new PlaceHolder("amount", Integer.toString(amount)),
                new PlaceHolder("max", Integer.toString(max))));

        if(amount >= max && Bukkit.getOnlinePlayers().size() >= (int) voteModule.getConf().get("party.minPlayer")) voteModule.startParty();


    }

    public void showParty(CommandSender sender) {
        sender.sendMessage(PlaceHolder.replace(voteModule.getConf().get("chat.party.show"),
                new PlaceHolder("amount", Integer.toString(voteModule.getPartyAmount())),
                new PlaceHolder("max", Integer.toString(voteModule.getConf().get("party.max")))));
    }




}
