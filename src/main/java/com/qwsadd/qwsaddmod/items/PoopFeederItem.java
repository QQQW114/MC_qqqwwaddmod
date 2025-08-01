package com.qwsadd.qwsaddmod.items;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.init.ItemInit;
import com.qwsadd.qwsaddmod.network.PoopLevelPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PoopFeederItem extends Item {

    public PoopFeederItem(Properties pProperties) {
        super(pProperties);
    }

    /**
     * 当物品在物品栏中时，每tick调用
     */
    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        // 确保只在服务端运行，并且是在玩家的物品栏中
        if (pLevel.isClientSide() || !(pEntity instanceof Player player)) {
            return;
        }

        // 每秒检查一次（20 tick），避免性能问题
        if (player.tickCount % 20 != 0) {
            return;
        }

        player.getCapability(QwsaddModMain.POOP_CAPABILITY).ifPresent(poopCap -> {
            // 检查便意值是否低于20
            if (poopCap.getPoopLevel() < 20) {
                // 检查玩家物品栏中是否有“大便”
                if (player.getInventory().contains(new ItemStack(ItemInit.POOP_ITEM.get()))) {
                    // 消耗一个“大便”
                    player.getInventory().clearOrCountMatchingItems(p -> p.is(ItemInit.POOP_ITEM.get()), 1, player.inventoryMenu.getCraftSlots());

                    // 增加20点便意值
                    poopCap.addPoopLevel(20);

                    // 将更新后的便意值同步到客户端
                    if (player instanceof ServerPlayer serverPlayer) {
                        QwsaddModMain.NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PoopLevelPacket(poopCap.getPoopLevel()));
                    }
                }
            }
        });
    }

    /**
     * 添加物品描述
     */
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.qwsaddmod.poop_feeder.description")
                .withStyle(ChatFormatting.GRAY));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
