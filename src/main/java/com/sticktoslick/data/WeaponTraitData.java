package com.sticktoslick.data;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Map;

/**
 * Defines a unique Trait for each weapon class.
 * Each trait has a passive effect that scales with trait level,
 * and a specific material used to upgrade it.
 */
public final class WeaponTraitData {
        private WeaponTraitData() {
        }

        public record TraitInfo(
                        String traitName, // Display name
                        String traitDesc, // Short description
                        Item upgradeMaterial, // Material to level up the trait
                        String effectType, // Internal effect key
                        float effectPerLevel // Effect magnitude per trait level
        ) {
        }

        public static final Map<String, TraitInfo> TRAITS = Map.ofEntries(
                        // ── Level 1: Root ──
                        Map.entry("weapon_wooden_stick",
                                        new TraitInfo("Potansiyel", "+%5 XP kazanımı", Items.STICK, "bonus_xp", 0.05f)),

                        // ── Level 5: Base Branches ──
                        Map.entry("weapon_dagger",
                                        new TraitInfo("Hafiflik", "+%10 saldırı hızı", Items.FLINT, "attack_speed",
                                                        0.10f)),
                        Map.entry("weapon_shortsword",
                                        new TraitInfo("Denge", "+%5 kritik şansı", Items.IRON_NUGGET, "crit_chance",
                                                        0.05f)),
                        Map.entry("weapon_spear",
                                        new TraitInfo("Menzil", "+0.2 saldırı menzili", Items.BONE, "attack_range",
                                                        0.2f)),
                        Map.entry("weapon_club",
                                        new TraitInfo("Sersemletme", "+%5 yavaşlatma şansı", Items.OAK_LOG, "slowness",
                                                        0.05f)),

                        // ── Level 10: Tier 1 Upgrades ──
                        Map.entry("weapon_dirk",
                                        new TraitInfo("Keskinlik", "+%10 kanama şansı", Items.IRON_INGOT,
                                                        "bleed_chance", 0.10f)),
                        Map.entry("weapon_arming_sword",
                                        new TraitInfo("Riposte", "+%8 karşı saldırı hasarı", Items.IRON_INGOT,
                                                        "riposte", 0.08f)),
                        Map.entry("weapon_trident",
                                        new TraitInfo("Delici", "+%10 zırh delme", Items.PRISMARINE_SHARD,
                                                        "armor_pierce", 0.10f)),
                        Map.entry("weapon_mace",
                                        new TraitInfo("Ezme", "+%10 zırhlılara ekstra hasar", Items.IRON_INGOT,
                                                        "armor_damage", 0.10f)),

                        // ── Level 15: First Hybrids ──
                        Map.entry("weapon_saber",
                                        new TraitInfo("Süvari", "+%15 koşarken hasar", Items.GOLD_INGOT,
                                                        "charge_damage", 0.15f)),
                        Map.entry("weapon_longsword",
                                        new TraitInfo("Şövalye", "+%5 kalkan bloğu", Items.GOLD_INGOT, "shield_boost",
                                                        0.05f)),
                        Map.entry("weapon_lucerne_hammer",
                                        new TraitInfo("Durdurucu", "+%10 knockback direnci", Items.IRON_BLOCK,
                                                        "kb_resist", 0.10f)),
                        Map.entry("weapon_morning_star",
                                        new TraitInfo("Vahşet", "+2 tick kanama süresi", Items.CHAIN, "bleed", 2.0f)),

                        // ── Level 20: Specialization ──
                        Map.entry("weapon_katana",
                                        new TraitInfo("Kan Akışı", "+%5 lifesteal", Items.DIAMOND, "lifesteal", 0.05f)),
                        Map.entry("weapon_bastard_sword",
                                        new TraitInfo("Süpürme", "+%15 sweeping hasarı", Items.OBSIDIAN, "sweeping",
                                                        0.15f)),
                        Map.entry("weapon_halberd",
                                        new TraitInfo("Uzun Vuruş", "+0.3 menzil", Items.OBSIDIAN, "attack_range",
                                                        0.3f)),
                        Map.entry("weapon_warhammer",
                                        new TraitInfo("Taş Kıran", "+%15 blok kırma hızı", Items.DIAMOND, "break_speed",
                                                        0.15f)),

                        // ── Level 25: Heavy Hybrids ──
                        Map.entry("weapon_nodachi",
                                        new TraitInfo("İnfaz", "+%20 düşük canlılara hasar", Items.NETHERITE_SCRAP,
                                                        "execute_damage", 0.20f)),
                        Map.entry("weapon_claymore",
                                        new TraitInfo("Yıkım", "+%15 patlama hasarı", Items.NETHERITE_SCRAP,
                                                        "explosion", 0.15f)),
                        Map.entry("weapon_partisan",
                                        new TraitInfo("Savunma İhlali", "+%15 zırh delme", Items.EMERALD,
                                                        "armor_pierce", 0.15f)),
                        Map.entry("weapon_great_maul",
                                        new TraitInfo("Zelzele", "+%15 alan hasarı yarıçapı", Items.OBSIDIAN,
                                                        "aoe_radius", 0.15f)),

                        // ── Level 30: Historical Peak ──
                        Map.entry("weapon_zweihander",
                                        new TraitInfo("Deviren", "+%30 sweeping hasarı", Items.NETHERITE_INGOT,
                                                        "sweeping", 0.30f)),
                        Map.entry("weapon_winged_lance",
                                        new TraitInfo("Hussar", "+%25 koşarken hasar", Items.NETHERITE_INGOT,
                                                        "charge_damage", 0.25f)),
                        Map.entry("weapon_executioners_axe",
                                        new TraitInfo("Korku Aurası", "+%15 yakındakileri yavaşlat",
                                                        Items.WITHER_SKELETON_SKULL, "fear_aura", 0.15f)),

                        // ── Level 40: Mythological ──
                        Map.entry("weapon_dragon_slayer",
                                        new TraitInfo("Ejderha Pulu", "+%20 ateş direnci", Items.MAGMA_CREAM,
                                                        "fire_resist", 0.20f)),
                        Map.entry("weapon_gungnir",
                                        new TraitInfo("Şimşek", "+%15 yıldırım şansı", Items.LIGHTNING_ROD, "lightning",
                                                        0.15f)),
                        Map.entry("weapon_void_crusher",
                                        new TraitInfo("Boşluk Tüketimi", "+%10 wither efekti şansı", Items.ECHO_SHARD,
                                                        "wither_chance", 0.10f)),

                        // ── Level 50: Godly Finales ──
                        Map.entry("weapon_genesis",
                                        new TraitInfo("Kutsama", "+1 iyileşme (her vuruşta)", Items.NETHER_STAR,
                                                        "heal_on_hit", 1.0f)),
                        Map.entry("weapon_longinus",
                                        new TraitInfo("Tanrı Katili", "+%30 zırh delme & boss hasarı",
                                                        Items.NETHER_STAR, "god_slayer", 0.30f)),
                        Map.entry("weapon_atlas", new TraitInfo("Dünya Yıkan", "+%25 deprem (AOE) çapı",
                                        Items.NETHER_STAR, "earthquake", 0.25f)));

        public static TraitInfo get(String weaponClass) {
                return TRAITS.getOrDefault(weaponClass, TRAITS.get("weapon_wooden_stick"));
        }
}
