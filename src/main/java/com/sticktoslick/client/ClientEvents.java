package com.sticktoslick.client;

import com.sticktoslick.StickToSlick;
import com.sticktoslick.client.gui.WeaponUpgradeScreen;
import com.sticktoslick.data.WeaponNBTHelper;
import com.sticktoslick.item.StarterStickItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

/**
 * Client-side Ponder logic: Hold the bound key for 3 seconds while hovering
 * over a StarterStickItem in any inventory screen to open the upgrade GUI.
 * Uses raw GLFW input to avoid conflicts with Minecraft's keybind system.
 */
@Mod.EventBusSubscriber(modid = StickToSlick.MODID, value = Dist.CLIENT)
public class ClientEvents {

    private static int holdTicks = 0;
    private static final int REQUIRED_TICKS = 60; // 3 seconds at 20 tps
    private static ItemStack hoveredWeapon = ItemStack.EMPTY;

    @SubscribeEvent
    public static void onScreenRenderPost(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen))
            return;

        Minecraft mc = Minecraft.getInstance();

        // Use raw GLFW input to detect the key — avoids conflicts with chat/keybind
        // system
        long windowHandle = mc.getWindow().getWindow();
        int boundKey = ModKeyBindings.PONDER_KEY.getKey().getValue();
        boolean keyDown = GLFW.glfwGetKey(windowHandle, boundKey) == GLFW.GLFW_PRESS;

        // Find the slot under the cursor
        Slot hoveredSlot = screen.getSlotUnderMouse();
        ItemStack hovered = (hoveredSlot != null) ? hoveredSlot.getItem() : ItemStack.EMPTY;

        boolean isWeapon = !hovered.isEmpty()
                && hovered.getItem() instanceof StarterStickItem
                && WeaponNBTHelper.hasWeaponData(hovered);

        if (keyDown && isWeapon) {
            hoveredWeapon = hovered;
            holdTicks++;

            // Draw progress bar near cursor
            GuiGraphics graphics = event.getGuiGraphics();
            int mouseX = event.getMouseX();
            int mouseY = event.getMouseY();

            float progress = Math.min((float) holdTicks / REQUIRED_TICKS, 1.0f);
            int barWidth = 40;
            int barHeight = 4;
            int barX = mouseX - barWidth / 2;
            int barY = mouseY + 16;

            // Background bar (dark gray)
            graphics.fill(barX - 1, barY - 1, barX + barWidth + 1, barY + barHeight + 1, 0xAA000000);
            // Progress bar (aqua -> gold gradient)
            int barColor = progress < 1.0f ? 0xFF55FFFF : 0xFFFFAA00;
            graphics.fill(barX, barY, barX + (int) (barWidth * progress), barY + barHeight, barColor);

            // Label
            String label = progress < 1.0f ? "Basılı tut..." : "Açılıyor!";
            graphics.drawString(mc.font, label, barX, barY - 10, 0xFFFFFF, true);

            if (holdTicks >= REQUIRED_TICKS) {
                holdTicks = 0;
                mc.setScreen(new WeaponUpgradeScreen(hoveredWeapon));
            }
        } else {
            holdTicks = 0;
            hoveredWeapon = ItemStack.EMPTY;
        }
    }

    @SubscribeEvent
    public static void onClientTick(net.minecraftforge.event.TickEvent.ClientTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END)
            return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null)
            return;

        while (ModKeyBindings.SETTINGS_KEY.consumeClick()) {
            mc.setScreen(new com.sticktoslick.client.gui.SettingsScreen(mc.screen));
        }

        while (ModKeyBindings.PONDER_KEY.consumeClick()) {
            ItemStack mainHand = mc.player.getMainHandItem();
            if (mainHand.getItem() instanceof StarterStickItem && WeaponNBTHelper.hasWeaponData(mainHand)) {
                mc.setScreen(new WeaponUpgradeScreen(mainHand));
            }
        }

        while (ModKeyBindings.DODGE_KEY.consumeClick()) {
            ItemStack mainHand = mc.player.getMainHandItem();
            if (mainHand.getItem() instanceof StarterStickItem && WeaponNBTHelper.hasWeaponData(mainHand)) {

                if (mc.player.getCooldowns().isOnCooldown(mainHand.getItem())) {
                    return;
                }

                String weaponClass = com.sticktoslick.data.WeaponNBTHelper.getWeaponClass(mainHand);
                float baseSpeed = com.sticktoslick.data.WeaponClassData.get(weaponClass).baseAttackSpeed();

                if (baseSpeed >= 1.4f) {
                    if (mc.player.getFoodData().getFoodLevel() > 6 || mc.player.getAbilities().instabuild) {
                        net.minecraft.world.phys.Vec3 look = mc.player.getLookAngle();
                        net.minecraft.world.phys.Vec3 dash = new net.minecraft.world.phys.Vec3(look.x, 0, look.z)
                                .normalize().scale(1.2);
                        mc.player.setDeltaMovement(mc.player.getDeltaMovement().add(dash.x, 0.3, dash.z));

                        com.sticktoslick.network.ModMessages
                                .sendToServer(new com.sticktoslick.network.C2SDodgePacket());
                    } else {
                        mc.player.displayClientMessage(net.minecraft.network.chat.Component
                                .literal("Kaçınmak için çok yorgunsun!").withStyle(net.minecraft.ChatFormatting.RED),
                                true);
                    }
                } else {
                    mc.player.displayClientMessage(net.minecraft.network.chat.Component
                            .literal("Bu silah kaçınmak için çok ağır!").withStyle(net.minecraft.ChatFormatting.RED),
                            true);
                }
            }
        }
    }
}
