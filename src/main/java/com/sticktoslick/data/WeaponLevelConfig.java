package com.sticktoslick.data;

import java.util.Set;

/**
 * Holds XP thresholds, stat-point levels, and milestone levels configuration.
 */
public final class WeaponLevelConfig {
    private WeaponLevelConfig() {
    }

    public static final int MAX_LEVEL = 50;

    /**
     * XP required to advance FROM the given level TO level+1.
     * Index = current level (1-based, so index 0 is unused).
     */
    private static final int[] XP_THRESHOLDS = new int[MAX_LEVEL + 1];

    static {
        // Progressive scaling: Level 0 to 1 requires 50 XP
        XP_THRESHOLDS[0] = 50;
        for (int i = 1; i <= MAX_LEVEL; i++) {
            XP_THRESHOLDS[i] = 50 + (i * i * 10); // e.g. L1->L2 = 60, L5->L6 = 300, L29->L30 = 8460
        }
    }

    /**
     * Milestone levels where evolution happens (not stat points).
     */
    public static final Set<Integer> MILESTONE_LEVELS = Set.of(5, 10, 15, 20, 25, 30, 40, 50);

    /**
     * Returns true if the given level is a milestone (evolution) level.
     */
    public static boolean isMilestone(int level) {
        return MILESTONE_LEVELS.contains(level);
    }

    /**
     * Returns the XP needed to go from fromLevel to fromLevel+1.
     */
    public static int getXPForNextLevel(int fromLevel) {
        if (fromLevel < 0 || fromLevel >= MAX_LEVEL)
            return Integer.MAX_VALUE;
        return XP_THRESHOLDS[fromLevel];
    }

    // ─── XP awarded per mob type ────────────────────────────

    public static final int XP_PASSIVE = 5; // Chicken, Pig, etc.
    public static final int XP_HOSTILE = 15; // Zombie, Skeleton, etc.
    public static final int XP_NETHER = 30; // Blaze, Ghast, Piglin Brute
    public static final int XP_MINI_BOSS = 80; // Elder Guardian, Warden
    public static final int XP_BOSS = 300; // Wither, Ender Dragon
}
