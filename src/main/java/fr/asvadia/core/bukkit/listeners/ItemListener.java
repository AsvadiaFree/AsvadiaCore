package fr.asvadia.core.bukkit.listeners;

import fr.asvadia.api.bukkit.reflection.Reflector;
import fr.asvadia.core.bukkit.module.itemControl.item.CustomItem;
import fr.asvadia.core.bukkit.module.itemControl.ItemControlModule;
import fr.asvadia.core.bukkit.module.itemControl.item.LockItem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemListener implements Listener {

    ItemControlModule itemModule;

    public ItemListener(ItemControlModule itemModule) {
        this.itemModule = itemModule;
    }



    @EventHandler
    private void PlayerPickupItem(PlayerPickupItemEvent event) {
        updateCustomItem(event.getItem().getItemStack());
    }


    @EventHandler
    private void onBreakBlock(BlockBreakEvent event) {
        boolean action = false;
        for(ItemStack item : event.getBlock().getDrops()) {
            LockItem lockItem = itemModule.getLockItem(item.getType());
            if(lockItem != null && !lockItem.isLootable()) {
                event.getBlock().getDrops().remove(item);
                action = true;
            }
        }
        if(action) {
            event.getBlock().setType(Material.AIR);
            event.getBlock().getDrops().forEach(drop -> event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drop));
        }
    }

    @EventHandler
    private void onCreatureSpawn(CreatureSpawnEvent event) {
        for (ItemStack armor : event.getEntity().getEquipment().getArmorContents()) {
            if (armor != null) {
                LockItem lockItem = itemModule.getLockItem(armor.getType());
                if (lockItem != null && !lockItem.isLootable()) armor.setType(Material.AIR);
            }
        }
    }

    @EventHandler
    private void onCreatureDeath(EntityDeathEvent event) {
        new ArrayList<>(event.getDrops()).forEach(item -> {
            LockItem lockItem = itemModule.getLockItem(item.getType());
            if(lockItem != null && !lockItem.isLootable()) event.getDrops().remove(item);
        });
    }
    
    public void updateCustomItem(ItemStack item) {
        String key = Reflector.getNbt(item, "cIKey");
        if (key != null) {
           CustomItem customItem = itemModule.getCustomItem(key);
            if (customItem != null) {
                ItemStack modelItem = customItem.getItemStack().clone();

                if (item.getType() != modelItem.getType()) item.setType(modelItem.getType());


                Integer maxDurability = itemModule.getDurabilityManager().getDurability(modelItem).getValue();
                Integer durabilityItem = itemModule.getDurabilityManager().getDurability(item).getKey();
                if (maxDurability != null) itemModule.getDurabilityManager().deleteDurability(modelItem);
                if (durabilityItem != null) itemModule.getDurabilityManager().deleteDurability(item);

                if (customItem.isForceAttribute()) {
                    Map<String, Double> attributesModel = Reflector.getAttributes(modelItem);
                    Map<String, Double> attributesItem = Reflector.getAttributes(item);
                    if(attributesModel.equals(attributesItem)) Reflector.setAttributes(item, attributesModel);
                }


                ItemMeta modelItemMeta = modelItem.getItemMeta();
                ItemMeta itemMeta = item.getItemMeta();

                if(customItem.isForceName() && !modelItemMeta.getDisplayName().equals(item.getItemMeta().getDisplayName()))
                    itemMeta.setDisplayName(modelItemMeta.getDisplayName());

                List<String> lore = itemMeta.getLore();
                if (modelItemMeta.hasLore() && !itemMeta.hasLore()) lore = modelItemMeta.getLore();
                else if (!modelItemMeta.hasLore() && itemMeta.hasLore()) lore.clear();
                else if (itemMeta.hasLore() && modelItemMeta.hasLore()) {
                    for (int i = 0; i < lore.size(); i++) {
                        if (!customItem.getIgnoreLineLore().contains(i+1)
                                && !lore.get(i).equals(modelItemMeta.getLore().get(i)))
                            lore.set(i, modelItemMeta.getLore().get(i));
                    }
                }
                itemMeta.setLore(lore);

                if (customItem.isForceEnchant()) {
                    if (!itemMeta.getEnchants().equals(modelItemMeta.getEnchants()))
                        if (itemMeta.hasEnchants()) itemMeta.getEnchants().keySet().forEach(itemMeta::removeEnchant);
                    if (modelItemMeta.hasEnchants())
                        modelItemMeta.getEnchants().forEach((key1, value) -> itemMeta.addEnchant(key1, value, false));
                }
                if(!itemMeta.getItemFlags().equals(modelItemMeta.getItemFlags())) {
                    itemMeta.getItemFlags().forEach(itemMeta::removeItemFlags);
                    modelItemMeta.getItemFlags().forEach(itemMeta::addItemFlags);
                }
                item.setItemMeta(itemMeta);

            } else item.setType(Material.AIR);
        }
    }



}
