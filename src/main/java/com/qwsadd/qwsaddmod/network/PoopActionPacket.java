package com.qwsadd.qwsaddmod.network;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.entity.PoopEntity; // 1. 导入我们的新实体
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class PoopActionPacket {
    public PoopActionPacket() {}
    public static void encode(PoopActionPacket msg, FriendlyByteBuf buf) {}
    public static PoopActionPacket decode(FriendlyByteBuf buf) { return new PoopActionPacket(); }

    // 【修复】handle 方法需要两个参数
    public static void handle(PoopActionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        // ... 内部逻辑保持不变 ...
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            player.getCapability(QwsaddModMain.POOP_CAPABILITY).ifPresent(cap -> {
                if (cap.canPoop()) {
                    int newLevel = Math.max(cap.getPoopLevel() - 25, 0);
                    cap.setPoopLevel(newLevel);

                    QwsaddModMain.NETWORK_CHANNEL.send(
                            PacketDistributor.PLAYER.with(() -> player),
                            new PoopLevelPacket(newLevel)
                    );

                    PoopEntity poopEntity = new PoopEntity(player.level(), player.getX(), player.getY(), player.getZ());
                    poopEntity.setOwnerName(player.getDisplayName().getString());
                    player.level().addFreshEntity(poopEntity);
                    player.playSound(SoundEvents.PLAYER_SPLASH_HIGH_SPEED, 1.0F, 0.8F);
                } else {
                    player.sendSystemMessage(Component.translatable("message.qwsaddmod.cannot_poop"));
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}