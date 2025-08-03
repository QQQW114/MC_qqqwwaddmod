package com.qwsadd.qwsaddmod.entity;

import com.qwsadd.qwsaddmod.init.BlockInit;
import com.qwsadd.qwsaddmod.init.EntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player; // <-- 在这里添加 import
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;

public class SeatEntity extends Entity {

    public SeatEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
    }

    public SeatEntity(Level level, BlockPos pos) {
        this(EntityInit.SEAT_ENTITY.get(), level);
        // 【核心修正】调整Y轴高度，从 0.1 改为 0.5
        this.setPos(pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (this.getPassengers().isEmpty() || !this.level().getBlockState(this.blockPosition()).is(BlockInit.SQUAT_TOILET.get())) {
                this.discard();
            }
        }
    }

    @Override
    protected void defineSynchedData() {}
    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {}
    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static void createSeat(Level level, BlockPos pos, Player player) {
        if (!level.isClientSide && player.getVehicle() == null) {
            List<SeatEntity> seats = level.getEntitiesOfClass(SeatEntity.class, new AABB(pos));
            if (seats.isEmpty()) {
                SeatEntity seat = new SeatEntity(level, pos);
                level.addFreshEntity(seat);
                player.startRiding(seat, true);
            }
        }
    }
}
