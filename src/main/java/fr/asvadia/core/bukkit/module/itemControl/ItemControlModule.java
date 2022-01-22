package fr.asvadia.core.bukkit.module.itemControl;

import fr.asvadia.api.bukkit.reflection.Reflector;
import fr.asvadia.core.bukkit.module.Module;
import fr.asvadia.core.bukkit.AsvadiaCore;
import fr.asvadia.core.bukkit.commands.ItemCommand;
import fr.asvadia.core.bukkit.listeners.ItemListener;
import fr.asvadia.core.bukkit.module.itemControl.item.CustomItem;
import fr.asvadia.core.bukkit.module.itemControl.item.ItemManager;
import fr.asvadia.core.bukkit.module.itemControl.durability.DurabilityManager;
import fr.asvadia.core.bukkit.module.itemControl.item.LockItem;
import fr.asvadia.core.bukkit.module.itemControl.recipe.RecipeManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.*;

import java.util.*;

public class ItemControlModule extends Module {


    DurabilityManager durabilityManager;

    ItemManager itemManager;

    RecipeManager recipeManager;

    public ItemControlModule(AsvadiaCore main) {
        super(main, "itemControl");
    }

    @Override
    public void onEnable() {
        itemManager = new ItemManager(this);
        durabilityManager = new DurabilityManager(this);
        recipeManager = new RecipeManager(this);

        register(new ItemListener(this));
        registerCommand("aitem", new ItemCommand(this));
    }

    public DurabilityManager getDurabilityManager() {
        return durabilityManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }



}
