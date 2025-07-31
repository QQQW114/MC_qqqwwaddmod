package com.qwsadd.qwsaddmod.items;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PoopHoeItem extends HoeItem {

    public PoopHoeItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        Player player = pContext.getPlayer();
        ItemStack itemstack = pContext.getItemInHand();
        BlockState blockstate = level.getBlockState(blockpos);

        // 检查是否点击的是可施肥的方块
        if (blockstate.getBlock() instanceof BonemealableBlock bonemealableblock) {
            // 确保只在服务端执行逻辑
            if (level instanceof ServerLevel serverlevel) {
                // 【核心修正】为 isValidBonemealTarget 添加第四个 boolean 参数
                if (bonemealableblock.isValidBonemealTarget(serverlevel, blockpos, blockstate, false)) {
                    // 调用方块的施肥方法
                    bonemealableblock.performBonemeal(serverlevel, level.random, blockpos, blockstate);

                    // 播放声音和粒子效果
                    level.levelEvent(1505, blockpos, 0);

                    // 消耗2点耐久度
                    if (player != null) {
                        itemstack.hurtAndBreak(2, player, (p) -> p.broadcastBreakEvent(pContext.getHand()));
                    }

                    return InteractionResult.SUCCESS;
                }
            }
        }

        // 如果上面的施肥逻辑没有成功，则执行锄头原有的耕地功能
        return super.useOn(pContext);
    }
}
