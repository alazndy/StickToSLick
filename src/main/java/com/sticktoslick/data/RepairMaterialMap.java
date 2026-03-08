package com.sticktoslick.data;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Map;

/**
 * Maps each weapon class to the item needed to repair it on an anvil.
 * The repair material is always the LAST catalyst used to evolve into that
 * class.
 */
public final class RepairMaterialMap {
    private RepairMaterialMap() {
    }

    private static final Map<String, Item> REPAIR_MATERIALS = Map.ofEntries(
            // Starter - repaired with sticks
            Map.entry("starter", Items.STICK),

            // Level 5: Primal Age
            Map.entry("hand_axe", Items.FLINT),
            Map.entry("spear", Items.BONE),
            Map.entry("club", Items.OAK_LOG),
            Map.entry("dagger", Items.FEATHER),

            // Level 10: Iron Age
            Map.entry("battle_axe", Items.IRON_INGOT),
            Map.entry("pike", Items.IRON_INGOT),
            Map.entry("mace", Items.IRON_INGOT),
            Map.entry("shortsword", Items.IRON_INGOT),

            // Level 15: Specialization
            Map.entry("longsword", Items.GOLD_INGOT),
            Map.entry("falchion", Items.GOLD_INGOT),
            Map.entry("double_axe", Items.IRON_INGOT),
            Map.entry("glaive", Items.PRISMARINE_SHARD),
            Map.entry("halberd", Items.IRON_BLOCK),
            Map.entry("flail", Items.CHAIN),
            Map.entry("war_hammer", Items.OBSIDIAN),

            // Level 20: Masterworks
            Map.entry("rapier", Items.AMETHYST_SHARD),
            Map.entry("katana", Items.DIAMOND),
            Map.entry("greatsword", Items.OBSIDIAN),
            Map.entry("lance", Items.EMERALD),

            // Level 25: Dark Age
            Map.entry("ignis_halberd", Items.BLAZE_ROD),
            Map.entry("windpiercer", Items.PHANTOM_MEMBRANE),
            Map.entry("cursed_odachi", Items.ECHO_SHARD),
            Map.entry("dreadnought_axe", Items.NETHERITE_INGOT),

            // Level 30: Legendary
            Map.entry("excalibur", Items.NETHER_STAR),
            Map.entry("muramasa", Items.DRAGON_BREATH),
            Map.entry("void_crusher", Items.ECHO_SHARD),
            Map.entry("spear_of_heavens", Items.NETHER_STAR));

    /**
     * Returns the item needed to repair the given weapon class, or null if unknown.
     */
    public static Item getRepairMaterial(String weaponClass) {
        return REPAIR_MATERIALS.get(weaponClass);
    }
}
