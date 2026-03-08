package com.sticktoslick.data;

import net.minecraft.ChatFormatting;

/**
 * Defines the progression tiers for weapons in Stick to Slick.
 * Each tier has a unique name, color, and minimum level requirement.
 */
public enum WeaponTierData {
    STARTER("Root", ChatFormatting.GRAY, 1, 0x95A5A6),
    BASE("Base Branches", ChatFormatting.GREEN, 5, 0x27AE60),
    TIER_1("Tier 1 Upgrades", ChatFormatting.WHITE, 10, 0xECF0F1),
    HYBRID_1("First Hybrids", ChatFormatting.AQUA, 15, 0x3498DB),
    SPECIALIZATION("Specialization", ChatFormatting.LIGHT_PURPLE, 20, 0x9B59B6),
    HEAVY_HYBRID("Heavy Hybrids", ChatFormatting.DARK_RED, 25, 0x8E44AD),
    HISTORICAL("Historical Peak", ChatFormatting.GOLD, 30, 0xF1C40F),
    MYTHIC("Mythological", ChatFormatting.BLUE, 40, 0x1E90FF),
    GODLY("Godly Finales", ChatFormatting.WHITE, 50, 0xFFFFFF); // Can be styled with pure white/glow

    public final String name;
    public final ChatFormatting formatting;
    public final int minLevel;
    public final int color; // Hex color for HUD/GUI elements

    WeaponTierData(String name, ChatFormatting formatting, int minLevel, int color) {
        this.name = name;
        this.formatting = formatting;
        this.minLevel = minLevel;
        this.color = color;
    }

    /**
     * Gets the current tier based on the weapon's level.
     */
    public static WeaponTierData getFromLevel(int level) {
        if (level >= 50)
            return GODLY;
        if (level >= 40)
            return MYTHIC;
        if (level >= 30)
            return HISTORICAL;
        if (level >= 25)
            return HEAVY_HYBRID;
        if (level >= 20)
            return SPECIALIZATION;
        if (level >= 15)
            return HYBRID_1;
        if (level >= 10)
            return TIER_1;
        if (level >= 5)
            return BASE;
        return STARTER;
    }
}
