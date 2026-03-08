package com.sticktoslick.event;

import com.sticktoslick.data.RepairMaterialMap;
import com.sticktoslick.data.WeaponClassData;
import com.sticktoslick.data.WeaponNBTHelper;
import com.sticktoslick.item.StarterStickItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Handles custom Anvil repair rules:
 * - Only the correct evolution material can repair a weapon
 * - Repair amount scales with the material used
 */
public class AnvilRepairHandler {

    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST)
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft(); // The weapon
        ItemStack right = event.getRight(); // The repair material

        if (!(left.getItem() instanceof StarterStickItem))
            return;
        if (!WeaponNBTHelper.hasWeaponData(left))
            return;
        if (right.isEmpty())
            return;

        String weaponClass = WeaponNBTHelper.getWeaponClass(left);
        Item requiredMaterial = RepairMaterialMap.getRepairMaterial(weaponClass);

        if (requiredMaterial == null)
            return;

        // If the right item IS the correct repair material
        if (right.is(requiredMaterial)) {
            ItemStack result = left.copy();

            // Each material restores 25% of max durability
            int maxDurability = WeaponClassData.get(weaponClass).maxDurability();
            int repairAmount = Math.max(1, maxDurability / 4) * right.getCount();
            int currentDamage = result.getDamageValue();
            int newDamage = Math.max(0, currentDamage - repairAmount);

            result.setDamageValue(newDamage);

            event.setOutput(result);
            event.setCost(Math.max(1, right.getCount())); // XP cost = number of materials used
            event.setMaterialCost(right.getCount());
        } else {
            // Ignore if the secondary item is an enchanted book or nether star,
            // so EnchantmentCapacityHandler can process it
            if (right.is(net.minecraft.world.item.Items.ENCHANTED_BOOK)
                    || right.is(net.minecraft.world.item.Items.NETHER_STAR)) {
                return;
            }

            // If the right item is NOT a Stick to Slick weapon (i.e., trying to repair with
            // wrong material)
            if (!(right.getItem() instanceof StarterStickItem)) {
                // Block the repair — don't set any output so vanilla behavior is cancelled
                event.setCanceled(true);
            }
        }
    }
}
