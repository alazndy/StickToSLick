package com.sticktoslick.client;

import com.sticktoslick.StickToSlick;
import com.sticktoslick.data.WeaponLevelConfig;
import com.sticktoslick.data.WeaponNBTHelper;
import com.sticktoslick.data.WeaponTierData;
import com.sticktoslick.item.StarterStickItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.sticktoslick.client.vfx.HitFeedbackRenderer;

/**
 * Renders a sleek HUD overlay showing weapon level, XP, and tier
 * when the player is holding a Stick to Slick weapon.
 */
@Mod.EventBusSubscriber(modid = StickToSlick.MODID, value = Dist.CLIENT)
public class WeaponHUDRenderer {

    private static final int BAR_WIDTH = 100;
    private static final int BAR_HEIGHT = 4;
    private static final int BG_COLOR = 0xAA000000; // Semi-transparent black

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        // Only render on a specific overlay (e.g., Hotbar) to avoid double rendering
        if (event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player == null)
                return;

            ItemStack stack = player.getMainHandItem();
            if (!(stack.getItem() instanceof StarterStickItem))
                return;

            if (WeaponNBTHelper.hasWeaponData(stack)) {
                renderHUD(event.getGuiGraphics(), stack, event.getWindow().getGuiScaledWidth(),
                        event.getWindow().getGuiScaledHeight());
            }
        }

        // --- Hit Flash Vignette (Subtle) ---
        float flash = HitFeedbackRenderer.getFlashIntensity();
        if (flash > 0.01f && com.sticktoslick.config.ModClientConfig.ENABLE_VIGNETTE.get()) {
            int alpha = (int) (flash * 100); // Super subtle (max ~40% opacity)
            int color = (alpha << 24) | 0xFF0000;
            int w = event.getWindow().getGuiScaledWidth();
            int h = event.getWindow().getGuiScaledHeight();
            int thickness = 20; // Thinner border

            // Draw a subtle vignette (4 rectangles at edges)
            event.getGuiGraphics().fillGradient(0, 0, w, thickness, color, 0x00FF0000); // Top
            event.getGuiGraphics().fillGradient(0, h - thickness, w, h, 0x00FF0000, color); // Bottom
            event.getGuiGraphics().fillGradient(0, 0, thickness, h, color, 0x00FF0000); // Left (approximate)
            event.getGuiGraphics().fillGradient(w - thickness, 0, w, h, 0x00FF0000, color); // Right (approximate)
        }
    }

    private static void renderHUD(GuiGraphics graphics, ItemStack stack, int screenWidth, int screenHeight) {
        int weaponLevel = WeaponNBTHelper.getLevel(stack);
        int xp = WeaponNBTHelper.getXP(stack);
        WeaponTierData tier = WeaponTierData.getFromLevel(weaponLevel);

        int x = screenWidth / 2 - BAR_WIDTH / 2;
        int y = screenHeight - 50; // Above the hotbar

        // 1. Draw Tier & Level Text
        String title = tier.name + " [Lv." + weaponLevel + "]";
        graphics.drawString(Minecraft.getInstance().font, title,
                screenWidth / 2 - Minecraft.getInstance().font.width(title) / 2,
                y - 12, tier.color, true);

        // 2. XP Bar Background
        graphics.fill(x - 1, y - 1, x + BAR_WIDTH + 1, y + BAR_HEIGHT + 1, 0xFF000000); // Border
        graphics.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, BG_COLOR); // Fill BG

        // 3. XP Bar Fill
        if (weaponLevel < 30) {
            int nextXP = WeaponLevelConfig.getXPForNextLevel(weaponLevel);
            float progress = Math.min(1.0f, (float) xp / nextXP);
            int fillWidth = (int) (progress * BAR_WIDTH);

            // Draw gradient-like bar
            graphics.fill(x, y, x + fillWidth, y + BAR_HEIGHT, tier.color | 0xFF000000);

            // Add a "shining" highlight
            if (fillWidth > 2) {
                graphics.fill(x, y, x + fillWidth, y + 1, 0x44FFFFFF);
            }
        } else {
            // Max Level Bar (Rainbow or Gold)
            graphics.fill(x, y, x + BAR_WIDTH, y + BAR_HEIGHT, 0xFFFFCC00); // Gold
        }

        // 4. Broken Warning
        if (StarterStickItem.isBroken(stack)) {
            String warning = "⚠ TAMİR GEREKLİ ⚠";
            graphics.drawString(Minecraft.getInstance().font, warning,
                    screenWidth / 2 - Minecraft.getInstance().font.width(warning) / 2,
                    y + 8, 0xFFFF0000, true);
        }
    }
}
