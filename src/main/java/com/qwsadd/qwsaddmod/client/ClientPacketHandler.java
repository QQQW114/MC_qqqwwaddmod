package com.qwsadd.qwsaddmod.client;

import com.qwsadd.qwsaddmod.network.PeeLevelPacket;
import com.qwsadd.qwsaddmod.network.PlayPeeAnimationPacket;
import com.qwsadd.qwsaddmod.network.PlayTotemEffectPacket;
import com.qwsadd.qwsaddmod.network.PoopLevelPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ClientPacketHandler {

    public static BiConsumer<PoopLevelPacket, Supplier<NetworkEvent.Context>> getPoopLevelConsumer() {
        return (msg, ctx) -> {
            ctx.get().enqueueWork(() -> ClientPoopLevelData.setClientPoopLevel(msg.poopLevel));
            ctx.get().setPacketHandled(true);
        };
    }

    public static BiConsumer<PeeLevelPacket, Supplier<NetworkEvent.Context>> getPeeLevelConsumer() {
        return (msg, ctx) -> {
            ctx.get().enqueueWork(() -> ClientPeeLevelData.setClientPeeLevel(msg.peeLevel));
            ctx.get().setPacketHandled(true);
        };
    }

    public static BiConsumer<PlayPeeAnimationPacket, Supplier<NetworkEvent.Context>> getPlayPeeAnimationConsumer() {
        return (msg, ctx) -> {
            ctx.get().enqueueWork(() -> {
                Player player = Minecraft.getInstance().level.getPlayerByUUID(msg.playerUUID);
                if (player != null) {
                    // 动画逻辑
                }
            });
            ctx.get().setPacketHandled(true);
        };
    }

    public static BiConsumer<PlayTotemEffectPacket, Supplier<NetworkEvent.Context>> getPlayTotemEffectConsumer() {
        return (msg, ctx) -> {
            ctx.get().enqueueWork(() -> {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    Minecraft.getInstance().gameRenderer.displayItemActivation(new ItemStack(com.qwsadd.qwsaddmod.init.ItemInit.PEE_EXOSKELETON.get()));
                }
            });
            ctx.get().setPacketHandled(true);
        };
    }
}
