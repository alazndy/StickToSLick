package com.sticktoslick.data;

import java.util.Map;

/**
 * Maps each weapon class to an animation category.
 * Used by both Better Combat integration and our custom first-person animation
 * system.
 */
public final class WeaponAnimationData {
        private WeaponAnimationData() {
        }

        /**
         * Animation categories - each has a unique swing style.
         */
        public enum AnimCategory {
                STICK, // Basic stick poke
                DAGGER, // Fast stab forward + quick slash
                SWORD, // Classic left-right slash
                KATANA, // Fast diagonal iaido-style cuts
                GREATSWORD, // Slow, heavy wide arc
                AXE, // Overhead chop
                SPEAR, // Forward thrust/lunge
                HALBERD, // Wide sweep + thrust combo
                HAMMER // Overhead slam
        }

        public record AnimInfo(
                        AnimCategory category,
                        float attackRange, // Better Combat range
                        float swingAngle, // Degrees of the swing arc
                        boolean twoHanded,
                        String bcPose, // Better Combat pose ID
                        String[] bcAnimations, // Better Combat animation IDs
                        float[] bcDamageModifiers) {
        }

        // ─── Mapping: weapon class -> animation info ───────────────────
        private static final Map<String, AnimInfo> ANIM_MAP = Map.ofEntries(
                        // ── Level 1: Root ──
                        Map.entry("weapon_wooden_stick", info(AnimCategory.STICK, 2.5f, 40, false, "pose_sword",
                                        new String[] { "sword_slash_right" }, new float[] { 1.0f })),

                        // ── Level 5: Base Branches ──
                        Map.entry("weapon_dagger", info(AnimCategory.DAGGER, 2.0f, 40, false, "pose_dagger",
                                        new String[] { "dagger_stab", "dagger_cut" }, new float[] { 1.0f, 0.8f })),
                        Map.entry("weapon_shortsword", info(AnimCategory.SWORD, 2.5f, 55, false, "pose_sword",
                                        new String[] { "sword_slash_right", "sword_slash_left" },
                                        new float[] { 1.0f, 1.0f })),
                        Map.entry("weapon_spear", info(AnimCategory.SPEAR, 4.0f, 25, true, "pose_spear",
                                        new String[] { "spear_stab" }, new float[] { 1.0f })),
                        Map.entry("weapon_club", info(AnimCategory.HAMMER, 2.5f, 70, false, "pose_hammer",
                                        new String[] { "hammer_slam" }, new float[] { 1.0f })),

                        // ── Level 10: Tier 1 Upgrades ──
                        Map.entry("weapon_dirk", info(AnimCategory.DAGGER, 2.0f, 40, false, "pose_dagger",
                                        new String[] { "dagger_stab", "dagger_cut" }, new float[] { 1.0f, 0.8f })),
                        Map.entry("weapon_arming_sword", info(AnimCategory.SWORD, 3.0f, 55, false, "pose_sword",
                                        new String[] { "sword_slash_right", "sword_slash_left" },
                                        new float[] { 1.0f, 1.0f })),
                        Map.entry("weapon_trident", info(AnimCategory.SPEAR, 4.0f, 25, true, "pose_spear",
                                        new String[] { "spear_stab" }, new float[] { 1.0f })),
                        Map.entry("weapon_mace", info(AnimCategory.HAMMER, 3.0f, 80, false, "pose_hammer",
                                        new String[] { "hammer_slam" }, new float[] { 1.0f })),

                        // ── Level 15: First Hybrids ──
                        Map.entry("weapon_saber", info(AnimCategory.SWORD, 3.0f, 65, false, "pose_sword",
                                        new String[] { "sword_slash_right", "sword_slash_left" },
                                        new float[] { 1.1f, 0.9f })),
                        Map.entry("weapon_longsword", info(AnimCategory.SWORD, 3.5f, 65, false, "pose_sword",
                                        new String[] { "sword_slash_right", "sword_slash_left" },
                                        new float[] { 1.0f, 1.0f })),
                        Map.entry("weapon_lucerne_hammer", info(AnimCategory.HALBERD, 4.0f, 75, true, "pose_spear",
                                        new String[] { "spear_stab", "sword_slash_right" },
                                        new float[] { 1.0f, 1.0f })),
                        Map.entry("weapon_morning_star", info(AnimCategory.HAMMER, 3.0f, 90, false, "pose_hammer",
                                        new String[] { "hammer_slam" }, new float[] { 1.2f })),

                        // ── Level 20: Specialization ──
                        Map.entry("weapon_katana", info(AnimCategory.KATANA, 3.5f, 60, true, "pose_katana",
                                        new String[] { "katana_slash_right", "katana_slash_left" },
                                        new float[] { 1.0f, 1.0f })),
                        Map.entry("weapon_bastard_sword", info(AnimCategory.GREATSWORD, 3.5f, 75, true, "pose_claymore",
                                        new String[] { "claymore_slash_right", "claymore_slash_left" },
                                        new float[] { 1.0f, 1.0f })),
                        Map.entry("weapon_halberd", info(AnimCategory.HALBERD, 4.0f, 75, true, "pose_spear",
                                        new String[] { "sword_slash_right", "spear_stab" },
                                        new float[] { 1.0f, 1.0f })),
                        Map.entry("weapon_warhammer", info(AnimCategory.HAMMER, 3.0f, 90, true, "pose_hammer",
                                        new String[] { "hammer_slam" }, new float[] { 1.2f })),

                        // ── Level 25: Heavy Hybrids ──
                        Map.entry("weapon_nodachi", info(AnimCategory.KATANA, 4.0f, 70, true, "pose_katana",
                                        new String[] { "katana_slash_right", "katana_slash_left" },
                                        new float[] { 1.2f, 1.0f })),
                        Map.entry("weapon_claymore", info(AnimCategory.GREATSWORD, 4.0f, 100, true, "pose_claymore",
                                        new String[] { "claymore_slash_right", "claymore_slash_left" },
                                        new float[] { 1.2f, 1.0f })),
                        Map.entry("weapon_partisan", info(AnimCategory.SPEAR, 4.5f, 40, true, "pose_spear",
                                        new String[] { "spear_stab", "spear_stab" }, new float[] { 1.1f, 1.1f })),
                        Map.entry("weapon_great_maul", info(AnimCategory.HAMMER, 3.5f, 100, true, "pose_hammer",
                                        new String[] { "hammer_slam" }, new float[] { 1.4f })),

                        // ── Level 30: Historical Peak ──
                        Map.entry("weapon_zweihander", info(AnimCategory.GREATSWORD, 4.5f, 100, true, "pose_claymore",
                                        new String[] { "claymore_slash_right", "claymore_slash_left" },
                                        new float[] { 1.2f, 1.2f })),
                        Map.entry("weapon_winged_lance", info(AnimCategory.SPEAR, 5.0f, 20, true, "pose_spear",
                                        new String[] { "spear_stab" }, new float[] { 1.2f })),
                        Map.entry("weapon_executioners_axe", info(AnimCategory.AXE, 3.5f, 110, true, "pose_axe",
                                        new String[] { "axe_slash_right" }, new float[] { 1.5f })),

                        // ── Level 40: Mythological ──
                        Map.entry("weapon_dragon_slayer",
                                        info(AnimCategory.GREATSWORD, 5.0f, 120, true, "pose_claymore",
                                                        new String[] { "claymore_slash_right", "claymore_slash_left",
                                                                        "claymore_slash_right" },
                                                        new float[] { 1.5f, 1.2f, 1.8f })),
                        Map.entry("weapon_gungnir", info(AnimCategory.SPEAR, 6.0f, 20, true, "pose_spear",
                                        new String[] { "spear_stab" }, new float[] { 1.5f })),
                        Map.entry("weapon_void_crusher", info(AnimCategory.HAMMER, 4.0f, 130, true, "pose_hammer",
                                        new String[] { "hammer_slam" }, new float[] { 2.0f })),

                        // ── Level 50: Godly Finales ──
                        Map.entry("weapon_genesis", info(AnimCategory.SWORD, 4.5f, 80, true, "pose_sword",
                                        new String[] { "sword_slash_right", "sword_slash_left" },
                                        new float[] { 1.5f, 1.5f })),
                        Map.entry("weapon_longinus", info(AnimCategory.SPEAR, 6.5f, 20, true, "pose_spear",
                                        new String[] { "spear_stab" }, new float[] { 1.8f })),
                        Map.entry("weapon_atlas", info(AnimCategory.HAMMER, 4.5f, 140, true, "pose_hammer",
                                        new String[] { "hammer_slam" }, new float[] { 2.5f })));

        private static AnimInfo info(AnimCategory cat, float range, float angle, boolean twoHanded,
                        String pose, String[] anims, float[] dmgMods) {
                return new AnimInfo(cat, range, angle, twoHanded, pose, anims, dmgMods);
        }

        public static AnimInfo get(String weaponClass) {
                return ANIM_MAP.getOrDefault(weaponClass, ANIM_MAP.get("weapon_wooden_stick"));
        }
}
