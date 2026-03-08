package com.sticktoslick.event;

import com.sticktoslick.data.WeaponNBTHelper;
import com.sticktoslick.item.StarterStickItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import java.util.Map;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Enforces the enchantment capacity system:
 * - Each weapon class has a max number of enchantment slots
 * - Nether Star bonus capacity is added on top
 * - Anvil combining is blocked if it would exceed the limit
 */
public class EnchantmentCapacityHandler {

    /**
     * Intercepts anvil operations to block enchantment additions
     * that would exceed the weapon's enchantment capacity.
     */
    @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST)
    public void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (!(left.getItem() instanceof StarterStickItem))
            return;

        System.out.println("[Anvil Debug] Found StarterStickItem left input.");

        if (!WeaponNBTHelper.hasWeaponData(left)) {
            System.out.println("[Anvil Debug] StarterStickItem does NOT have WeaponData.");
            return;
        }

        // ──── Case 1: Slot Expansion with Nether Star ────
        if (right.is(net.minecraft.world.item.Items.NETHER_STAR)) {
            int extraSlots = WeaponNBTHelper.getExtraSlots(left);
            int cost = extraSlots + 1;

            if (right.getCount() >= cost) {
                ItemStack result = left.copy();
                WeaponNBTHelper.addExtraSlot(result);
                WeaponNBTHelper.addBonusEnchantCapacity(result, 1);

                event.setOutput(result);
                event.setCost(10 * (extraSlots + 1)); // XP cost increases
                event.setMaterialCost(cost);
                return;
            }
        }

        // ──── Case 2: Enchantment Quest with Enchanted Book ────
        if (right.is(net.minecraft.world.item.Items.ENCHANTED_BOOK)) {
            System.out.println("[Anvil Debug] Right item is ENCHANTED_BOOK.");

            if (WeaponNBTHelper.hasActiveQuest(left)) {
                System.out.println("[Anvil Debug] Left item already has an active quest! Canceling.");
                event.setCanceled(true);
                return;
            }

            Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(right);
            if (enchants.isEmpty()) {
                net.minecraft.nbt.ListTag listtag = net.minecraft.world.item.EnchantedBookItem
                        .getEnchantments(right);
                enchants = EnchantmentHelper.deserializeEnchantments(listtag);
            }

            if (enchants.isEmpty())
                return;

            // Take the first enchantment from the book
            Map.Entry<Enchantment, Integer> entry = enchants.entrySet().iterator().next();
            Enchantment enchantment = entry.getKey();
            int bookLevel = entry.getValue();
            String enchantId = ForgeRegistries.ENCHANTMENTS.getKey(enchantment).toString();

            // Get current level of this enchantment on the weapon
            int currentLevel = net.minecraft.world.item.enchantment.EnchantmentHelper
                    .getTagEnchantmentLevel(enchantment, left);

            // If weapon already has this enchant at the book's max level, block
            if (currentLevel >= bookLevel) {
                System.out.println("[Anvil Debug] Already at max level for this book (" + currentLevel + "/" + bookLevel
                        + "). Canceling.");
                event.setCanceled(true);
                return;
            }

            // If this is a NEW enchantment (level 0), check slot capacity
            if (currentLevel == 0) {
                int maxSlots = 3 + WeaponNBTHelper.getExtraSlots(left);
                int currentEnchancts = left.getEnchantmentTags().size();
                if (currentEnchancts >= maxSlots) {
                    System.out.println("[Anvil Debug] No free enchantment slots! Canceling.");
                    event.setCanceled(true);
                    return;
                }
            }

            // Target level is current + 1
            int targetLevel = currentLevel + 1;

            ItemStack result = left.copy();

            // VERY IMPORTANT: If we are a SwordItem, vanilla might have already added the
            // enchantment
            // to our copy during the anvil update process. We must ensure the result
            // has the PREVIOUS level (not the new one) so the quest can start properly.
            // We'll clear the potential "new" enchantment and restore the old one manually.
            java.util.Map<Enchantment, Integer> currentEnchants = EnchantmentHelper.getEnchantments(result);
            if (currentLevel > 0) {
                currentEnchants.put(enchantment, currentLevel);
            } else {
                currentEnchants.remove(enchantment);
            }
            EnchantmentHelper.setEnchantments(currentEnchants, result);

            com.sticktoslick.data.EnchantmentQuestManager.QuestInfo info = com.sticktoslick.data.EnchantmentQuestManager
                    .getInfoForLevel(enchantment, targetLevel);
            WeaponNBTHelper.startQuest(result, enchantId, info.goal, info.type.name(), targetLevel);

            System.out.println("[Anvil Debug] Started quest for " + enchantId + " Level " + targetLevel
                    + " (Goal: " + info.goal + ")");

            // Set output name to show quest
            result.setHoverName(
                    net.minecraft.network.chat.Component
                            .literal("Quest: " + enchantment.getFullname(targetLevel).getString()));

            event.setOutput(result);
            event.setCost(5 * targetLevel); // XP cost scales with level
            event.setMaterialCost(1);
        }
    }

    /**
     * Modifies enchanting table behavior to limit power based on weapon tier.
     * Lower-tier weapons get weaker enchantments.
     */
    @SubscribeEvent
    public void onEnchantmentLevelSet(EnchantmentLevelSetEvent event) {
        ItemStack stack = event.getItem();
        if (!(stack.getItem() instanceof StarterStickItem))
            return;
        if (!WeaponNBTHelper.hasWeaponData(stack))
            return;

        // If the weapon has 0 enchant slots, disable enchanting entirely
        int maxSlots = 3 + WeaponNBTHelper.getExtraSlots(stack);
        if (maxSlots <= 0) {
            event.setEnchantLevel(0);
            return;
        }

        // Already has max enchantments? Disable further enchanting
        int currentEnchants = stack.getEnchantmentTags().size();
        if (currentEnchants >= maxSlots) {
            event.setEnchantLevel(0);
        }
    }
}
