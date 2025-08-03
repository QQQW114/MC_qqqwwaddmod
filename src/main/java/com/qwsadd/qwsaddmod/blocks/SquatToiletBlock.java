package com.qwsadd.qwsaddmod.blocks;

import com.qwsadd.qwsaddmod.blockentity.SquatToiletBlockEntity;
import com.qwsadd.qwsaddmod.entity.SeatEntity; // 新增 import
import com.qwsadd.qwsaddmod.init.ItemInit;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;


public class SquatToiletBlock extends BaseEntityBlock {
    public static final int MAX_LEVEL = 8;
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_COMPOSTER;

    public SquatToiletBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LEVEL);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        int currentLevel = pState.getValue(LEVEL);
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (currentLevel > 0) {
            extractPoopAndDrop(pState, pLevel, pPos);
            return InteractionResult.CONSUME;
        } else {
            // 【核心修正】调用 SeatEntity 的辅助方法来创建座椅
            SeatEntity.createSeat(pLevel, pPos, pPlayer);
            return InteractionResult.SUCCESS;
        }
    }

    // 【已重命名】这个方法现在专门给玩家右键使用
    public static void extractPoopAndDrop(BlockState pState, Level pLevel, BlockPos pPos) {
        int currentLevel = pState.getValue(LEVEL);
        if (currentLevel > 0) {
            popResource(pLevel, pPos, new ItemStack(ItemInit.POOP_ITEM.get())); // 产生掉落物
            pLevel.setBlock(pPos, pState.setValue(LEVEL, currentLevel - 1), 3);
            pLevel.playSound(null, pPos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }
    // ==================== ↓↓↓ 新增：专门给漏斗使用的方法 ↓↓↓ ====================
    /**
     * 只降低堆肥等级，不产生掉落物。
     * @return 返回true表示成功提取
     */
    public static boolean extractPoopForHopper(BlockState pState, Level pLevel, BlockPos pPos) {
        int currentLevel = pState.getValue(LEVEL);
        if (currentLevel > 0) {
            pLevel.setBlock(pPos, pState.setValue(LEVEL, currentLevel - 1), 3);
            pLevel.playSound(null, pPos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            return true;
        }
        return false;
    }
// ==================== ↑↑↑ 新增代码结束 ↑↑↑ ====================

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SquatToiletBlockEntity(pPos, pState);
    }

    // getTicker 方法可以被移除，因为我们的 BlockEntity 不需要 tick
}
