package com.qwsadd.qwsaddmod.network;
import net.minecraft.network.FriendlyByteBuf;
import java.util.UUID;
public class PlayPeeAnimationPacket {
    public final UUID playerUUID;
    public PlayPeeAnimationPacket(UUID playerUUID) { this.playerUUID = playerUUID; }
    public PlayPeeAnimationPacket(FriendlyByteBuf buf) { this.playerUUID = buf.readUUID(); }
    public void toBytes(FriendlyByteBuf buf) { buf.writeUUID(this.playerUUID); }
}
