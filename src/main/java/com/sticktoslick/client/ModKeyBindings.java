package com.sticktoslick.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.sticktoslick.StickToSlick;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = StickToSlick.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModKeyBindings {
    public static final KeyMapping PONDER_KEY = new KeyMapping(
            "key.sticktoslick.ponder", // Translation key
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H, // Default: H
            "key.categories.sticktoslick" // Category
    );

    public static final KeyMapping DODGE_KEY = new KeyMapping(
            "key.sticktoslick.dodge", // Translation key
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_ALT, // Default: Left Alt
            "key.categories.sticktoslick" // Category
    );

    public static final KeyMapping SETTINGS_KEY = new KeyMapping(
            "key.sticktoslick.settings", // Translation key
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_O, // Default: O
            "key.categories.sticktoslick" // Category
    );

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(PONDER_KEY);
        event.register(DODGE_KEY);
        event.register(SETTINGS_KEY);
    }
}
