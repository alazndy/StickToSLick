package com.sticktoslick.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CLevelUpVfxPacket {
    private final int level;
    private final int tierColor;

    public S2CLevelUpVfxPacket(int level, int tierColor) {
        this.level = level;
        this.tierColor = tierColor;
    }

    public S2CLevelUpVfxPacket(FriendlyByteBuf buf) {
        this.level = buf.readInt();
        this.tierColor = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(level);
        buf.writeInt(tierColor);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            com.sticktoslick.client.vfx.LevelUpVfxRenderer.trigger(level, tierColor);
        });
        return true;
    }
}
