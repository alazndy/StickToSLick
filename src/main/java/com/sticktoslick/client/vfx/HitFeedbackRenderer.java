package com.sticktoslick.client.vfx;

import com.sticktoslick.StickToSlick;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles visual "Hit Feel" on the client side.
 * Includes camera shake, FOV pulsing, and color flashes.
 */
@Mod.EventBusSubscriber(modid = StickToSlick.MODID, value = Dist.CLIENT)
public class HitFeedbackRenderer {

    private static float shakeIntensity = 0f;
    private static float shakeTime = 0f;
    private static float fovPulse = 0f;
    private static float flashIntensity = 0f;
    private static float recoilOffset = 0f;

    public static void handleHit(float damage, boolean isCrit) {
        float weight = getWeaponWeightFactor();

        // Power calculation (allows heavier weapons to scale better)
        float power = Math.max(0.5f, Math.min(damage / 10f, 2.0f));

        // Shake intensity: old max (w/ crit) was ~0.45.
        // New max (w/ crit + 1.8 weight) is ~0.52 (exactly ~15% higher).
        shakeIntensity = 0.0475f * power * weight;

        if (isCrit) {
            shakeIntensity *= 1.5f;
        }

        // Make heavy weapons shake LONGER, and light weapons shake SHORTER
        shakeTime = 4f + (weight * 4f);

        // FOV Pulse and Recoil MUST scale with weight! This is why they felt the same
        // before.
        fovPulse = -0.75f * power * weight;
        flashIntensity = isCrit ? 0.12f : 0.05f;
        recoilOffset = 0.04f * power * weight;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        if (shakeTime > 0) {
            shakeTime--;
            shakeIntensity *= 0.85f;
        } else {
            shakeIntensity = 0;
        }

        fovPulse *= 0.7f;
        flashIntensity *= 0.9f;
        recoilOffset *= 0.75f;
    }

    @SubscribeEvent
    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (shakeIntensity > 0 && com.sticktoslick.config.ModClientConfig.ENABLE_SCREENSHAKE.get()) {
            float time = (float) (System.currentTimeMillis() % 1000) / 1000f;
            float offX = (Mth.sin(time * 50f) * shakeIntensity);
            float offY = (Mth.cos(time * 60f) * shakeIntensity);
            float offZ = (Mth.sin(time * 70f) * shakeIntensity);

            event.setPitch(event.getPitch() + offX * 10f);
            event.setYaw(event.getYaw() + offY * 10f);
            event.setRoll(event.getRoll() + offZ * 15f);
        }
    }

    @SubscribeEvent
    public static void onComputeFov(ViewportEvent.ComputeFov event) {
        if (Math.abs(fovPulse) > 0.01f) {
            event.setFOV(event.getFOV() + fovPulse);
        }
    }

    // Note: A full-screen red flash would require a custom overlay in
    // RenderGuiOverlayEvent
    public static float getFlashIntensity() {
        return flashIntensity;
    }

    public static float getRecoilOffset() {
        return recoilOffset;
    }

    private static float getWeaponWeightFactor() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        net.minecraft.client.player.LocalPlayer player = mc.player;
        if (player == null)
            return 1.0f;

        net.minecraft.world.item.ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof com.sticktoslick.item.StarterStickItem
                && com.sticktoslick.data.WeaponNBTHelper.hasWeaponData(stack)) {
            String wClass = com.sticktoslick.data.WeaponNBTHelper.getWeaponClass(stack);
            com.sticktoslick.data.WeaponAnimationData.AnimInfo info = com.sticktoslick.data.WeaponAnimationData
                    .get(wClass);

            if (info != null) {
                return switch (info.category()) {
                    case GREATSWORD, HAMMER -> 1.8f; // Heavy, massive impact
                    case AXE, HALBERD -> 1.4f; // Strong impact
                    case SWORD, KATANA -> 1.0f; // Medium impact
                    case SPEAR -> 0.9f;
                    case STICK -> 0.7f;
                    case DAGGER -> 0.5f; // Very light impact
                };
            }
        }
        return 1.0f;
    }
}
