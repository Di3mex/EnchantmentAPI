package com.rit.sucy;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Hashtable;
import java.util.Map;

/**
 * Handles keeping track of player equipment for Equip and Unequip enchantment effects
 */
class EEquip extends BukkitRunnable {

    /**
     * Table of player data
     */
    static Hashtable<String, ItemStack[]> equipment = new Hashtable<String, ItemStack[]>();

    /**
     * Loads the equipment of the given player
     *
     * @param player player to load
     */
    static void loadPlayer(Player player) {
        equipment.put(player.getName(), player.getEquipment().getArmorContents());
    }

    /**
     * Clears teh data for the given player
     *
     * @param player player to clear
     */
    static void clearPlayer(Player player) {
        equipment.remove(player.getName());
    }

    /**
     * Clears all player data
     */
    static void clear() {
        equipment.clear();
    }

    /**
     * Player reference
     */
    Player player;

    /**
     * Constructor
     *
     * @param player player to re-evaluate
     */
    public EEquip(Player player) {
        this.player = player;
    }

    /**
     * Performs checks for changes to player equipment
     */
    public void run() {
        ItemStack[] equips = player.getEquipment().getArmorContents();
        ItemStack[] previous = equipment.get(player.getName());
        for (int i = 0; i < equips.length; i++) {
            if (!equips[i].toString().equalsIgnoreCase(previous[i].toString())) {
                doEquip(equips[i]);
                doUnequip(previous[i]);
            }
        }
        equipment.put(player.getName(), equips);
    }

    /**
     * Applies equip actions to the given item
     *
     * @param item the equipment that was just equipped
     */
    private void doEquip(ItemStack item) {
        for (Map.Entry<CustomEnchantment, Integer> enchantment : EnchantmentAPI.getEnchantments(item).entrySet())
            enchantment.getKey().applyEquipEffect(player, enchantment.getValue());
    }

    /**
     * Applies equip actions to the given item
     *
     * @param item the equipment that was just equipped
     */
    private void doUnequip(ItemStack item) {
        for (Map.Entry<CustomEnchantment, Integer> enchantment : EnchantmentAPI.getEnchantments(item).entrySet())
            enchantment.getKey().applyUnequipEffect(player, enchantment.getValue());
    }
}
