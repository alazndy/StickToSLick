package com.sticktoslick.client.gui;

import com.sticktoslick.config.ModClientConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;

public class SettingsScreen extends Screen {
    private final Screen lastScreen;

    public SettingsScreen(Screen lastScreen) {
        super(Component.literal("Stick to Slick: Ayarlar"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2;

        // Screenshake Toggle
        this.addRenderableWidget(CycleButton.onOffBuilder(ModClientConfig.ENABLE_SCREENSHAKE.get())
                .create(cx - 100, cy - 30, 200, 20, Component.literal("Ekran Titremesi"), (button, value) -> {
                    ModClientConfig.ENABLE_SCREENSHAKE.set(value);
                }));

        // Vignette Toggle
        this.addRenderableWidget(CycleButton.onOffBuilder(ModClientConfig.ENABLE_VIGNETTE.get())
                .create(cx - 100, cy, 200, 20, Component.literal("Kırmızı Ekran Parlaması"), (button, value) -> {
                    ModClientConfig.ENABLE_VIGNETTE.set(value);
                }));

        // Done Button
        this.addRenderableWidget(Button.builder(Component.literal("Tamam"), button -> {
            Minecraft.getInstance().setScreen(this.lastScreen);
        }).bounds(cx - 100, cy + 50, 200, 20).build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        super.render(g, mouseX, mouseY, partialTick);
        g.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 80, 0xFFFFFF);
    }
}
