package com.qwsadd.qwsaddmod.network;
import net.minecraft.network.FriendlyByteBuf;
public class PeeLevelPacket {
    public final int peeLevel;
    public PeeLevelPacket(int peeLevel) { this.peeLevel = peeLevel; }
    public PeeLevelPacket(FriendlyByteBuf buf) { this.peeLevel = buf.readInt(); }
    public void toBytes(FriendlyByteBuf buf) { buf.writeInt(this.peeLevel); }
}
