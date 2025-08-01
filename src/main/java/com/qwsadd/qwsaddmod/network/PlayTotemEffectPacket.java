package com.qwsadd.qwsaddmod.network;

import com.qwsadd.qwsaddmod.init.ItemInit;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayTotemEffectPacket {

    public PlayTotemEffectPacket() {}

    public static void encode(PlayTotemEffectPacket msg, FriendlyByteBuf buf) {}

    public static PlayTotemEffectPacket decode(FriendlyByteBuf buf) {
        return new PlayTotemEffectPacket();
    }

    public static void handle(PlayTotemEffectPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 确保只在客户端执行
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    // 触发客户端的不死图腾视觉效果
                    Minecraft.getInstance().gameRenderer.displayItemActivation(new ItemStack(ItemInit.PEE_EXOSKELETON.get()));
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }
}
