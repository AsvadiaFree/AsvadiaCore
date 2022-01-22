package fr.asvadia.core.bukkit.module.itemControl.durability;

import fr.asvadia.core.bukkit.module.itemControl.ItemControlModule;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DurabilityManager {

    ItemControlModule itemModule;

    public DurabilityManager(ItemControlModule itemModule) {
        this.itemModule = itemModule;
    }

    public Map.Entry<Integer, Integer> getDurability(ItemStack item) {
        int durabilityLine = getDurabilityLine(item);
        if(durabilityLine > -1) {
            String[] durabilities = item.getItemMeta().getLore().get(durabilityLine).replaceFirst(itemModule.getMain().getConf().get("durability.key"), "").split(itemModule.getMain().getConf().get("durability.separator"));
            return new AbstractMap.SimpleEntry<>(Integer.parseInt(durabilities[0]), Integer.parseInt(durabilities[1]));
        }
        return new AbstractMap.SimpleEntry<>(null, null);
    }

    public int getDurabilityLine(ItemStack item) {
        if(item.hasItemMeta() && item.getItemMeta().hasLore()) {
            for(String line : item.getItemMeta().getLore()) {
                if(line.startsWith(itemModule.getMain().getConf().get("durability.key"))) return item.getItemMeta().getLore().indexOf(line);
            }
        }
        return -1;
    }

    public void deleteDurability(ItemStack item) {
        int durabilityLine = getDurabilityLine(item);
        if(durabilityLine > -1) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = item.getItemMeta().getLore();
            lore.remove(durabilityLine);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    public void setDurability(ItemStack item, Integer durability, Integer maxDurability) {
        if (durability > 0) {
            String line = itemModule.getMain().getConf().get("durability.key") + Integer.toString(durability) + itemModule.getMain().getConf().get("durability.separator") + maxDurability;

            ItemMeta meta = item.getItemMeta();

            int durability_item = item.getType().getMaxDurability() * durability / maxDurability;
            ((Damageable) meta).setDamage(item.getType().getMaxDurability() - durability_item);

            List<String> lore;
            if(meta.hasLore()) lore = meta.getLore();
            else lore = new ArrayList<>();

            if(!lore.isEmpty()) {
                int durabilityLine = getDurabilityLine(item);
                if (durabilityLine > -1) {
                    if(durabilityLine == lore.size()-1) {
                        lore.set(durabilityLine, line);
                        return;
                    } else lore.remove(durabilityLine);
                }
            }
            lore.add(line);
            meta.setLore(lore);
            item.setItemMeta(meta);
        } else {
            item.setType(Material.AIR);
        }
    }
}
