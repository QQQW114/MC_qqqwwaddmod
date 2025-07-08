package com.qwsadd.qwsaddmod.network;

import com.qwsadd.qwsaddmod.client.ClientPoopLevelData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PoopLevelPacket {
    private final int poopLevel;

    public PoopLevelPacket(int poopLevel) {
        this.poopLevel = poopLevel;
    }

    public static void encode(PoopLevelPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.poopLevel);
    }

    public static PoopLevelPacket decode(FriendlyByteBuf buf) {
        return new PoopLevelPacket(buf.readInt());
    }

    public static void handle(PoopLevelPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 【修复】使用正确的方法名 setClientPoopLevel()
            ClientPoopLevelData.setClientPoopLevel(msg.poopLevel);
        });
        ctx.get().setPacketHandled(true);
    }
}