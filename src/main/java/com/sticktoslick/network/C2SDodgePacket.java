package com.sticktoslick.network;

import com.sticktoslick.data.WeaponNBTHelper;
import com.sticktoslick.item.StarterStickItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SDodgePacket {
    public C2SDodgePacket() {
    }

    public C2SDodgePacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null)
                return;

            ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (mainHand.getItem() instanceof StarterStickItem && WeaponNBTHelper.hasWeaponData(mainHand)) {
                // Exhaust the player slightly to prevent infinite dodging (stamina cost)
                player.causeFoodExhaustion(2.0f);

                // Play dash sound for everyone around
                player.level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP,
                        SoundSource.PLAYERS, 0.5f, 1.5f);

                // Track cooldown natively using item cooldown
                player.getCooldowns().addCooldown(mainHand.getItem(), 40); // 2 second cooldown
            }
        });
        return true;
    }
}
