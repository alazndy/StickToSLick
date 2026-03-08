package com.sticktoslick;

import com.mojang.logging.LogUtils;
import com.sticktoslick.event.*;
import com.sticktoslick.item.ModItems;
import com.sticktoslick.network.ModMessages;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import com.sticktoslick.config.ModClientConfig;
import org.slf4j.Logger;

@Mod(StickToSlick.MODID)
public class StickToSlick {
    public static final String MODID = "sticktoslick";
    private static final Logger LOGGER = LogUtils.getLogger();

    public StickToSlick(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        // Register configs
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ModClientConfig.SPEC);

        // Register deferred registries
        ModItems.register(modEventBus);
        com.sticktoslick.data.ModRecipes.register(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register Mod Packets
        ModMessages.register();

        // Register game event handlers
        MinecraftForge.EVENT_BUS.register(new CombatEventHandler());
        MinecraftForge.EVENT_BUS.register(new ItemInteractionHandler());
        MinecraftForge.EVENT_BUS.register(new AnvilRepairHandler());
        MinecraftForge.EVENT_BUS.register(new ProtectionHandler());
        MinecraftForge.EVENT_BUS.register(new EnchantmentCapacityHandler());
        MinecraftForge.EVENT_BUS.register(new TraitEffectHandler());
        MinecraftForge.EVENT_BUS.register(new DeathPenaltyHandler());

        LOGGER.info("Stick to Slick initialized. Your journey begins with a stick.");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("[StickToSlick] Common setup complete.");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("[StickToSlick] Client setup complete.");
        }
    }
}
