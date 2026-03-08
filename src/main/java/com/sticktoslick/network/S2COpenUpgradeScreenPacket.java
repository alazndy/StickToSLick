package com.sticktoslick.network;

import com.sticktoslick.client.gui.WeaponUpgradeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2COpenUpgradeScreenPacket {
    public S2COpenUpgradeScreenPacket() {
    }

    public S2COpenUpgradeScreenPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Client side
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                if (Minecraft.getInstance().player != null) {
                    ItemStack stack = Minecraft.getInstance().player.getMainHandItem();
                    Minecraft.getInstance().setScreen(new WeaponUpgradeScreen(stack));
                }
            });
        });
        return true;
    }
}
