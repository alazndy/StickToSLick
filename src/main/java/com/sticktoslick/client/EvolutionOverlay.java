package com.sticktoslick.client;

import com.sticktoslick.StickToSlick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Full-screen overlay that plays when a weapon evolves.
 * Shows a bright flash, text, and fades out over 3 seconds.
 */
@Mod.EventBusSubscriber(modid = StickToSlick.MODID, value = Dist.CLIENT)
public class EvolutionOverlay {

    private static boolean active = false;
    private static long startTime = 0;
    private static String evolvedName = "";
    private static final long DURATION_MS = 3000; // 3 seconds

    /**
     * Call this from the client side to trigger the overlay animation.
     */
    public static void trigger(String weaponName) {
        active = true;
        startTime = System.currentTimeMillis();
        evolvedName = weaponName;
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (!active)
            return;

        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > DURATION_MS) {
            active = false;
            return;
        }

        float progress = (float) elapsed / DURATION_MS;
        Minecraft mc = Minecraft.getInstance();
        GuiGraphics g = event.getGuiGraphics();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();

        // Phase 1 (0-0.3): Bright white flash fading in
        // Phase 2 (0.3-0.7): Text displayed with gold particles
        // Phase 3 (0.7-1.0): Fade out

        int alpha;
        if (progress < 0.3f) {
            // Flash in
            alpha = (int) (255 * (progress / 0.3f));
        } else if (progress < 0.7f) {
            // Full visibility
            alpha = 255;
        } else {
            // Fade out
            alpha = (int) (255 * (1.0f - (progress - 0.7f) / 0.3f));
        }
        alpha = Math.max(0, Math.min(255, alpha));

        // White flash background
        int flashAlpha = progress < 0.3f ? (int) (180 * (progress / 0.3f))
                : (int) (180 * Math.max(0, 1.0f - (progress - 0.2f) / 0.5f));
        flashAlpha = Math.max(0, Math.min(255, flashAlpha));
        g.fill(0, 0, w, h, (flashAlpha << 24) | 0xFFFFFF);

        // Evolution text
        if (progress > 0.15f) {
            int textAlpha = alpha;
            int titleColor = (textAlpha << 24) | 0xFFAA00;
            int nameColor = (textAlpha << 24) | 0x55FFFF;

            g.drawCenteredString(mc.font, "✦ EVRİMLEŞME ✦", w / 2, h / 2 - 20, titleColor);
            g.drawCenteredString(mc.font, evolvedName, w / 2, h / 2, nameColor);

            // Decorative lines
            int lineAlpha = (int) (textAlpha * 0.5f);
            int lineColor = (lineAlpha << 24) | 0xFFAA00;
            int lineW = (int) (100 * Math.min(1.0f, (progress - 0.15f) / 0.3f));
            g.fill(w / 2 - lineW, h / 2 + 12, w / 2 + lineW, h / 2 + 13, lineColor);
        }
    }
}
