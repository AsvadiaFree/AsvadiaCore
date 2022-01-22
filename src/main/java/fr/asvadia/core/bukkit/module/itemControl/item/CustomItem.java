package fr.asvadia.core.bukkit.module.itemControl.item;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CustomItem {

    String key;
    ItemStack itemStack;

    boolean forceName;
    boolean forceEnchant;
    boolean forceAttribute;
    List<Integer> ignoreLineLore;

    public CustomItem(String key, ItemStack itemStack, boolean forceName, boolean forceEnchant, boolean forceAttribute, List<Integer> ignoreLineLore) {
        this.key = key;
        this.forceName = forceName;
        this.forceEnchant = forceEnchant;
        this.forceAttribute = forceAttribute;
        this.itemStack = itemStack;
        this.ignoreLineLore = new ArrayList<>(ignoreLineLore);
    }

    public String getKey() {
        return key;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isForceName() {
        return forceName;
    }

    public boolean isForceAttribute() {
        return forceAttribute;
    }

    public boolean isForceEnchant() {
        return forceEnchant;
    }

    public List<Integer> getIgnoreLineLore() {
        return ignoreLineLore;
    }

}