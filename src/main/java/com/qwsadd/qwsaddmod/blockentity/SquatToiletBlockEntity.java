package com.qwsadd.qwsaddmod.blockentity;

import com.qwsadd.qwsaddmod.blocks.SquatToiletBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SquatToiletBlockEntity extends BlockEntity {
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(this::createHandler);

    public SquatToiletBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SQUAT_TOILET_BE.get(), pPos, pBlockState);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER && side == Direction.DOWN) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    private IItemHandler createHandler() {
        return new InvWrapper(null) { // We don't have a real inventory
            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (level == null || amount == 0) {
                    return ItemStack.EMPTY;
                }
                BlockState state = getBlockState();
                int currentLevel = state.getValue(SquatToiletBlock.LEVEL);
                if (currentLevel > 0) {
                    if (!simulate) {
                        SquatToiletBlock.extractPoopForHopper(state, level, worldPosition);
                    }
                    return new ItemStack(com.qwsadd.qwsaddmod.init.ItemInit.POOP_ITEM.get(), 1);
                }
                return ItemStack.EMPTY;
            }

            @Override
            public int getSlots() {
                return 1; // Virtual slot
            }

            @Override
            public @NotNull ItemStack getStackInSlot(int slot) {
                return ItemStack.EMPTY;
            }

            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                return stack; // Cannot insert items
            }
        };
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, SquatToiletBlockEntity blockEntity) {
        // This BE doesn't need to tick itself, the logic is in PlayerEvents
    }
}
