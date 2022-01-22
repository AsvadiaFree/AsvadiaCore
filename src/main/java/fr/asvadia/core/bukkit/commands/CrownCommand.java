package fr.asvadia.core.bukkit.commands;

import fr.asvadia.api.bukkit.util.Creator;
import fr.asvadia.api.common.util.PlaceHolder;
import fr.asvadia.core.bukkit.module.crown.CrownModule;
import fr.asvadia.core.bukkit.module.PlayerModule;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;

public class CrownCommand implements CommandExecutor {


    CrownModule crownModule;

    public CrownCommand(CrownModule crownModule) {
        this.crownModule = crownModule;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 0) {
            if(args[0].equalsIgnoreCase("help")) {
                if(sender.hasPermission("crown.help")) {
                    sender.sendMessage((String) crownModule.getConf().get("chat.help"));
                    return false;
                }
            }
            if (args.length > 1) {
                Player target = null;
                try {
                    target = Bukkit.getPlayer(args[1]);
                } catch (NullPointerException ignored) {
                }

                if (args[0].equalsIgnoreCase("show")) {
                    if (sender.hasPermission("crown.show.other")) {
                        show(sender, target);
                        return false;
                    }
                }
                if (args.length > 2) {
                    Integer amount = null;
                    try {
                        amount = Integer.parseInt(args[2]);
                    } catch (NumberFormatException ignored) {
                    }

                    if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("set")) {
                        if (sender.hasPermission("crown.edit")) {
                            edit(sender, target, amount, Edit.valueOf(args[0].toUpperCase()));
                            return false;
                        }
                    } else if (sender instanceof Player &&
                            (args[0].equalsIgnoreCase("transfer")
                                    || args[0].equalsIgnoreCase("pay")
                                    || args[0].equalsIgnoreCase("send"))) {
                        if (sender.hasPermission("crown.transfer")) {
                            transfer((Player) sender, target, amount);
                            return false;
                        }
                    }
                }
            }
        }
        if (sender instanceof Player) {
            if (sender.hasPermission("crown.show")) show(sender, null);
            else sender.sendMessage((String) crownModule.getMain().getConf().get("chat.permission"));
        } else sender.sendMessage((String) crownModule.getMain().getConf().get("chat.noPlayer"));
        return false;
    }

    public void show(CommandSender sender, Player target) {
        try {
            PlaceHolder pAmount = new PlaceHolder("amount");
            if(target != null) {
                pAmount.setValue(crownModule.getMain().getPlayerModule().getPlayer(target).getCrown(false).toString());
                sender.sendMessage(PlaceHolder.replace(crownModule.getConf().get("chat.show.target"), pAmount));;
            } else {
                if(sender instanceof Player) {
                    pAmount.setValue(crownModule.getMain().getPlayerModule().getPlayer((Player) sender).getCrown(false).toString());
                    sender.sendMessage(PlaceHolder.replace(crownModule.getConf().get("chat.show.sender"), pAmount));
                } else sender.sendMessage((String) crownModule.getMain().getConf().get("chat.noPlayer"));
            }
        } catch (NullPointerException e) {
            sender.sendMessage((String) crownModule.getConf().get("chat.noAccount"));
        }
    }

    public void edit(CommandSender sender, Player target, Integer amount, Edit edit) {
        try {
            PlayerModule.AsvadiaPlayer player = crownModule.getMain().getPlayerModule().getPlayer(target);
            if (amount >= 0) {
                int crown = 0;
                if (edit == Edit.SET) {
                    player.setCrown(amount);
                } else {
                    crown = player.getCrown(false);
                    player.setCrown(player.getCrown(true) + amount * edit.getCoef());
                }
                PlaceHolder pAmount = new PlaceHolder("amount", amount.toString());
                sender.sendMessage(PlaceHolder.replace(crownModule.getConf().get("chat." + edit.getKey() + ".sender"), pAmount, new PlaceHolder("target", target.getName())));
                target.sendMessage(PlaceHolder.replace(crownModule.getConf().get("chat." + edit.getKey() + ".target"), pAmount));
                animate(target, crown, player.getCrown(false));
            } else {
                sender.sendMessage((String) crownModule.getConf().get("chat.invalidAmount"));
            }
        } catch(NullPointerException e) {
            sender.sendMessage((String) crownModule.getConf().get("chat.noAccount"));
        }
    }

    public void transfer(Player sender, Player target, Integer amount) {
        if(sender != target) {
            if (amount > 0) {
                try {
                    PlayerModule.AsvadiaPlayer playerTarget = crownModule.getMain().getPlayerModule().getPlayer(target);
                    PlayerModule.AsvadiaPlayer playerSender = crownModule.getMain().getPlayerModule().getPlayer(sender);
                    int crownSender = playerSender.getCrown(true);
                    if (crownSender >= amount) {
                        int crownTarget = playerTarget.getCrown(true);
                        playerSender.setCrown(crownSender - amount);
                        playerTarget.setCrown(crownTarget + amount);
                        crownModule.logTransfer(playerSender, playerTarget, amount);
                        PlaceHolder pAmount = new PlaceHolder("amount", amount.toString());
                        sender.sendMessage(PlaceHolder.replace(crownModule.getConf().get("chat.transfer.sender"), pAmount, new PlaceHolder("target", target.getName())));
                        target.sendMessage(PlaceHolder.replace(crownModule.getConf().get("chat.transfer.target"), pAmount, new PlaceHolder("sender", target.getName())));
                        animate(sender, crownSender, playerSender.getCrown(false));
                        animate(target, crownTarget, playerTarget.getCrown(false));
                    } else sender.sendMessage((String) crownModule.getConf().get("chat.noCrown"));
                } catch (NullPointerException e) {
                    sender.sendMessage((String) crownModule.getConf().get("chat.noAccount"));
                }
            } else sender.sendMessage((String) crownModule.getConf().get("chat.invalidAmount"));
        } else sender.sendMessage((String) crownModule.getConf().get("chat.noOneself"));
    }

    public void animate(Player target, double start, double end) {
        final DecimalFormat format = new DecimalFormat("##");

        final double coef;
        String title;
        String subtitle;
        if (start > end) {
            coef = -(start - end) / 50;
            title = crownModule.getConf().get("chat.lost.title");
            subtitle = crownModule.getConf().get("chat.lost.subtitle");
        } else {
            coef = (end - start) / 50.0;
            title = crownModule.getConf().get("chat.earn.title");
            subtitle = crownModule.getConf().get("chat.earn.subtitle");
        }

        new BukkitRunnable() {
            int lastEvolution;
            double evolution = start;

            @Override
            public void run() {
                lastEvolution = (int) evolution;
                evolution += coef;
                if ((int) evolution == (int) end || !target.isOnline()) cancel();
                if(lastEvolution != (int) evolution)
                    Creator.title(title, subtitle.replace("%amount%", format.format(evolution)), 0, 5, 0).send(target);
            }

        }.runTaskTimer(crownModule.getMain(), 0, 2);
    }

    public enum Edit {
        ADD(1, "add"),
        SET(0, "set"),
        REMOVE(-1, "remove");

        int coef;
        String key;

        Edit(int coef, String key) {
            this.coef = coef;
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public int getCoef() {
            return coef;
        }
    }

}
