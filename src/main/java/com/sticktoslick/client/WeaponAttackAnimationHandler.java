package com.sticktoslick.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.sticktoslick.StickToSlick;
import com.sticktoslick.data.WeaponAnimationData;
import com.sticktoslick.data.WeaponAnimationData.AnimCategory;
import com.sticktoslick.data.WeaponNBTHelper;
import com.sticktoslick.item.StarterStickItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = StickToSlick.MODID, value = Dist.CLIENT)
public class WeaponAttackAnimationHandler {

    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();
    private static Boolean betterCombatLoaded = null;
    private static float lastSwingProgress = 0f;
    private static int comboStep = 0;
    private static long lastAttackTime = 0;

    private static boolean isBetterCombatLoaded() {
        if (betterCombatLoaded == null) {
            betterCombatLoaded = ModList.get().isLoaded("bettercombat");
        }
        return betterCombatLoaded;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderHand(RenderHandEvent event) {
        if (isBetterCombatLoaded())
            return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null)
            return;

        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof StarterStickItem))
            return;
        if (!WeaponNBTHelper.hasWeaponData(stack))
            return;
        if (event.getHand() != InteractionHand.MAIN_HAND)
            return;

        String weaponClass = WeaponNBTHelper.getWeaponClass(stack);
        WeaponAnimationData.AnimInfo animInfo = WeaponAnimationData.get(weaponClass);

        float swingProgress = event.getSwingProgress();
        PoseStack poseStack = event.getPoseStack();

        // 1. Duruş (Idle Pose)
        applyIdlePose(poseStack, animInfo.category());

        // Detect combo/swing reset
        long now = System.currentTimeMillis();
        if (now - lastAttackTime > 1200) { // 1.2 seconds combo window
            comboStep = 0;
        }

        // Detect swing START
        if (swingProgress > 0 && lastSwingProgress == 0) {
            WeaponParticleHandler.spawnSlashTrail(player, stack, animInfo.category(), comboStep);
            lastAttackTime = now;
        }

        // Advance combo step at the VERY END of the swing
        if (swingProgress == 0 && lastSwingProgress > 0) {
            comboStep = (comboStep + 1) % 3;
        }

        lastSwingProgress = swingProgress;

        // 3. Recoil (Impact Feedback)
        float recoil = com.sticktoslick.client.vfx.HitFeedbackRenderer.getRecoilOffset();
        if (recoil > 0.001f) {
            poseStack.translate(0, 0, recoil);
            poseStack.mulPose(Axis.XP.rotationDegrees(recoil * 100f));
        }

        // 2. Vuruş (Swing Animation)
        if (swingProgress > 0.0f) {
            applySwingAnimation(poseStack, animInfo.category(), swingProgress);
        }
    }

    private static void applyIdlePose(PoseStack pose, AnimCategory category) {
        switch (category) {
            case STICK -> {
                // Higher and more centered
                pose.translate(0.04, 0.05, -0.1);
                pose.mulPose(Axis.ZP.rotationDegrees(-15f));
            }
            case DAGGER -> {
                // Lifted up significantly
                pose.translate(0.1, 0.15, -0.2);
                pose.mulPose(Axis.XP.rotationDegrees(-20f));
                pose.mulPose(Axis.ZP.rotationDegrees(15f));
            }
            case SWORD, KATANA -> {
                // More prominent guard position
                pose.translate(-0.02, 0.1, -0.15);
                pose.mulPose(Axis.ZP.rotationDegrees(-12f));
                pose.mulPose(Axis.XP.rotationDegrees(-5f));
                pose.mulPose(Axis.YP.rotationDegrees(8f));
            }
            case GREATSWORD, HAMMER -> {
                // Heavy weapons: balanced visibility
                pose.translate(-0.08, 0.0, -0.1);
                pose.mulPose(Axis.ZP.rotationDegrees(-18f));
                pose.mulPose(Axis.XP.rotationDegrees(10f));
            }
            case AXE -> {
                // Raised axe
                pose.translate(0.02, 0.1, -0.1);
                pose.mulPose(Axis.ZP.rotationDegrees(-15f));
                pose.mulPose(Axis.XP.rotationDegrees(-15f));
            }
            case SPEAR, HALBERD -> {
                // Long weapons: adjusted for better FOV
                pose.translate(0.0, 0.2, -0.35);
                pose.mulPose(Axis.XP.rotationDegrees(-35f));
                pose.mulPose(Axis.YP.rotationDegrees(5f));
            }
        }
    }

    private static void applySwingAnimation(PoseStack pose, AnimCategory category, float swing) {
        // Hızlı vuruş eğrisi (Snappier easing)
        float peak = Mth.sin(swing * (float) Math.PI);
        // Ease out curve: starts fast, ends slow
        float power = 1.0f - (float) Math.pow(1.0f - swing, 3);
        float snapPeak = Mth.sin((float) Math.pow(swing, 0.5f) * (float) Math.PI);

        switch (category) {
            case STICK -> {
                if (comboStep == 0) {
                    // Right slash
                    pose.translate(-peak * 0.4, 0, -peak * 0.4);
                    pose.mulPose(Axis.YP.rotationDegrees(peak * 40f));
                    pose.mulPose(Axis.XP.rotationDegrees(peak * -20f));
                } else if (comboStep == 1) {
                    // Left slash
                    pose.translate(peak * 0.4, 0, -peak * 0.4);
                    pose.mulPose(Axis.YP.rotationDegrees(peak * -40f));
                    pose.mulPose(Axis.XP.rotationDegrees(peak * -20f));
                } else {
                    // Overhead bash
                    pose.translate(0, -peak * 0.3, -peak * 0.5);
                    pose.mulPose(Axis.XP.rotationDegrees(peak * -60f));
                }
            }
            case DAGGER -> {
                if (comboStep == 0) {
                    // Fast stab
                    pose.translate(0, -snapPeak * 0.1, -power * 0.8);
                    pose.mulPose(Axis.ZP.rotationDegrees(power * 30f));
                    pose.mulPose(Axis.XP.rotationDegrees(snapPeak * -30f));
                } else if (comboStep == 1) {
                    // High reverse slash
                    pose.translate(-power * 0.3, -snapPeak * 0.2, -power * 0.5);
                    pose.mulPose(Axis.YP.rotationDegrees(power * 60f));
                    pose.mulPose(Axis.XP.rotationDegrees(snapPeak * -20f));
                } else {
                    // Double flurry
                    float doublePeak = Mth.sin(swing * 2 * (float) Math.PI); // Two pulses
                    pose.translate(doublePeak * 0.2, -0.1, -power * 0.8);
                    pose.mulPose(Axis.ZP.rotationDegrees(45f));
                    pose.mulPose(Axis.XP.rotationDegrees(doublePeak * -40f));
                }
            }
            case SWORD -> {
                if (comboStep == 0) {
                    // Horizontal Right -> Left
                    pose.translate(-peak * 0.7, -peak * 0.2, -peak * 0.2);
                    pose.mulPose(Axis.YP.rotationDegrees(peak * 80f));
                    pose.mulPose(Axis.ZP.rotationDegrees(power * -50f));
                    pose.mulPose(Axis.XP.rotationDegrees(peak * -30f));
                } else if (comboStep == 1) {
                    // Horizontal Left -> Right
                    pose.translate(peak * 0.7, -peak * 0.2, -peak * 0.2);
                    pose.mulPose(Axis.YP.rotationDegrees(peak * -80f));
                    pose.mulPose(Axis.ZP.rotationDegrees(power * 50f));
                    pose.mulPose(Axis.XP.rotationDegrees(peak * -30f));
                } else {
                    // Downward chop
                    pose.translate(0, -peak * 0.4, -peak * 0.4);
                    pose.mulPose(Axis.YP.rotationDegrees(10f));
                    pose.mulPose(Axis.XP.rotationDegrees(peak * -80f));
                    pose.mulPose(Axis.ZP.rotationDegrees(-20f));
                }
            }
            case KATANA -> {
                if (comboStep == 0) {
                    // Fast diagonal down right
                    pose.translate(-power * 0.5, -snapPeak * 0.4, -snapPeak * 0.3);
                    pose.mulPose(Axis.YP.rotationDegrees(power * 60f));
                    pose.mulPose(Axis.ZP.rotationDegrees(snapPeak * -80f));
                    pose.mulPose(Axis.XP.rotationDegrees(power * -40f));
                } else if (comboStep == 1) {
                    // Fast diagonal up left
                    pose.translate(power * 0.4, snapPeak * 0.3, -snapPeak * 0.3);
                    pose.mulPose(Axis.YP.rotationDegrees(power * -60f));
                    pose.mulPose(Axis.ZP.rotationDegrees(snapPeak * 70f));
                    pose.mulPose(Axis.XP.rotationDegrees(power * -20f));
                } else {
                    // Lightning horizontal cut
                    pose.translate(0, -0.2, -power * 0.6);
                    pose.mulPose(Axis.ZP.rotationDegrees(-90f));
                    pose.mulPose(Axis.YP.rotationDegrees(snapPeak * 120f));
                }
            }
            case GREATSWORD -> {
                if (comboStep == 0) {
                    // Slow, massive arc right
                    pose.translate(-power * 0.8, -peak * 0.3, -peak * 0.4);
                    pose.mulPose(Axis.YP.rotationDegrees(power * 90f));
                    pose.mulPose(Axis.ZP.rotationDegrees(peak * -60f));
                    pose.mulPose(Axis.XP.rotationDegrees(peak * -40f));
                    pose.mulPose(Axis.ZN.rotationDegrees(peak * 15f));
                } else if (comboStep == 1) {
                    // Heavy backswing left
                    pose.translate(power * 0.8, -peak * 0.2, -peak * 0.4);
                    pose.mulPose(Axis.YP.rotationDegrees(power * -90f));
                    pose.mulPose(Axis.ZP.rotationDegrees(peak * 60f));
                    pose.mulPose(Axis.XP.rotationDegrees(peak * -40f));
                } else {
                    // Heavy overhead execution
                    float raise = Math.max(0, 1.0f - power * 2.5f);
                    pose.translate(0, -peak * 0.7 + raise * 0.4, -peak * 0.5);
                    pose.mulPose(Axis.XP.rotationDegrees(peak * -110f));
                }
            }
            case AXE, HAMMER -> {
                if (comboStep == 0) {
                    // Standard overhead chop
                    float raise = Math.max(0, 1.0f - power * 3.0f);
                    pose.translate(0, -peak * 0.6 + raise * 0.2, -peak * 0.3);
                    pose.mulPose(Axis.XP.rotationDegrees(peak * -100f));
                    pose.mulPose(Axis.ZP.rotationDegrees(power * -20f));
                } else if (comboStep == 1) {
                    // Horizontal bash
                    pose.translate(-peak * 0.6, -0.1, -power * 0.4);
                    pose.mulPose(Axis.YP.rotationDegrees(peak * 70f));
                    pose.mulPose(Axis.XP.rotationDegrees(peak * -20f));
                    pose.mulPose(Axis.ZP.rotationDegrees(-30f));
                } else {
                    // Brutal rising strike (Golf swing)
                    pose.translate(peak * 0.4, peak * 0.5, -power * 0.4);
                    pose.mulPose(Axis.YP.rotationDegrees(power * -40f));
                    pose.mulPose(Axis.XP.rotationDegrees(peak * 80f)); // Upward
                    pose.mulPose(Axis.ZP.rotationDegrees(power * 50f));
                }
            }
            case SPEAR, HALBERD -> {
                if (comboStep == 0) {
                    // Primary thrust
                    pose.translate(0, -peak * 0.2, -power * 0.9);
                    pose.mulPose(Axis.YP.rotationDegrees(peak * 10f));
                    pose.mulPose(Axis.XP.rotationDegrees(peak * -10f));
                } else if (comboStep == 1) {
                    // High thrust
                    pose.translate(0, peak * 0.2, -power * 0.9);
                    pose.mulPose(Axis.XP.rotationDegrees(peak * -25f));
                } else {
                    // Wide sweep
                    pose.translate(-peak * 0.6, 0, -peak * 0.3);
                    pose.mulPose(Axis.YP.rotationDegrees(peak * 80f));
                    pose.mulPose(Axis.ZP.rotationDegrees(power * -40f));
                }
            }
        }
    }
}
