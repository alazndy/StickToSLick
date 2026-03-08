package com.sticktoslick.data;

import com.sticktoslick.StickToSlick;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister
            .create(ForgeRegistries.RECIPE_SERIALIZERS, StickToSlick.MODID);

    public static final RegistryObject<RecipeSerializer<ManualRepairRecipe>> MANUAL_REPAIR_SERIALIZER = SERIALIZERS
            .register("manual_repair", () -> new SimpleCraftingRecipeSerializer<>(ManualRepairRecipe::new));

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
