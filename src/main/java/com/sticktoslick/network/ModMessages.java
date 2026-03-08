package com.sticktoslick.network;

import com.sticktoslick.StickToSlick;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(StickToSlick.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(C2SUpgradeWeaponPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SUpgradeWeaponPacket::new)
                .encoder(C2SUpgradeWeaponPacket::toBytes)
                .consumerMainThread(C2SUpgradeWeaponPacket::handle)
                .add();

        net.messageBuilder(S2COpenUpgradeScreenPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(S2COpenUpgradeScreenPacket::new)
                .encoder(S2COpenUpgradeScreenPacket::toBytes)
                .consumerMainThread(S2COpenUpgradeScreenPacket::handle)
                .add();

        net.messageBuilder(S2CLevelUpVfxPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(S2CLevelUpVfxPacket::new)
                .encoder(S2CLevelUpVfxPacket::toBytes)
                .consumerMainThread(S2CLevelUpVfxPacket::handle)
                .add();

        net.messageBuilder(S2CHitFeedbackPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(S2CHitFeedbackPacket::new)
                .encoder(S2CHitFeedbackPacket::toBytes)
                .consumerMainThread(S2CHitFeedbackPacket::handle)
                .add();

        net.messageBuilder(C2SDodgePacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SDodgePacket::new)
                .encoder(C2SDodgePacket::toBytes)
                .consumerMainThread(C2SDodgePacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
