package com.rit.sucy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for custom enchantments
 */
public abstract class CustomEnchantment {

    /**
     * Name of the enchantment
     */
    protected String enchantName;

    /**
     * Names of all the items that can receive this enchantment at an enchanting table
     */
    protected String[] naturalItems;

    /**
     * Weight of the enchantment
     */
    int weight;

    /**
     * Creates a new custom enchantment with the given name that can be
     * enchanted onto the items using an enchantment table with names
     * given in the array.
     *
     * @param name         the unique name of the enchantment
     * @param naturalItems the names of items that can normally have this enchantment
     */
    public CustomEnchantment(String name, String[] naturalItems) {
        this(name, naturalItems, 5);
    }

    /**
     * Creates a new custom enchantment with the given name that can
     * be enchanted onto the items using an enchantment table with names
     * given in the array. The chance of this enchantment occurring is
     * based on the weight (generally between 1 and 10, 1 being the most rare)
     *
     * @param name         the unique name of the enchantment
     * @param naturalItems the names of items that can normally have this enchantment
     * @param weight       the weight of the enchantment
     */
    public CustomEnchantment(String name, String[] naturalItems, int weight) {
        this.enchantName = name;
        this.naturalItems = naturalItems;
        this.weight = weight;
    }

    /**
     * Retrieves the name of the enchantment
     *
     * @return Enchantment name
     */
    public String name() {
        return enchantName;
    }

    /**
     * Retrieves the level of enchantment depending on the modified exp level (max 49)
     *
     * @param  expLevel the experience level the player used (between 1 and 49)
     * @return          returns the enchantment level; returns < 1 if the enchantment should not be applied
     */
    public int getEnchantmentLevel(int expLevel) {
        return 1;
    }

    /**
     * Checks if this enchantment can be normally applied to the item.
     *
     * @param  item the item to check for
     * @return      true if the enchantment can be normally applied, false otherwise
     */
    public boolean canEnchantOnto(ItemStack item) {
        for (String validItem : naturalItems) {
            if (item.getType().name().equalsIgnoreCase(validItem)) return true;
        }
        return false;
    }

    /**
     * Adds this enchantment onto the given item with the enchantment level provided
     *
     * @param  item         the item being enchanted
     * @param  enchantLevel the level of enchantment
     * @return              the enchanted item
     */
    public ItemStack addToItem(ItemStack item, int enchantLevel) {
        ItemMeta meta = item.getItemMeta();
        List<String> metaLore = meta.getLore() == null ? new ArrayList<String>() : meta.getLore();

        // Make sure the enchantment doesn't already exist on the item
        for (String lore : metaLore) {
            if (lore.contains(enchantName)) {

                // Confirm that the enchanting name is the same
                String loreName = ENameParser.parseName(lore);
                if (loreName == null) continue;
                if (!enchantName.equalsIgnoreCase(loreName)) continue;

                // Compare the enchantment levels
                String[] pieces = lore.split(" ");
                int level = ERomanNumeral.getValueOf(pieces[pieces.length - 1]);
                if (level == 0) continue;

                // Leave higher enchantments alone
                if (level >= enchantLevel) return item;

                // Replace lower enchantments
                List<String> newLore = meta.getLore();
                newLore.remove(lore);
                meta.setLore(newLore);
                break;
            }
        }

        // Add the enchantment
        metaLore.add(0, ChatColor.GRAY + enchantName + " " + ERomanNumeral.numeralOf(enchantLevel));
        meta.setLore(metaLore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Removes this enchantment from the item if it exists
     *
     * @param item item to remove this enchantment from
     * @return     the item without this enchantment
     */
    public ItemStack removeFromItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        if (!meta.hasLore()) return item;
        List<String> metaLore = meta.getLore();

        // Make sure the enchantment doesn't already exist on the item
        for (String lore : metaLore) {
            if (lore.contains(enchantName)) {

                // Confirm that the enchanting name is the same
                String loreName = ENameParser.parseName(lore);
                if (loreName == null) continue;
                if (!enchantName.equalsIgnoreCase(loreName)) continue;

                // Compare the enchantment levels
                List<String> newLore = meta.getLore();
                newLore.remove(lore);
                meta.setLore(newLore);
                item.setItemMeta(meta);
                return item;
            }
        }
        return item;
    }

    /**
     * Applies the enchantment affect when attacking someone
     *
     * @param user         the entity that has the enchantment
     * @param target       the entity that was struck by the enchantment
     * @param enchantLevel the level of the used enchantment
     * @param event        the event details
     */
    public void applyEffect(LivingEntity user, LivingEntity target, int enchantLevel, EntityDamageByEntityEvent event) { }

    /**
     * Applies the enchantment defensively (when taking damage)
     *
     * @param user         the entity hat has the enchantment
     * @param target       the entity that attacked the enchantment, can be null
     * @param enchantLevel the level of the used enchantment
     * @param event        the event details (EntityDamageByEntityEvent, EntityDamageByBlockEvent, or just EntityDamageEvent)
     */
    public void applyDefenseEffect(LivingEntity user, LivingEntity target,
            int enchantLevel, EntityDamageEvent event) {}

    /**
     * Applies effects while breaking blocks (for tool effects)
     *
     * @param player the player with the enchantment
     * @param block  the block being broken
     * @param event  the event details (either BlockBreakEvent or BlockDamageEvent)
     */
    public void applyToolEffect(Player player, Block block, int enchantLevel, BlockEvent event) {}

    /**
     * Applies effects when the player left or right clicks (For other kinds of enchantments like spells)
     *
     * @param player the player with the enchantment
     * @param event  the event details
     */
    public void applyMiscEffect(Player player, int enchantLevel, PlayerInteractEvent event) {}

    /**
     * Applies effects when the item is equipped
     *
     * @param player       the player that equipped it
     * @param enchantLevel the level of enchantment
     */
    public void applyEquipEffect(Player player, int enchantLevel) {}

    /**
     * Applies effects when the item is unequipped
     *
     * @param player       the player that unequipped it
     * @param enchantLevel the level of enchantment
     */
    public void applyUnequipEffect(Player player, int enchantLevel) {}
}
