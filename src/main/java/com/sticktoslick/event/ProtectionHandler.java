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
 * Protects weapon NBT data from being wiped:
 * 1. Grindstone: Prevents disenchanting our weapons (which would wipe custom
 * NBT)
 * 2. Crafting Grid: Prevents combining two weapons in crafting grid (vanilla
 * SwordItem repair)
 */
public class ProtectionHandler {

    /**
     * Intercepts the crafting result to prevent vanilla crafting-grid repair
     * which would destroy our NBT data by merging two damaged items.
     */
    @SubscribeEvent
    public void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack result = event.getCrafting();

        // If the result is our weapon item
        if (result.getItem() instanceof StarterStickItem) {
            // Check if it was a crafting-grid repair (2 items combined)
            // In vanilla, combining two damaged SwordItems in crafting grid creates a new
            // one
            // This would lose all our NBT data, so we block it
            boolean hasWeaponInputs = false;
            for (int i = 0; i < event.getInventory().getContainerSize(); i++) {
                ItemStack input = event.getInventory().getItem(i);
                if (input.getItem() instanceof StarterStickItem && WeaponNBTHelper.hasWeaponData(input)) {
                    if (hasWeaponInputs) {
                        // Second weapon found in crafting grid = vanilla repair attempt
                        // Cancel by clearing the result
                        event.getCrafting().setCount(0);
                        Player player = event.getEntity();
                        player.sendSystemMessage(
                                Component.literal("⚠ Silahları crafting ile birleştiremezsin! Örs kullan.")
                                        .withStyle(ChatFormatting.RED));
                        return;
                    }
                    hasWeaponInputs = true;
                }
            }
        }
    }
}
