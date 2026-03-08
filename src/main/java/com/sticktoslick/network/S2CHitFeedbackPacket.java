package com.sticktoslick.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import com.sticktoslick.client.vfx.HitFeedbackRenderer;

import java.util.function.Supplier;

/**
 * Sent from server to client when a weapon hit connects.
 * Triggers camera shake, FOV pulse, and other "impact" feels.
 */
public class S2CHitFeedbackPacket {
    private final float damage;
    private final boolean isCrit;

    public S2CHitFeedbackPacket(float damage, boolean isCrit) {
        this.damage = damage;
        this.isCrit = isCrit;
    }

    public S2CHitFeedbackPacket(FriendlyByteBuf buf) {
        this.damage = buf.readFloat();
        this.isCrit = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(damage);
        buf.writeBoolean(isCrit);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Forward to client-side renderer
            HitFeedbackRenderer.handleHit(damage, isCrit);
        });
        return true;
    }
}
