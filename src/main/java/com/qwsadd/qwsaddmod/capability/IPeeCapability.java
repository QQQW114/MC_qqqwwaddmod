package com.qwsadd.qwsaddmod.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;

public interface IPeeCapability extends INBTSerializable<CompoundTag> {
    Capability<IPeeCapability> PEE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    int getPeeLevel();
    void setPeeLevel(int level);
    void addPeeLevel(int amount);
    boolean canPee();
    int getMaxPeeLevel();
    boolean isPeeFull();

    // 序列化方法
    @Override
    CompoundTag serializeNBT();
    @Override
    void deserializeNBT(CompoundTag nbt);
}