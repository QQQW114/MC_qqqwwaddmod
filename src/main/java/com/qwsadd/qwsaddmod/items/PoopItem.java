package com.qwsadd.qwsaddmod.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player; // 新增 import
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PoopItem extends Item {

    // 定义食物属性 (这部分代码保持不变)
    private static final FoodProperties FOOD_PROPERTIES = new FoodProperties.Builder()
            .nutrition(1)
            .saturationMod(0.1F)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 200, 0), 1.0F)
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 100, 0), 1.0F)
            .alwaysEat()
            .build();

    public PoopItem(Properties properties) {
        // 将食物属性应用到物品上 (这部分代码保持不变)
        super(properties.food(FOOD_PROPERTIES));
    }

    // 重写这个方法来添加自定义的物品提示 (这部分代码保持不变)
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        if (pStack.hasTag() && pStack.getTag().contains("OwnerName")) {
            String ownerName = pStack.getTag().getString("OwnerName");
            pTooltipComponents.add(Component.translatable("tooltip.qwsaddmod.poop.owner", ownerName)
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    // ==================================================================
    // ====================    ↓↓↓ 修正后的代码 ↓↓↓    ====================
    // ==================================================================

    /**
     * 当物品被用于方块上时调用 (例如，右键点击一个方块)
     */
    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        Player player = pContext.getPlayer(); // 从 context 中获取玩家
        ItemStack itemstack = pContext.getItemInHand();

        // 检查被点击的方块是否可以被施肥
        if (level.getBlockState(blockpos).getBlock() instanceof BonemealableBlock) {
            // 尝试应用骨粉效果，这次传入了 player 参数
            if (BoneMealItem.applyBonemeal(itemstack, level, blockpos, player)) {
                // 如果成功，只在服务端执行效果
                if (!level.isClientSide) {
                    // 播放原版骨粉成功时的声音和粒子效果
                    level.levelEvent(1505, blockpos, 0);
                }
                // 返回成功，这样客户端会播放手臂挥动的动画
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        // 如果不能施肥，返回PASS，允许其他操作（比如吃掉它）
        return InteractionResult.PASS;
    }

    // ==================================================================
    // ====================    ↑↑↑ 修正后的代码 ↑↑↑    ====================
    // ==================================================================
}
