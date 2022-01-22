package fr.asvadia.core.bungee.commands;

import fr.asvadia.core.bungee.module.VoteModule;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.command.ConsoleCommandSender;

public class VoteCommand extends Command {


    VoteModule voteModule;

    public VoteCommand(VoteModule voteModule, String name) {
        super(name);
        this.voteModule = voteModule;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ConsoleCommandSender) {
            if (args.length > 2) {
                String name = args[0];
                try {
                    voteModule.addVote(name, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                } catch (NumberFormatException ignored) {

                }
            }
        }
    }



}
