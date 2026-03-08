package com.sticktoslick.event;

import com.sticktoslick.data.WeaponNBTHelper;
import com.sticktoslick.data.WeaponSoundData;
import com.sticktoslick.data.WeaponTraitData;
import com.sticktoslick.item.StarterStickItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import java.util.List;

/**
 * Applies weapon trait passive effects during combat.
 * Each trait effect scales with the weapon's trait level.
 */
public class TraitEffectHandler {

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof Player player))
            return;

        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof StarterStickItem))
            return;
        if (!WeaponNBTHelper.hasWeaponData(mainHand))
            return;

        String weaponClass = WeaponNBTHelper.getWeaponClass(mainHand);
        int traitLevel = WeaponNBTHelper.getTraitLevel(mainHand);

        // Play weapon-specific hit sound
        WeaponSoundData.SoundInfo soundInfo = WeaponSoundData.get(weaponClass);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                soundInfo.hitSound(), net.minecraft.sounds.SoundSource.PLAYERS, 0.7f, soundInfo.pitch());

        // Track damage dealt
        WeaponNBTHelper.addDamageDealt(mainHand, (int) event.getAmount());

        // Send hit feedback to player (for camera shake/impact feel)
        if (player instanceof ServerPlayer sp) {
            com.sticktoslick.network.ModMessages.sendToPlayer(
                    new com.sticktoslick.network.S2CHitFeedbackPacket(event.getAmount(), player.fallDistance > 0.1f),
                    sp);
        }

        if (traitLevel <= 0)
            return;

        WeaponTraitData.TraitInfo trait = WeaponTraitData.get(weaponClass);
        float totalEffect = trait.effectPerLevel() * traitLevel;
        LivingEntity victim = event.getEntity();

        switch (trait.effectType()) {
            case "bonus_xp":
                // Handled in CombatEventHandler during XP calculation
                break;

            case "armor_pierce":
                // Increase damage by ignoring a percentage of armor
                float currentDmg = event.getAmount();
                event.setAmount(currentDmg * (1.0f + totalEffect));
                break;

            case "sweeping":
                // Extra area damage — increase base hit damage
                event.setAmount(event.getAmount() * (1.0f + totalEffect));
                break;

            case "crit_chance":
                // Random chance to deal double damage
                if (player.level().random.nextFloat() < totalEffect) {
                    event.setAmount(event.getAmount() * 2.0f);
                }
                break;

            case "slowness":
                // Chance to apply Slowness to target
                if (player.level().random.nextFloat() < totalEffect) {
                    victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                }
                break;

            case "armor_damage":
                // Extra damage to armored targets
                if (victim.getArmorValue() > 0) {
                    event.setAmount(event.getAmount() * (1.0f + totalEffect));
                }
                break;

            case "dodge":
                // Handled defensively — see below
                break;

            case "riposte":
                // After blocking, next hit does extra damage (simplified: just bonus)
                if (player.isBlocking()) {
                    event.setAmount(event.getAmount() * (1.0f + totalEffect));
                }
                break;

            case "bleed":
                // Apply Poison (simulating bleed)
                victim.addEffect(new MobEffectInstance(MobEffects.POISON, (int) totalEffect, 0));
                break;

            case "extra_knockback":
                // Extra knockback multiplier applied by attribute system; simplified here
                break;

            case "lifesteal":
                // Heal attacker based on damage dealt
                float healAmount = event.getAmount() * totalEffect;
                player.heal(healAmount);
                break;

            case "explosion":
                // Large explosion damage — hits nearby enemies
                event.setAmount(event.getAmount() * (1.0f + totalEffect * 0.5f));
                applyAOEDamage(player, victim, 3.0f + totalEffect * 2.0f, event.getAmount() * 0.4f);
                // Visual effect
                player.level().addParticle(net.minecraft.core.particles.ParticleTypes.EXPLOSION, victim.getX(),
                        victim.getY() + 1.0, victim.getZ(), 0, 0, 0);
                break;

            case "charge_damage":
                // Extra damage if player is sprinting
                if (player.isSprinting()) {
                    event.setAmount(event.getAmount() * (1.0f + totalEffect));
                }
                break;

            case "fire_aspect":
                // Set target on fire
                victim.setSecondsOnFire((int) totalEffect);
                break;

            case "speed_boost":
                // Give player Speed after hitting
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, (int) (totalEffect * 10)));
                break;

            case "wither_chance":
                // Chance to apply Wither
                if (player.level().random.nextFloat() < totalEffect) {
                    victim.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 1));
                }
                break;

            case "fear_aura":
                // Apply Slowness and Weakness to nearby enemies
                float fearRadius = 3.5f + (traitLevel * 0.2f);
                List<LivingEntity> nearby = player.level().getEntitiesOfClass(LivingEntity.class,
                        victim.getBoundingBox().inflate(fearRadius),
                        e -> e != player && e != victim && e.isAlive());

                // Primary target gets major effects
                victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
                victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));

                // Nearby targets get minor effects and chip damage
                for (LivingEntity nearbyEntity : nearby) {
                    nearbyEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0));
                    nearbyEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0));
                    nearbyEntity.hurt(player.damageSources().playerAttack(player), event.getAmount() * 0.25f);
                }
                break;

            case "lightning":
                // Chance to strike lightning
                if (player.level().random.nextFloat() < totalEffect && player instanceof ServerPlayer sp) {
                    net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(sp.serverLevel());
                    // Simplified: just deal extra damage
                    event.setAmount(event.getAmount() + 4.0f * traitLevel);
                }
                break;

            case "heal_on_hit":
                // Heal player on each hit
                player.heal(totalEffect);
                break;

            case "blood_price":
                // Deal extra damage but take some self-damage
                event.setAmount(event.getAmount() * (1.0f + totalEffect));
                player.hurt(player.damageSources().magic(), 1.0f);
                break;

            case "aoe_radius":
                // Massive AOE damage — focused on Void Crusher
                float aoeRadius = 4.0f + (totalEffect * 5.0f);
                event.setAmount(event.getAmount() * (1.0f + totalEffect * 0.3f)); // Main target buff
                applyAOEDamage(player, victim, aoeRadius, event.getAmount() * 0.6f);
                break;
        }
    }

    /**
     * Helper to deal damage to enemies in a radius.
     */
    private void applyAOEDamage(Player player, LivingEntity victim, float radius, float damage) {
        List<LivingEntity> targets = player.level().getEntitiesOfClass(LivingEntity.class,
                victim.getBoundingBox().inflate(radius),
                e -> e != player && e != victim && e.isAlive());

        for (LivingEntity target : targets) {
            target.hurt(player.damageSources().playerAttack(player), damage);
        }
    }

    /**
     * Defensive trait: Dodge chance (for shortsword / dagger-like weapons).
     * Subscribe to player being hurt.
     */
    @SubscribeEvent
    public void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        ItemStack mainHand = player.getMainHandItem();
        if (!(mainHand.getItem() instanceof StarterStickItem))
            return;
        if (!WeaponNBTHelper.hasWeaponData(mainHand))
            return;

        String weaponClass = WeaponNBTHelper.getWeaponClass(mainHand);
        WeaponTraitData.TraitInfo trait = WeaponTraitData.get(weaponClass);

        if ("dodge".equals(trait.effectType())) {
            int traitLevel = WeaponNBTHelper.getTraitLevel(mainHand);
            float dodgeChance = trait.effectPerLevel() * traitLevel;
            if (player.level().random.nextFloat() < dodgeChance) {
                event.setCanceled(true);
            }
        }
    }
}
