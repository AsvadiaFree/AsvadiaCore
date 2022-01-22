package fr.asvadia.core.bungee.commands;

import fr.asvadia.core.bungee.AsvadiaCore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class CoreCommand extends Command {

    AsvadiaCore main;

    public CoreCommand(AsvadiaCore main, String name) {
        super(name);
        this.main = main;
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender.hasPermission("core.manage")) {
            if(args.length > 1) {

            }
        } else sender.sendMessage(TextComponent.fromLegacyText((String) main.getConf().get("chat.permission")));
    }
}
