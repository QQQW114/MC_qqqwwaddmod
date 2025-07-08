// 文件路径: com/qwsadd/qwsaddmod/network/PeeActionPacket.java
package com.qwsadd.qwsaddmod.network;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.entity.PeeEntity;
import net.minecraft.core.particles.ParticleTypes; // 确保导入
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class PeeActionPacket {

    public PeeActionPacket() {}
    public static void encode(PeeActionPacket msg, FriendlyByteBuf buf) {}
    public static PeeActionPacket decode(FriendlyByteBuf buf) { return new PeeActionPacket(); }

    public static void handle(PeeActionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            player.getCapability(QwsaddModMain.PEE_CAPABILITY).ifPresent(peeCap -> {
                if (peeCap.canPee()) {
                    // 1. 更新服务器端的尿意值
                    int amountToPee = 40;
                    int newLevel = Math.max(0, peeCap.getPeeLevel() - amountToPee);
                    peeCap.setPeeLevel(newLevel);

                    // 2. 将新的尿意值同步到客户端
                    QwsaddModMain.NETWORK_CHANNEL.send(
                            PacketDistributor.PLAYER.with(() -> player),
                            new PeeLevelPacket(newLevel)
                    );

                    // 3. 在世界中生成 PeeEntity 实体
                    PeeEntity peeEntity = new PeeEntity(player);
                    player.level().addFreshEntity(peeEntity);

                    // 4. 播放音效
                    player.playSound(SoundEvents.PLAYER_SPLASH, 1.0F, 1.0F);

                    // 5. 【最终优化】使用 RAIN 粒子实现快速下落的水滴效果
                    if (player.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(
                                ParticleTypes.RAIN,         // 使用雨滴粒子，它会立刻下落
                                player.getX(),              // 玩家 X 坐标
                                player.getY() + 1.0,          // 从玩家腰部高度开始
                                player.getZ(),              // 玩家 Z 坐标
                                15,                         // 生成 15 个粒子就足够了
                                0.3, 0.2, 0.3,              // 在一个较小的范围内散布
                                0.1                         // 给予一个很小的初始速度，让它散开一点
                        );
                    }
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
