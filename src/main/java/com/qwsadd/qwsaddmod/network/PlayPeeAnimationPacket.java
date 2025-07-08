package com.qwsadd.qwsaddmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft; // 需要导入
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import java.util.UUID;
import java.util.function.Supplier;

// 文件路径: com/qwsadd/qwsaddmod/network/PlayPeeAnimationPacket.java
// ... imports ...
public class PlayPeeAnimationPacket {
    // ... 构造函数, encode, decode 保持不变 ...
    private final UUID playerUUID;
    public PlayPeeAnimationPacket(UUID playerUUID) { this.playerUUID = playerUUID; }
    public static void encode(PlayPeeAnimationPacket msg, FriendlyByteBuf buf) { buf.writeUUID(msg.playerUUID); }
    public static PlayPeeAnimationPacket decode(FriendlyByteBuf buf) { return new PlayPeeAnimationPacket(buf.readUUID()); }

    // 【修复】handle 方法需要两个参数
    public static void handle(PlayPeeAnimationPacket msg, Supplier<NetworkEvent.Context> ctx) {
        // ... 内部逻辑保持不变 ...
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Player player = Minecraft.getInstance().level.getPlayerByUUID(msg.playerUUID);
                if (player != null) {
                    // 动画逻辑
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
