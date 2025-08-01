package com.qwsadd.qwsaddmod.items;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.init.ItemInit;
import com.qwsadd.qwsaddmod.network.PeeLevelPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PeeFeederItem extends Item {

    public PeeFeederItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pLevel.isClientSide() || !(pEntity instanceof Player player)) {
            return;
        }

        if (player.tickCount % 20 != 0) {
            return;
        }

        player.getCapability(QwsaddModMain.PEE_CAPABILITY).ifPresent(peeCap -> {
            // 检查尿意值是否低于20
            if (peeCap.getPeeLevel() < 20) {
                // 检查玩家物品栏中是否有“一瓶尿”
                if (player.getInventory().contains(new ItemStack(ItemInit.BOTTLE_OF_PEE.get()))) {
                    // 消耗一个“一瓶尿”
                    player.getInventory().clearOrCountMatchingItems(p -> p.is(ItemInit.BOTTLE_OF_PEE.get()), 1, player.inventoryMenu.getCraftSlots());

                    // 返还一个空玻璃瓶
                    player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));

                    // 增加20点尿意值
                    peeCap.addPeeLevel(20);

                    // 将更新后的尿意值同步到客户端
                    if (player instanceof ServerPlayer serverPlayer) {
                        QwsaddModMain.NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PeeLevelPacket(peeCap.getPeeLevel()));
                    }
                }
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.qwsaddmod.pee_feeder.description")
                .withStyle(ChatFormatting.GRAY));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
