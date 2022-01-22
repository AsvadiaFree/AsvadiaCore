package fr.asvadia.core.bukkit.commands;

import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import fr.asvadia.api.common.util.PlaceHolder;
import fr.asvadia.core.bukkit.module.itemControl.ItemControlModule;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ItemCommand implements CommandExecutor, TabCompleter {

    ItemControlModule itemModule;

    public ItemCommand(ItemControlModule itemModule) {
        this.itemModule = itemModule;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("item.give")) {
            if (args.length > 1) {
                if(Bukkit.getPlayer(args[0]) != null) {
                    int amount = 1;
                    if(args.length > 2) {
                        try {
                            amount = Integer.parseInt(args[2]);
                            if(amount < 1) throw new NumberFormatException();
                        } catch (NumberFormatException e) {
                            sender.sendMessage((String) itemModule.getConf().get("chat.invalidAmount"));
                        }
                    }
                    give(sender, Bukkit.getPlayer(args[0]), args[1], amount);
                    return false;
                } else sender.sendMessage((String) itemModule.getConf().get("chat.unknown.target"));
            }
            sender.sendMessage((String) itemModule.getConf().get("chat.help"));
        } else sender.sendMessage((String) itemModule.getMain().getConf().get("chat.permission"));
        return false;
    }

    public void give(CommandSender sender, Player target, String key, int amount) {
        key = key.toUpperCase();
        if (itemModule.getCustomItems().containsKey(key)) {
            ItemStack itemStack = itemModule.getCustomItem(key).getItemStack();
            for (int i = 0; i < amount ; i++) target.getInventory().addItem(itemStack);
            PlaceHolder pTarget = new PlaceHolder("player", target.getName());
            PlaceHolder pItem = new PlaceHolder("item", key);
            PlaceHolder pAmount = new PlaceHolder("amount", Integer.toString(amount));
            sender.sendMessage(PlaceHolder.replace(itemModule.getConf().get("chat.give.sender"), pTarget, pAmount, pItem));
            target.sendMessage(PlaceHolder.replace(itemModule.getConf().get("chat.give.target"), pAmount, pItem));
        } else sender.sendMessage((String) itemModule.getConf().get("chat.unknown.item"));
    }








    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) return a(args, itemModule.getCustomItems().keySet());
        else if (args.length == 3) return a(args, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        return null;
    }

    public static List<String> a(String[] var0, Collection<?> var1) {
        String var2 = var0[var0.length - 1];
        ArrayList var3 = Lists.newArrayList();
        if (!var1.isEmpty()) {
            Iterator var4 = Iterables.transform(var1, Functions.toStringFunction()).iterator();

            while(var4.hasNext()) {
                String var5 = (String)var4.next();
                if (a(var2, var5)) {
                    var3.add(var5);
                }
            }

            if (var3.isEmpty()) {
                var4 = var1.iterator();

                while(var4.hasNext()) {
                    Object var6 = var4.next();
                    if (a(var2, (String) var6)) {
                        var3.add(String.valueOf(var6));
                    }
                }
            }
        }

        return var3;
    }

    public static boolean a(String var0, String var1) {
        return var1.regionMatches(true, 0, var0, 0, var0.length());
    }
}
