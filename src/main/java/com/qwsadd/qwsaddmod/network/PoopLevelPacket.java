package com.qwsadd.qwsaddmod.network;

import net.minecraft.network.FriendlyByteBuf;

public class PoopLevelPacket {
    public final int poopLevel;

    public PoopLevelPacket(int poopLevel) {
        this.poopLevel = poopLevel;
    }

    public PoopLevelPacket(FriendlyByteBuf buf) {
        this.poopLevel = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.poopLevel);
    }
}
