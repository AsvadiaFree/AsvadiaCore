package fr.asvadia.core.bukkit.module.itemControl.item;

import org.bukkit.Material;

public class LockItem {

    Material material;
    boolean lootable;
    boolean gettable;


    public LockItem(Material material, boolean lootable, boolean gettable) {
        this.material = material;
        this.lootable = lootable;
        this.gettable = gettable;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isLootable() {
        return lootable;
    }

    public boolean isGettable() {
        return gettable;
    }

}