package fr.asvadia.core.bukkit.module.itemControl.recipe;

import com.google.common.collect.Lists;
import fr.asvadia.api.bukkit.reflection.Reflector;
import fr.asvadia.core.bukkit.module.itemControl.ItemControlModule;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeManager {

    ItemControlModule module;

    public RecipeManager(ItemControlModule module) {
        this.module = module;
        if(module.getConf().get("item.enable")) {
            if (module.getConf().get("recipe.enable")) {
                ConfigurationSection section = module.getConf().get("recipe");
                section.getConfigurationSection("add").getKeys(false).forEach(this::loadRecipe);
                loadLock();
            }
        }
    }

    public void loadRecipe(String key) {

        ConfigurationSection sectionRecipe = module.getConf().get("recipe.add." + key);
        Recipe recipe;

        ItemStack result = module.getItemManager().getByMaterial(sectionRecipe.getString("result"));

        int amount = sectionRecipe.getInt("amount");
        if(sectionRecipe.isSet("amount") && amount > 1) result.setAmount(amount);

        String format = sectionRecipe.getString("format");
        if(format.equalsIgnoreCase("craft")) {
            recipe = new ShapedRecipe(result);
            String[] shape = sectionRecipe.getStringList("shape").toArray(new String[0]);
            ((ShapedRecipe) recipe).shape(shape[0], shape[1], shape[2]);

            ConfigurationSection sectionIngredient = sectionRecipe.getConfigurationSection("ingredient");

            sectionIngredient.getKeys(false).forEach(ingredient -> {
                if (Reflector.getVersionNumber() < 9)
                    ((ShapedRecipe) recipe).setIngredient(ingredient.charAt(0), module.getItemManager().getByMaterial(sectionIngredient.getString(ingredient)).getData());
                else {
                    try {
                        ((ShapedRecipe) recipe).getClass().getMethod("setIngredient", char.class, RecipeChoice.class).invoke(recipe, ingredient.charAt(0), new RecipeChoice.ExactChoice(module.getItemManager().getByMaterial(sectionIngredient.getString(ingredient))));
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

            });
        } else if(format.equalsIgnoreCase("furnace")) {
            recipe = new FurnaceRecipe(result, module.getItemManager().getByMaterial(sectionRecipe.getString("ingredient")).getData());
        } else return;
        Bukkit.addRecipe(recipe);

    }

    public void loadLock() {
        ConfigurationSection sectionRemove = module.getConf().getResource().getConfigurationSection("recipe.remove");
        HashMap<String, List<ItemStack>> removeRecipes = new HashMap<>();
        removeRecipes.put("craft", new ArrayList<>());
        removeRecipes.put("furnace", new ArrayList<>());
        for(String key : sectionRemove.getKeys(false)) {
            ConfigurationSection sectionRecipe = sectionRemove.getConfigurationSection(key);

            Material material = Material.getMaterial(key.toUpperCase());
            for (String format : sectionRecipe.getKeys(false)) {
                format = format.toLowerCase();
                if (removeRecipes.containsKey(format)) {
                    for (int amount : sectionRecipe.getIntegerList(format)) {
                        new ItemStack(material, amount);
                        removeRecipes.get(format).add(new ItemStack(material, amount));
                    }
                }
            }

            List<Recipe> recipes = Lists.newArrayList(Bukkit.getServer().recipeIterator());
            Bukkit.clearRecipes();
            recipes.forEach(recipe -> {
                if(!(instanceOfCraftRecipe(recipe) && removeRecipes.get("craft").contains(recipe.getResult())
                        || instanceOfFurnaceRecipe(recipe) && removeRecipes.get("furnace").contains(recipe.getResult())))
                    Bukkit.addRecipe(recipe);
            });
        }
    }

    public boolean instanceOfFurnaceRecipe(Object object) {
        if(Reflector.getVersionNumber() > 14.0) return object instanceof FurnaceRecipe || object instanceof BlastingRecipe;
        return object instanceof FurnaceRecipe;
    }

    public boolean instanceOfCraftRecipe(Object object) {
        return object instanceof ShapedRecipe || object instanceof ShapelessRecipe;
    }
}
