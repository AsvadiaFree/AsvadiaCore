package fr.asvadia.core.bukkit.module.lobby;

import fr.asvadia.core.bukkit.module.PlayerModule;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KitPvp {

    ItemStack helmet;
    ItemStack chestplate;
    ItemStack leggings;
    ItemStack boots;

    HashMap<Integer, ItemStack> items = new HashMap<>();

    public KitPvp(Map<String, ItemStack> items) {
        for (String key : items.keySet()) {
            if (key.equalsIgnoreCase("helmet")) helmet = items.get(key);
            else if (key.equalsIgnoreCase("chestplate")) chestplate = items.get(key);
            else if (key.equalsIgnoreCase("leggings")) leggings = items.get(key);
            else if (key.equalsIgnoreCase("boots")) boots = items.get(key);
            else {
                try {
                    Integer slot = Integer.parseInt(key);
                    this.items.put(slot, items.get(key));
                } catch (NumberFormatException ignored) {

                }
            }
        }
    }

    public ItemStack getHelmet() {
        return helmet;
    }

    public ItemStack getChestplate() {
        return chestplate;
    }

    public ItemStack getLeggings() {
        return leggings;
    }

    public ItemStack getBoots() {
        return boots;
    }

    public HashMap<Integer, ItemStack> getItems() {
        return items;
    }

    public void apply(PlayerModule.AsvadiaPlayer player) {
        player.getPlayer().getInventory().clear();
        player.getPlayer().getInventory().setArmorContents(null);
        player.getPlayer().getInventory().setArmorContents(new ItemStack[]{getBoots(), getLeggings(), getChestplate(), getHelmet()});
        getItems().keySet().forEach(slot -> player.getPlayer().getInventory().setItem(slot, getItems().get(slot)));
    }
}