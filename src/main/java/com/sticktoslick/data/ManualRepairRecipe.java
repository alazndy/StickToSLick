package com.sticktoslick.data;

import com.sticktoslick.item.StarterStickItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * A custom shapeless recipe: Starter Stick + Stick = +20 Durability.
 * Only works if the weapon is in the "starter" class.
 */
public class ManualRepairRecipe extends CustomRecipe {
    public ManualRepairRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        boolean foundWeapon = false;
        boolean foundStick = false;
        int count = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty())
                continue;
            count++;

            if (stack.getItem() instanceof StarterStickItem) {
                // Only allow repairing the base "starter" class manually
                if (!"starter".equals(WeaponNBTHelper.getWeaponClass(stack)))
                    return false;
                if (foundWeapon)
                    return false;
                foundWeapon = true;
            } else if (stack.is(Items.STICK)) {
                if (foundStick)
                    return false;
                foundStick = true;
            } else {
                return false;
            }
        }

        return foundWeapon && foundStick && count == 2;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack weapon = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.getItem() instanceof StarterStickItem) {
                weapon = stack.copy();
                break;
            }
        }

        if (!weapon.isEmpty()) {
            int currentDamage = weapon.getDamageValue();
            // Restore 20 durability (reduce damage by 20)
            weapon.setDamageValue(Math.max(0, currentDamage - 20));
            return weapon;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.MANUAL_REPAIR_SERIALIZER.get();
    }
}
