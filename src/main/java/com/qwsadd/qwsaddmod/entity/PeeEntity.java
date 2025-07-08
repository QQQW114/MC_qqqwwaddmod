// 文件路径: com/qwsadd/qwsaddmod/entity/PeeEntity.java
package com.qwsadd.qwsaddmod.entity;

// ... (所有 import 保持不变) ...
import com.qwsadd.qwsaddmod.init.EntityInit;
import com.qwsadd.qwsaddmod.init.ItemInit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class PeeEntity extends Entity {

    private static final EntityDataAccessor<Integer> DATA_LIFETIME_ID =
            SynchedEntityData.defineId(PeeEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID_ID =
            SynchedEntityData.defineId(PeeEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private static final int MAX_LIFE_TIME = 1200; // 存在1分钟

    public PeeEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public PeeEntity(Player owner) {
        this(EntityInit.PEE_ENTITY.get(), owner.level());
        this.setOwner(owner);
        // 【优化】让尿生成在玩家正前方一点的位置，而不是脚下
        this.setPos(owner.getX() + owner.getLookAngle().x * 0.5,
                owner.getY(),
                owner.getZ() + owner.getLookAngle().z * 0.5);
    }

    @Override
    protected void defineSynchedData() {
        // 【严重修复】不能调用 super.defineSynchedData()，因为它是一个抽象方法。
        // 直接定义我们自己的数据即可。
        this.entityData.define(DATA_LIFETIME_ID, MAX_LIFE_TIME);
        this.entityData.define(DATA_OWNER_UUID_ID, Optional.empty());
    }

    // ... (文件的其余所有部分都保持不变，从 tick() 方法开始) ...
    @Override
    public void tick() {
        super.tick();

        // 确保实体紧贴地面
        if (!this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04D, 0));
        }
        this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().multiply(0.98F, 1.0F, 0.98F));


        int currentLifeTime = this.getLifeTime();
        if (currentLifeTime > 0) {
            this.setLifeTime(currentLifeTime - 1);
        }

        if (!this.level().isClientSide) {
            if (currentLifeTime <= 0) {
                this.discard();
                return;
            }

            // 对踩在上面的非主人实体施加中毒效果
            Optional<UUID> ownerUUID = getOwnerUUID();
            List<LivingEntity> nearbyEntities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
            for (LivingEntity entity : nearbyEntities) {
                if (ownerUUID.isPresent() && entity.getUUID().equals(ownerUUID.get())) continue;
                if (entity instanceof Player p && (p.isCreative() || p.isSpectator())) continue;
                entity.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 0));
            }
        }
    }

    public int getLifeTime() {
        return this.entityData.get(DATA_LIFETIME_ID);
    }

    public void setLifeTime(int lifeTime) {
        this.entityData.set(DATA_LIFETIME_ID, lifeTime);
    }

    public Optional<UUID> getOwnerUUID() {
        return this.entityData.get(DATA_OWNER_UUID_ID);
    }

    @Nullable
    public LivingEntity getOwner() {
        try {
            Optional<UUID> uuid = getOwnerUUID();
            if (uuid.isPresent() && this.level() instanceof ServerLevel serverLevel) {
                return (LivingEntity) serverLevel.getEntity(uuid.get());
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.entityData.set(DATA_OWNER_UUID_ID, Optional.ofNullable(owner).map(Entity::getUUID));
    }

    @Override
    public boolean isPickable() { return true; }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (!this.level().isClientSide && !this.isRemoved()) {
            this.discard();
        }
        return true;
    }

    @Override
    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        ItemStack heldItem = pPlayer.getItemInHand(pHand);
        if (heldItem.is(Items.BUCKET)) {
            if (!this.level().isClientSide) {
                pPlayer.playSound(SoundEvents.BUCKET_FILL, 1.0F, 1.0F);
                if (!pPlayer.getAbilities().instabuild) {
                    heldItem.shrink(1);
                    pPlayer.getInventory().add(new ItemStack(ItemInit.PEE_BUCKET.get()));
                }
                this.discard();
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else if (heldItem.is(Items.GLASS_BOTTLE)) {
            if (!this.level().isClientSide) {
                pPlayer.playSound(SoundEvents.BOTTLE_FILL, 1.0F, 1.0F);
                if (!pPlayer.getAbilities().instabuild) {
                    heldItem.shrink(1);
                    pPlayer.getInventory().add(new ItemStack(ItemInit.BOTTLE_OF_PEE.get()));
                }
                this.discard();
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.interact(pPlayer, pHand);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.contains("LifeTime", 99)) {
            this.setLifeTime(pCompound.getInt("LifeTime"));
        }
        if (pCompound.hasUUID("Owner")) {
            this.entityData.set(DATA_OWNER_UUID_ID, Optional.of(pCompound.getUUID("Owner")));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("LifeTime", this.getLifeTime());
        this.getOwnerUUID().ifPresent(uuid -> pCompound.putUUID("Owner", uuid));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
