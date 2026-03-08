package com.sticktoslick.data;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.*;

/**
 * Defines all evolution transitions.
 * Key: "currentWeaponClass" -> Map of (catalyst Item -> "newWeaponClass")
 */
public final class EvolutionPath {
        private EvolutionPath() {
        }

        public record Evolution(Item catalyst, String targetClass, int requiredLevel) {
        }

        // Map from currentClass -> list of possible evolutions
        private static final Map<String, List<Evolution>> PATHS = new HashMap<>();

        static {
                // ──── Level 5: Base Branches (from Level 1 Root) ────
                PATHS.put("weapon_wooden_stick", List.of(
                                new Evolution(Items.FLINT, "weapon_dagger", 5),
                                new Evolution(Items.IRON_NUGGET, "weapon_shortsword", 5),
                                new Evolution(Items.BONE, "weapon_spear", 5),
                                new Evolution(Items.OAK_LOG, "weapon_club", 5)));

                // ──── Level 10: Tier 1 Upgrades ────
                PATHS.put("weapon_dagger", List.of(new Evolution(Items.IRON_INGOT, "weapon_dirk", 10)));
                PATHS.put("weapon_shortsword", List.of(new Evolution(Items.IRON_INGOT, "weapon_arming_sword", 10)));
                PATHS.put("weapon_spear", List.of(new Evolution(Items.IRON_INGOT, "weapon_trident", 10)));
                PATHS.put("weapon_club", List.of(new Evolution(Items.IRON_INGOT, "weapon_mace", 10)));

                // ──── Level 15: First Hybrids ────
                PATHS.put("weapon_dirk", List.of(new Evolution(Items.GOLD_INGOT, "weapon_saber", 15)));
                PATHS.put("weapon_arming_sword", List.of(
                                new Evolution(Items.GOLD_INGOT, "weapon_saber", 15),
                                new Evolution(Items.IRON_BLOCK, "weapon_longsword", 15)));
                PATHS.put("weapon_trident", List.of(
                                new Evolution(Items.IRON_BLOCK, "weapon_longsword", 15),
                                new Evolution(Items.STONE, "weapon_lucerne_hammer", 15)));
                PATHS.put("weapon_mace", List.of(
                                new Evolution(Items.STONE, "weapon_lucerne_hammer", 15),
                                new Evolution(Items.CHAIN, "weapon_morning_star", 15)));

                // ──── Level 20: Specialization ────
                PATHS.put("weapon_saber", List.of(new Evolution(Items.DIAMOND, "weapon_katana", 20)));
                PATHS.put("weapon_longsword", List.of(new Evolution(Items.DIAMOND, "weapon_bastard_sword", 20)));
                PATHS.put("weapon_lucerne_hammer", List.of(new Evolution(Items.IRON_AXE, "weapon_halberd", 20)));
                PATHS.put("weapon_morning_star", List.of(new Evolution(Items.OBSIDIAN, "weapon_warhammer", 20)));

                // ──── Level 25: Heavy Hybrids ────
                PATHS.put("weapon_katana", List.of(new Evolution(Items.NETHERITE_SCRAP, "weapon_nodachi", 25)));
                PATHS.put("weapon_bastard_sword", List.of(
                                new Evolution(Items.NETHERITE_SCRAP, "weapon_nodachi", 25),
                                new Evolution(Items.OBSIDIAN, "weapon_claymore", 25)));
                PATHS.put("weapon_halberd", List.of(new Evolution(Items.EMERALD, "weapon_partisan", 25)));
                PATHS.put("weapon_warhammer", List.of(new Evolution(Items.IRON_BLOCK, "weapon_great_maul", 25)));

                // ──── Level 30: Historical Peak ────
                PATHS.put("weapon_nodachi", List.of(new Evolution(Items.NETHERITE_INGOT, "weapon_zweihander", 30)));
                PATHS.put("weapon_claymore", List.of(new Evolution(Items.NETHERITE_INGOT, "weapon_zweihander", 30)));
                PATHS.put("weapon_partisan", List.of(new Evolution(Items.FEATHER, "weapon_winged_lance", 30)));
                PATHS.put("weapon_great_maul",
                                List.of(new Evolution(Items.WITHER_SKELETON_SKULL, "weapon_executioners_axe", 30)));

                // ──── Level 40: Mythological (The 3 Archetypes) ────
                PATHS.put("weapon_zweihander", List.of(new Evolution(Items.MAGMA_CREAM, "weapon_dragon_slayer", 40)));
                PATHS.put("weapon_winged_lance", List.of(new Evolution(Items.LIGHTNING_ROD, "weapon_gungnir", 40)));
                PATHS.put("weapon_executioners_axe",
                                List.of(new Evolution(Items.ECHO_SHARD, "weapon_void_crusher", 40)));

                // ──── Level 50: Godly Finales ────
                PATHS.put("weapon_dragon_slayer", List.of(new Evolution(Items.NETHER_STAR, "weapon_genesis", 50)));
                PATHS.put("weapon_gungnir", List.of(new Evolution(Items.NETHER_STAR, "weapon_longinus", 50)));
                PATHS.put("weapon_void_crusher", List.of(new Evolution(Items.NETHER_STAR, "weapon_atlas", 50)));
        }

        /**
         * Returns possible evolutions for the given weapon class, or empty list if
         * none.
         */
        public static List<Evolution> getEvolutions(String currentClass) {
                return PATHS.getOrDefault(currentClass, Collections.emptyList());
        }

        /**
         * Finds a matching evolution for the given weapon class and catalyst item.
         */
        public static Optional<Evolution> findEvolution(String currentClass, net.minecraft.world.item.Item catalyst) {
                return getEvolutions(currentClass).stream()
                                .filter(e -> e.catalyst() == catalyst)
                                .findFirst();
        }

        public static boolean hasPendingEvolution(String currentClass, int level) {
                return getEvolutions(currentClass).stream().anyMatch(e -> level >= e.requiredLevel());
        }

        public static boolean canEvolve(String currentClass, int level, net.minecraft.world.item.Item catalyst) {
                return findEvolution(currentClass, catalyst)
                                .map(e -> level >= e.requiredLevel())
                                .orElse(false);
        }

        public static String getTargetClass(String currentClass, net.minecraft.world.item.Item catalyst) {
                return findEvolution(currentClass, catalyst)
                                .map(Evolution::targetClass)
                                .orElse(currentClass);
        }
}
