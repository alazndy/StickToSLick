package com.sticktoslick.event;

import com.sticktoslick.data.WeaponNBTHelper;
import com.sticktoslick.item.StarterStickItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * On death, the player loses 30% of their weapon's current XP.
 * Level never decreases — only XP within the current level is affected.
 */
public class DeathPenaltyHandler {

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath())
            return;

        Player newPlayer = event.getEntity();
        Player oldPlayer = event.getOriginal();

        // Check all inventory slots for weapons
        for (int i = 0; i < oldPlayer.getInventory().getContainerSize(); i++) {
            ItemStack oldStack = oldPlayer.getInventory().getItem(i);
            if (oldStack.isEmpty() || !(oldStack.getItem() instanceof StarterStickItem))
                continue;
            if (!WeaponNBTHelper.hasWeaponData(oldStack))
                continue;

            // Find the corresponding slot in the new inventory (keepInventory or totem)
            ItemStack newStack = newPlayer.getInventory().getItem(i);
            if (newStack.isEmpty() || !(newStack.getItem() instanceof StarterStickItem))
                continue;
            if (!WeaponNBTHelper.hasWeaponData(newStack))
                continue;

            int currentXP = WeaponNBTHelper.getXP(newStack);
            int penalty = (int) (currentXP * 0.30);

            if (penalty > 0) {
                WeaponNBTHelper.setXP(newStack, currentXP - penalty);
                newPlayer.sendSystemMessage(Component.literal("💀 Ölüm Cezası: -" + penalty + " Silah XP")
                        .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
            }
        }
    }
}
