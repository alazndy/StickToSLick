package com.sticktoslick.client.vfx;

import com.sticktoslick.StickToSlick;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = StickToSlick.MODID, value = Dist.CLIENT)
public class LevelUpVfxRenderer {

    private static int timer = 0;
    private static int maxTimer = 60; // 3 seconds at 20 ticks
    private static int displayLevel = 0;
    private static int color = 0xFFFFFF;

    public static void trigger(int level, int tierColor) {
        timer = maxTimer;
        displayLevel = level;
        color = tierColor;

        // Spawn particles immediately
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player != null) {
            net.minecraft.world.level.Level world = player.level();
            for (int i = 0; i < 20; i++) {
                world.addParticle(ParticleTypes.TOTEM_OF_UNDYING,
                        player.getX() + (world.random.nextDouble() - 0.5) * 2,
                        player.getY() + 1.0 + (world.random.nextDouble() - 0.5) * 2,
                        player.getZ() + (world.random.nextDouble() - 0.5) * 2,
                        0, 0.1, 0);
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && timer > 0) {
            timer--;
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        if (timer <= 0)
            return;

        Minecraft mc = Minecraft.getInstance();
        GuiGraphics graphics = event.getGuiGraphics();
        int width = event.getWindow().getGuiScaledWidth();
        int height = event.getWindow().getGuiScaledHeight();

        float alpha = Math.min(1.0f, (float) timer / 20.0f);
        if (timer > (maxTimer - 10)) {
            alpha = (maxTimer - timer) / 10.0f; // Fade in
        }

        // 1. Draw Vignette Flash
        int overlayColor = (Math.round(alpha * 0.3f * 255) << 24) | (color & 0xFFFFFF);
        renderVignette(graphics, width, height, overlayColor);

        // 2. Draw Text
        String text = "SEVİYE ATLADI!";
        String subtext = "Lvl " + displayLevel;

        int yOff = (int) (Mth.sin((maxTimer - timer) * 0.2f) * 5); // Floating animation

        graphics.pose().pushPose();
        graphics.pose().translate(width / 2.0f, height / 3.0f + yOff, 0);
        graphics.pose().scale(2.0f, 2.0f, 2.0f);

        graphics.drawCenteredString(mc.font, text, 0, 0, color | (Math.round(alpha * 255) << 24));

        graphics.pose().scale(0.5f, 0.5f, 0.5f);
        graphics.drawCenteredString(mc.font, subtext, 0, 25, 0xFFFFFF | (Math.round(alpha * 255) << 24));

        graphics.pose().popPose();
    }

    private static void renderVignette(GuiGraphics graphics, int width, int height, int color) {
        // Simple translucent fill for now as a "flash"
        // In a real premium mod we'd use a custom shader or texture, but fill is safe.
        graphics.fill(0, 0, width, 10, color); // Top
        graphics.fill(0, height - 10, width, height, color); // Bottom
        graphics.fill(0, 10, 10, height - 10, color); // Left
        graphics.fill(width - 10, 10, width, height - 10, color); // Right
    }
}
