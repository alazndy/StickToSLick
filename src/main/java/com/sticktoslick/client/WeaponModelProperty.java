package com.sticktoslick.client;

import com.sticktoslick.StickToSlick;
import com.sticktoslick.data.WeaponNBTHelper;
import com.sticktoslick.item.ModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;

/**
 * Registers an item model property "weapon_class" that returns a float
 * based on the weapon's NBT class, allowing model overrides to switch textures.
 */
@Mod.EventBusSubscriber(modid = StickToSlick.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WeaponModelProperty {

    // Map weapon class string -> float ID for model overrides
    public static final Map<String, Float> CLASS_TO_FLOAT = Map.ofEntries(
            Map.entry("weapon_wooden_stick", 0.00f),
            Map.entry("weapon_dagger", 0.01f),
            Map.entry("weapon_shortsword", 0.02f),
            Map.entry("weapon_spear", 0.03f),
            Map.entry("weapon_club", 0.04f),
            Map.entry("weapon_dirk", 0.05f),
            Map.entry("weapon_arming_sword", 0.06f),
            Map.entry("weapon_trident", 0.07f),
            Map.entry("weapon_mace", 0.08f),
            Map.entry("weapon_saber", 0.09f),
            Map.entry("weapon_longsword", 0.10f),
            Map.entry("weapon_lucerne_hammer", 0.11f),
            Map.entry("weapon_morning_star", 0.12f),
            Map.entry("weapon_katana", 0.13f),
            Map.entry("weapon_bastard_sword", 0.14f),
            Map.entry("weapon_halberd", 0.15f),
            Map.entry("weapon_warhammer", 0.16f),
            Map.entry("weapon_nodachi", 0.17f),
            Map.entry("weapon_claymore", 0.18f),
            Map.entry("weapon_partisan", 0.19f),
            Map.entry("weapon_great_maul", 0.20f),
            Map.entry("weapon_zweihander", 0.21f),
            Map.entry("weapon_winged_lance", 0.22f),
            Map.entry("weapon_executioners_axe", 0.23f),
            Map.entry("weapon_dragon_slayer", 0.24f),
            Map.entry("weapon_gungnir", 0.25f),
            Map.entry("weapon_void_crusher", 0.26f),
            Map.entry("weapon_genesis", 0.27f),
            Map.entry("weapon_longinus", 0.28f),
            Map.entry("weapon_atlas", 0.29f));

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                    ModItems.WEAPON_WOODEN_STICK.get(),
                    new ResourceLocation(StickToSlick.MODID, "weapon_class"),
                    (stack, level, entity, seed) -> {
                        if (!WeaponNBTHelper.hasWeaponData(stack))
                            return 0.0f;
                        String weaponClass = WeaponNBTHelper.getWeaponClass(stack);
                        return CLASS_TO_FLOAT.getOrDefault(weaponClass, 0.0f);
                    });
        });
    }
}
