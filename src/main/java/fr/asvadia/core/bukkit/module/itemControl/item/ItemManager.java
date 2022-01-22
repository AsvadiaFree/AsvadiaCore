package fr.asvadia.core.bukkit.module.itemControl.item;

import fr.asvadia.api.bukkit.reflection.Reflector;
import fr.asvadia.api.bukkit.util.Creator;
import fr.asvadia.core.bukkit.module.itemControl.ItemControlModule;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.*;

import java.util.HashMap;
import java.util.List;

public class ItemManager {

    ItemControlModule module;

    HashMap<String, CustomItem> customItems;
    HashMap<Material, LockItem> lockItems;

    public ItemManager(ItemControlModule module) {
        this.module = module;
        if(module.getConf().get("item.enable")) {
            try {
                module.getConf().getResource().getConfigurationSection("item.custom").getKeys(false).forEach(this::loadCustom);
                module.getConf().getResource().getConfigurationSection("item.lock").getKeys(false).forEach(this::loadLock);
            } catch (NullPointerException ignored) {}
        }
    }

    public void loadCustom(String key) {
        ConfigurationSection section = module.getConf().get("item.custom." + key);
        key = key.toUpperCase();

        ItemStack itemStack = Creator.item(section);
        if (section.isSet("durability")) {
            int durability = section.getInt("durability");
            module.getDurabilityManager().setDurability(itemStack, durability, durability);
        }

        List<String> forceList = section.getStringList("force");
        this.customItems.put(key, new CustomItem(key, Reflector.addNbt(Creator.item(section), "cIKey", key),
                forceList.contains("name"),
                forceList.contains("enchant"), forceList.contains("attribute"),
                section.getIntegerList("ignoreLineLore")));
    }

    public void loadLock(String material) {
        ConfigurationSection section = module.getConf().getResource().getConfigurationSection("item.lock." + material);
        material = material.toUpperCase();
        this.lockItems.put(Material.getMaterial(material.toUpperCase()), new LockItem(Material.getMaterial(material.toUpperCase()), section.getBoolean("lootable"), section.getBoolean("getable")));
    }


    public ItemStack getByMaterial(String key) {
        key = key.toUpperCase();
        if(customItems.containsKey(key)) return customItems.get(key).getItemStack();
        else return new ItemStack(Material.getMaterial(key));
    }

}
