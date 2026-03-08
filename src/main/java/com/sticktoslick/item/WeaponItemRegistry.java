package com.sticktoslick.item;

import net.minecraft.world.item.Item;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps weaponClass string identifiers to their corresponding registered Item
 * instances.
 * Used by evolution logic to swap ItemStacks and by the EvolutionTreeScreen to
 * display correct textures.
 */
public final class WeaponItemRegistry {
    private WeaponItemRegistry() {
    }

    private static final Map<String, java.util.function.Supplier<Item>> CLASS_TO_ITEM = new HashMap<>();

    static {
        // Level 1
        CLASS_TO_ITEM.put("weapon_wooden_stick", () -> ModItems.WEAPON_WOODEN_STICK.get());
        // Level 5
        CLASS_TO_ITEM.put("weapon_dagger", () -> ModItems.WEAPON_DAGGER.get());
        CLASS_TO_ITEM.put("weapon_shortsword", () -> ModItems.WEAPON_SHORTSWORD.get());
        CLASS_TO_ITEM.put("weapon_spear", () -> ModItems.WEAPON_SPEAR.get());
        CLASS_TO_ITEM.put("weapon_club", () -> ModItems.WEAPON_CLUB.get());
        // Level 10
        CLASS_TO_ITEM.put("weapon_dirk", () -> ModItems.WEAPON_DIRK.get());
        CLASS_TO_ITEM.put("weapon_arming_sword", () -> ModItems.WEAPON_ARMING_SWORD.get());
        CLASS_TO_ITEM.put("weapon_trident", () -> ModItems.WEAPON_TRIDENT.get());
        CLASS_TO_ITEM.put("weapon_mace", () -> ModItems.WEAPON_MACE.get());
        // Level 15
        CLASS_TO_ITEM.put("weapon_saber", () -> ModItems.WEAPON_SABER.get());
        CLASS_TO_ITEM.put("weapon_longsword", () -> ModItems.WEAPON_LONGSWORD.get());
        CLASS_TO_ITEM.put("weapon_lucerne_hammer", () -> ModItems.WEAPON_LUCERNE_HAMMER.get());
        CLASS_TO_ITEM.put("weapon_morning_star", () -> ModItems.WEAPON_MORNING_STAR.get());
        // Level 20
        CLASS_TO_ITEM.put("weapon_katana", () -> ModItems.WEAPON_KATANA.get());
        CLASS_TO_ITEM.put("weapon_bastard_sword", () -> ModItems.WEAPON_BASTARD_SWORD.get());
        CLASS_TO_ITEM.put("weapon_halberd", () -> ModItems.WEAPON_HALBERD.get());
        CLASS_TO_ITEM.put("weapon_warhammer", () -> ModItems.WEAPON_WARHAMMER.get());
        // Level 25
        CLASS_TO_ITEM.put("weapon_nodachi", () -> ModItems.WEAPON_NODACHI.get());
        CLASS_TO_ITEM.put("weapon_claymore", () -> ModItems.WEAPON_CLAYMORE.get());
        CLASS_TO_ITEM.put("weapon_partisan", () -> ModItems.WEAPON_PARTISAN.get());
        CLASS_TO_ITEM.put("weapon_great_maul", () -> ModItems.WEAPON_GREAT_MAUL.get());
        // Level 30
        CLASS_TO_ITEM.put("weapon_zweihander", () -> ModItems.WEAPON_ZWEIHANDER.get());
        CLASS_TO_ITEM.put("weapon_winged_lance", () -> ModItems.WEAPON_WINGED_LANCE.get());
        CLASS_TO_ITEM.put("weapon_executioners_axe", () -> ModItems.WEAPON_EXECUTIONERS_AXE.get());
        // Level 40
        CLASS_TO_ITEM.put("weapon_dragon_slayer", () -> ModItems.WEAPON_DRAGON_SLAYER.get());
        CLASS_TO_ITEM.put("weapon_gungnir", () -> ModItems.WEAPON_GUNGNIR.get());
        CLASS_TO_ITEM.put("weapon_void_crusher", () -> ModItems.WEAPON_VOID_CRUSHER.get());
        // Level 50
        CLASS_TO_ITEM.put("weapon_genesis", () -> ModItems.WEAPON_GENESIS.get());
        CLASS_TO_ITEM.put("weapon_longinus", () -> ModItems.WEAPON_LONGINUS.get());
        CLASS_TO_ITEM.put("weapon_atlas", () -> ModItems.WEAPON_ATLAS.get());
    }

    /**
     * Returns the Item for the given weapon class, or the wooden stick if not
     * found.
     */
    public static Item getItem(String weaponClass) {
        var supplier = CLASS_TO_ITEM.get(weaponClass);
        return supplier != null ? supplier.get() : ModItems.WEAPON_WOODEN_STICK.get();
    }
}
