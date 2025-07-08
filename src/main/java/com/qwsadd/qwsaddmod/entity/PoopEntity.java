package com.qwsadd.qwsaddmod.entity;

import com.qwsadd.qwsaddmod.init.EntityInit;
import com.qwsadd.qwsaddmod.init.ItemInit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class PoopEntity extends Entity {
    private static final EntityDataAccessor<String> DATA_OWNER_NAME =
            SynchedEntityData.defineId(PoopEntity.class, EntityDataSerializers.STRING);
    private int lifeTime = 6000;
    private float health = 10.0F;

    public PoopEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public PoopEntity(Level level, double x, double y, double z) {
        this(EntityInit.POOP_ENTITY.get(), level);
        this.setPos(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04D, 0));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        float friction = 0.98F;
        if (this.onGround()) {
            friction = this.level().getBlockState(this.getOnPosLegacy()).getBlock().getFriction() * 0.98F;
        }
        this.setDeltaMovement(this.getDeltaMovement().multiply(friction, 1.0F, friction));
        if (!this.level().isClientSide && this.lifeTime > 0) {
            this.lifeTime--;
        } else if (this.lifeTime <= 0) {
            this.kill();
        }
    }

    @Override
    public boolean isPickable() {
        return true;
    }
    @Override
    public boolean isPushable() {
        return true;
    }
    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        if (!this.level().isClientSide && tryPickUp(pPlayer)) {
            // 【新增】在实体的位置播放“小史莱姆挤压”音效
            this.playSound(SoundEvents.SLIME_SQUISH_SMALL, 1.0F, 1.0F);

            // 原有的中毒效果保持不变
            pPlayer.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
            return InteractionResult.SUCCESS;
        }
        return super.interact(pPlayer, pHand);
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.level().isClientSide || this.isRemoved()) {
            return false;
        }
        this.health -= pAmount;
        this.playSound(SoundEvents.SLIME_HURT_SMALL, 1.0f, 1.0f);
        if (this.health <= 0.0F) {
            this.dieAndDrop();
        }
        return true;
    }

    @Override
    public ItemStack getPickResult() {
        return createPoopItemStack();
    }

    private void dieAndDrop() {
        this.playSound(SoundEvents.SLIME_DEATH_SMALL, 1.0f, 1.0f);
        if (!this.level().isClientSide) {
            ItemStack drop = createPoopItemStack();
            ItemEntity itemEntity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), drop);
            this.level().addFreshEntity(itemEntity);
        }
        this.kill();
    }

    private boolean tryPickUp(Player player) {
        if (!this.isRemoved()) {
            ItemStack stack = createPoopItemStack();
            if (player.getInventory().add(stack)) {
                this.kill();
                return true;
            }
        }
        return false;
    }

    private ItemStack createPoopItemStack() {
        ItemStack poopItemStack = new ItemStack(ItemInit.POOP_ITEM.get());
        poopItemStack.getOrCreateTag().putString("OwnerName", this.getOwnerName());
        return poopItemStack;
    }

    public void setOwnerName(String name) {
        this.entityData.set(DATA_OWNER_NAME, name);
    }
    public String getOwnerName() {
        return this.entityData.get(DATA_OWNER_NAME);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_OWNER_NAME, "");
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.lifeTime = pCompound.getInt("LifeTime");
        setOwnerName(pCompound.getString("OwnerName"));
        if (pCompound.contains("Health")) {
            this.health = pCompound.getFloat("Health");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("LifeTime", this.lifeTime);
        pCompound.putString("OwnerName", getOwnerName());
        pCompound.putFloat("Health", this.health);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}