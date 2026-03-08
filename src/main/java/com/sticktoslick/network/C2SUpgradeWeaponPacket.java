package com.sticktoslick.network;

import com.sticktoslick.event.ItemInteractionHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SUpgradeWeaponPacket {
    private final String upgradeType; // "damage", "speed", "movement", "knockback", "evolve"
    private final String extraData; // Can carry target weapon class or other info

    public C2SUpgradeWeaponPacket(String upgradeType) {
        this.upgradeType = upgradeType;
        this.extraData = "";
    }

    public C2SUpgradeWeaponPacket(String upgradeType, String extraData) {
        this.upgradeType = upgradeType;
        this.extraData = extraData != null ? extraData : "";
    }

    public C2SUpgradeWeaponPacket(FriendlyByteBuf buf) {
        this.upgradeType = buf.readUtf();
        this.extraData = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(upgradeType);
        buf.writeUtf(extraData);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Server side
            ServerPlayer player = context.getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                // Reuse logic from ItemInteractionHandler
                ItemInteractionHandler.handleUpgradeFromPacket(player, stack, upgradeType, extraData);
            }
        });
        return true;
    }
}
