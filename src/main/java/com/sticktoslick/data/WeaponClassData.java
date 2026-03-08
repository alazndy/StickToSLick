package com.sticktoslick.data;

import java.util.Map;

/**
 * Stores every weapon class's base stats:
 * base damage, base attack speed, max durability, enchant slots.
 */
public final class WeaponClassData {
    private WeaponClassData() {
    }

    public record WeaponStats(
            float baseDamage,
            float baseAttackSpeed,
            int maxDurability,
            int enchantSlots,
            String displayNameKey) {
    }

    // ─── All weapon classes ─────────────────────────────────
    public static final Map<String, WeaponStats> WEAPON_STATS = Map.ofEntries(
            // Level 1: Root
            Map.entry("weapon_wooden_stick",
                    new WeaponStats(2.0f, 1.6f, 100, 0, "item.sticktoslick.weapon_wooden_stick")),

            // Level 5: Base Branches
            Map.entry("weapon_dagger", new WeaponStats(3.0f, 2.2f, 200, 1, "item.sticktoslick.weapon_dagger")),
            Map.entry("weapon_shortsword", new WeaponStats(4.0f, 1.6f, 250, 1, "item.sticktoslick.weapon_shortsword")),
            Map.entry("weapon_spear", new WeaponStats(3.5f, 1.4f, 250, 1, "item.sticktoslick.weapon_spear")),
            Map.entry("weapon_club", new WeaponStats(5.0f, 0.9f, 300, 1, "item.sticktoslick.weapon_club")),

            // Level 10: Tier 1 Upgrades
            Map.entry("weapon_dirk", new WeaponStats(4.5f, 2.0f, 400, 2, "item.sticktoslick.weapon_dirk")),
            Map.entry("weapon_arming_sword",
                    new WeaponStats(5.5f, 1.6f, 450, 2, "item.sticktoslick.weapon_arming_sword")),
            Map.entry("weapon_trident", new WeaponStats(5.0f, 1.3f, 450, 2, "item.sticktoslick.weapon_trident")),
            Map.entry("weapon_mace", new WeaponStats(6.5f, 0.8f, 500, 2, "item.sticktoslick.weapon_mace")),

            // Level 15: First Hybrids
            Map.entry("weapon_saber", new WeaponStats(6.5f, 1.9f, 600, 3, "item.sticktoslick.weapon_saber")),
            Map.entry("weapon_longsword", new WeaponStats(7.5f, 1.3f, 650, 3, "item.sticktoslick.weapon_longsword")),
            Map.entry("weapon_lucerne_hammer",
                    new WeaponStats(7.0f, 1.1f, 650, 3, "item.sticktoslick.weapon_lucerne_hammer")),
            Map.entry("weapon_morning_star",
                    new WeaponStats(8.5f, 0.8f, 700, 3, "item.sticktoslick.weapon_morning_star")),

            // Level 20: Specialization
            Map.entry("weapon_katana", new WeaponStats(8.5f, 1.8f, 900, 4, "item.sticktoslick.weapon_katana")),
            Map.entry("weapon_bastard_sword",
                    new WeaponStats(9.5f, 1.2f, 1000, 4, "item.sticktoslick.weapon_bastard_sword")),
            Map.entry("weapon_halberd", new WeaponStats(9.0f, 1.0f, 1000, 4, "item.sticktoslick.weapon_halberd")),
            Map.entry("weapon_warhammer", new WeaponStats(11.0f, 0.7f, 1100, 4, "item.sticktoslick.weapon_warhammer")),

            // Level 25: Heavy Hybrids
            Map.entry("weapon_nodachi", new WeaponStats(11.0f, 1.3f, 1400, 5, "item.sticktoslick.weapon_nodachi")),
            Map.entry("weapon_claymore", new WeaponStats(12.0f, 1.0f, 1500, 5, "item.sticktoslick.weapon_claymore")),
            Map.entry("weapon_partisan", new WeaponStats(10.5f, 1.1f, 1500, 5, "item.sticktoslick.weapon_partisan")),
            Map.entry("weapon_great_maul",
                    new WeaponStats(13.0f, 0.6f, 1600, 5, "item.sticktoslick.weapon_great_maul")),

            // Level 30: Historical Peak
            Map.entry("weapon_zweihander",
                    new WeaponStats(14.0f, 0.9f, 2000, 6, "item.sticktoslick.weapon_zweihander")),
            Map.entry("weapon_winged_lance",
                    new WeaponStats(13.0f, 1.2f, 2000, 6, "item.sticktoslick.weapon_winged_lance")),
            Map.entry("weapon_executioners_axe",
                    new WeaponStats(16.0f, 0.6f, 2200, 6, "item.sticktoslick.weapon_executioners_axe")),

            // Level 40: Mythological
            Map.entry("weapon_dragon_slayer",
                    new WeaponStats(22.0f, 0.8f, 4000, 7, "item.sticktoslick.weapon_dragon_slayer")),
            Map.entry("weapon_gungnir", new WeaponStats(20.0f, 1.3f, 4000, 7, "item.sticktoslick.weapon_gungnir")),
            Map.entry("weapon_void_crusher",
                    new WeaponStats(25.0f, 0.5f, 4500, 7, "item.sticktoslick.weapon_void_crusher")),

            // Level 50: Godly Finales
            Map.entry("weapon_genesis", new WeaponStats(30.0f, 1.5f, 8000, 8, "item.sticktoslick.weapon_genesis")),
            Map.entry("weapon_longinus", new WeaponStats(28.0f, 1.6f, 8000, 8, "item.sticktoslick.weapon_longinus")),
            Map.entry("weapon_atlas", new WeaponStats(35.0f, 0.4f, 9000, 8, "item.sticktoslick.weapon_atlas")));

    public static WeaponStats get(String weaponClass) {
        return WEAPON_STATS.getOrDefault(weaponClass, WEAPON_STATS.get("weapon_wooden_stick"));
    }
}
