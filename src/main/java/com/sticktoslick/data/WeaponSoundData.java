package com.sticktoslick.data;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.Map;

/**
 * Maps each weapon class to a unique hit sound and pitch.
 * Gives every weapon a distinctive audio identity.
 */
public final class WeaponSoundData {
    private WeaponSoundData() {
    }

    public record SoundInfo(SoundEvent hitSound, float pitch) {
    }

    public static final Map<String, SoundInfo> SOUNDS = Map.ofEntries(
            // Starter
            Map.entry("starter", new SoundInfo(SoundEvents.PLAYER_ATTACK_WEAK, 1.0f)),

            // Primal
            Map.entry("hand_axe", new SoundInfo(SoundEvents.PLAYER_ATTACK_STRONG, 0.8f)),
            Map.entry("spear", new SoundInfo(SoundEvents.TRIDENT_HIT, 1.2f)),
            Map.entry("club", new SoundInfo(SoundEvents.ANVIL_LAND, 0.6f)),
            Map.entry("dagger", new SoundInfo(SoundEvents.PLAYER_ATTACK_SWEEP, 1.5f)),

            // Iron Age
            Map.entry("battle_axe", new SoundInfo(SoundEvents.PLAYER_ATTACK_CRIT, 0.7f)),
            Map.entry("pike", new SoundInfo(SoundEvents.TRIDENT_HIT, 1.0f)),
            Map.entry("mace", new SoundInfo(SoundEvents.ANVIL_USE, 0.5f)),
            Map.entry("shortsword", new SoundInfo(SoundEvents.PLAYER_ATTACK_SWEEP, 1.3f)),

            // Specialization
            Map.entry("longsword", new SoundInfo(SoundEvents.PLAYER_ATTACK_STRONG, 1.0f)),
            Map.entry("falchion", new SoundInfo(SoundEvents.PLAYER_ATTACK_SWEEP, 0.9f)),
            Map.entry("double_axe", new SoundInfo(SoundEvents.PLAYER_ATTACK_CRIT, 0.6f)),
            Map.entry("glaive", new SoundInfo(SoundEvents.TRIDENT_HIT, 0.8f)),
            Map.entry("halberd", new SoundInfo(SoundEvents.PLAYER_ATTACK_KNOCKBACK, 0.7f)),
            Map.entry("flail", new SoundInfo(SoundEvents.CHAIN_BREAK, 1.0f)),
            Map.entry("war_hammer", new SoundInfo(SoundEvents.ANVIL_LAND, 0.4f)),

            // Masterworks
            Map.entry("rapier", new SoundInfo(SoundEvents.PLAYER_ATTACK_SWEEP, 1.6f)),
            Map.entry("katana", new SoundInfo(SoundEvents.PLAYER_ATTACK_SWEEP, 1.8f)),
            Map.entry("greatsword", new SoundInfo(SoundEvents.PLAYER_ATTACK_CRIT, 0.5f)),
            Map.entry("lance", new SoundInfo(SoundEvents.TRIDENT_HIT, 0.9f)),

            // Dark Age
            Map.entry("ignis_halberd", new SoundInfo(SoundEvents.FIRECHARGE_USE, 0.8f)),
            Map.entry("windpiercer", new SoundInfo(SoundEvents.PLAYER_ATTACK_SWEEP, 2.0f)),
            Map.entry("cursed_odachi", new SoundInfo(SoundEvents.WITHER_HURT, 1.2f)),
            Map.entry("dreadnought_axe", new SoundInfo(SoundEvents.PLAYER_ATTACK_CRIT, 0.4f)),

            // Legendary
            Map.entry("spear_of_heavens", new SoundInfo(SoundEvents.LIGHTNING_BOLT_THUNDER, 1.5f)),
            Map.entry("excalibur", new SoundInfo(SoundEvents.PLAYER_ATTACK_CRIT, 1.2f)),
            Map.entry("muramasa", new SoundInfo(SoundEvents.WITHER_HURT, 0.8f)),
            Map.entry("void_crusher", new SoundInfo(SoundEvents.GENERIC_EXPLODE, 0.6f)));

    public static SoundInfo get(String weaponClass) {
        return SOUNDS.getOrDefault(weaponClass, SOUNDS.get("starter"));
    }
}
