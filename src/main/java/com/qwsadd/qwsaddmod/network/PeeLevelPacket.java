// 文件路径: com/qwsadd/qwsaddmod/network/PeeLevelPacket.java
package com.qwsadd.qwsaddmod.network;

import com.qwsadd.qwsaddmod.client.ClientPeeLevelData; // 【新增】需要导入新的客户端数据类
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// 【修复】这是一个全新的文件
public class PeeLevelPacket {
    private final int peeLevel;

    public PeeLevelPacket(int peeLevel) {
        this.peeLevel = peeLevel;
    }

    public static void encode(PeeLevelPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.peeLevel);
    }

    public static PeeLevelPacket decode(FriendlyByteBuf buf) {
        return new PeeLevelPacket(buf.readInt());
    }

    public static void handle(PeeLevelPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 【修复】更新客户端的尿意值
            ClientPeeLevelData.setClientPeeLevel(msg.peeLevel);
        });
        ctx.get().setPacketHandled(true);
    }
}
