package com.sticktoslick.client;

import com.sticktoslick.StickToSlick;
import com.sticktoslick.data.WeaponNBTHelper;
import com.sticktoslick.data.WeaponTierData;
import com.sticktoslick.item.StarterStickItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.sticktoslick.data.WeaponAnimationData.AnimCategory;
import org.joml.Vector3f;

import java.util.Random;

/**
 * spawns ambient particles around the weapon based on its Tier.
 * Provides that "Premium" feel for high-level weapons.
 */
@Mod.EventBusSubscriber(modid = StickToSlick.MODID, value = Dist.CLIENT)
public class WeaponParticleHandler {

    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused())
            return;

        Player player = mc.player;
        if (player == null)
            return;
        ItemStack stack = player.getMainHandItem();

        if (stack.getItem() instanceof StarterStickItem && WeaponNBTHelper.hasWeaponData(stack)) {
            if (RANDOM.nextInt(5) == 0) { // Don't spawn every tick to save performance
                spawnTierParticles(player, stack);
            }
        }
    }

    private static void spawnTierParticles(Player player, ItemStack stack) {
        int level = WeaponNBTHelper.getLevel(stack);
        WeaponTierData tier = WeaponTierData.getFromLevel(level);

        // No particles for Starter tier to keep it humble
        if (tier == WeaponTierData.STARTER)
            return;

        double x = player.getX() + (RANDOM.nextDouble() - 0.5) * 1.2;
        double y = player.getY() + 0.8 + (RANDOM.nextDouble() - 0.5) * 1.2;
        double z = player.getZ() + (RANDOM.nextDouble() - 0.5) * 1.2;

        switch (tier) {
            case STARTER:
                // No particles
                break;
            case BASE:
            case TIER_1:
                if (RANDOM.nextInt(4) == 0) {
                    player.level().addParticle(ParticleTypes.CRIT, x, y, z, 0, 0, 0);
                }
                break;
            case HYBRID_1:
            case SPECIALIZATION:
                if (RANDOM.nextInt(3) == 0) {
                    player.level().addParticle(ParticleTypes.ENCHANTED_HIT, x, y, z, 0, 0.05, 0);
                }
                break;
            case HEAVY_HYBRID:
            case HISTORICAL:
                if (RANDOM.nextInt(2) == 0) {
                    player.level().addParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, 0, 0.08, 0);
                }
                break;
            case MYTHIC:
                player.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0, 0.05, 0);
                if (RANDOM.nextBoolean()) {
                    player.level().addParticle(ParticleTypes.WITCH, x, y, z, 0, 0, 0);
                }
                break;
            case GODLY:
                player.level().addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0.05, 0);
                if (RANDOM.nextBoolean()) {
                    player.level().addParticle(ParticleTypes.GLOW, x, y, z, 0, 0.02, 0);
                }
                break;
        }
    }

    /**
     * Spawns a beautiful particle arc (slash trail) for the given weapon.
     */
    public static void spawnSlashTrail(Player player, ItemStack stack, AnimCategory category, int comboStep) {
        int level = WeaponNBTHelper.getLevel(stack);
        WeaponTierData tier = WeaponTierData.getFromLevel(level);

        float yaw = player.getYRot();
        float pitch = player.getXRot();

        // Arc parameters
        int particleCount = 20;
        float arcDegrees = 100f; // Default breadth

        switch (category) {
            case GREATSWORD -> arcDegrees = 140f;
            case AXE, HAMMER -> arcDegrees = 80f; // Vertical/Chop
            case SPEAR, DAGGER -> arcDegrees = 30f; // Thrust-focused
            case SWORD, KATANA, STICK -> arcDegrees = 110f;
            case HALBERD -> arcDegrees = 120f;
        }

        for (int i = 0; i < particleCount; i++) {
            float progress = (float) i / (float) particleCount;
            float angleRaw = (progress - 0.5f) * arcDegrees;

            // Adjust angle direction based on combo step
            if (comboStep == 1 && (category == AnimCategory.SWORD || category == AnimCategory.KATANA
                    || category == AnimCategory.STICK || category == AnimCategory.GREATSWORD)) {
                // Reverse slash for combo hit 2
                angleRaw = -angleRaw;
            } else if (comboStep == 2 && category == AnimCategory.KATANA) {
                // Horizontal lightning dash cut
                angleRaw *= 1.5f;
            }

            // Calculate base direction based on player orientation
            Vector3f dir;
            if (category == AnimCategory.AXE || category == AnimCategory.HAMMER
                    || (comboStep == 2 && (category == AnimCategory.SWORD || category == AnimCategory.GREATSWORD
                            || category == AnimCategory.STICK))) {
                // Vertical arc (pitch based) - either standard overhand OR combo 3 overhead
                // slam
                dir = new Vector3f(0, 0, 2.5f);
                dir.rotateX((pitch + angleRaw) * (float) Math.PI / 180f);

                // Add slight tilt based on combo step for vertical weapons
                float tilt = 0f;
                if (category == AnimCategory.AXE || category == AnimCategory.HAMMER) {
                    if (comboStep == 1)
                        tilt = -45f; // Horizontal/Diagonal bash
                    else if (comboStep == 2)
                        tilt = 45f; // Golf swing rise
                }
                dir.rotateY(-(yaw + tilt) * (float) Math.PI / 180f);
            } else {
                // Horizontal arc (yaw based)
                dir = new Vector3f(0, 0, 2.8f);
                dir.rotateX(pitch * (float) Math.PI / 180f);
                dir.rotateY(-(yaw + angleRaw) * (float) Math.PI / 180f);

                // Add slight vertical tilt for certain horizontal cuts
                if (category == AnimCategory.KATANA) {
                    dir.rotateZ(comboStep == 0 ? 0.3f : (comboStep == 1 ? -0.3f : 0));
                }
            }

            double px = player.getX() + dir.x();
            double py = player.getY() + 1.2 + dir.y();
            double pz = player.getZ() + dir.z();

            // Spawn tier-specific trail particles
            player.level().addParticle(getTrailParticle(tier), px, py, pz, 0, 0, 0);
        }
    }

    private static net.minecraft.core.particles.ParticleOptions getTrailParticle(WeaponTierData tier) {
        return switch (tier) {
            case STARTER -> ParticleTypes.CRIT;
            case BASE -> ParticleTypes.HAPPY_VILLAGER;
            case TIER_1 -> ParticleTypes.INSTANT_EFFECT;
            case HYBRID_1 -> ParticleTypes.GLOW;
            case SPECIALIZATION -> ParticleTypes.ELECTRIC_SPARK;
            case HEAVY_HYBRID -> ParticleTypes.WITCH;
            case HISTORICAL -> ParticleTypes.SOUL;
            case MYTHIC -> ParticleTypes.SOUL_FIRE_FLAME;
            case GODLY -> ParticleTypes.END_ROD;
        };
    }
}
