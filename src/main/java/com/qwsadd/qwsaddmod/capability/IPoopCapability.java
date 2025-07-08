// 文件路径: com/qwsadd/qwsaddmod/capability/IPoopCapability.java
package com.qwsadd.qwsaddmod.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IPoopCapability extends INBTSerializable<CompoundTag> {
    int getPoopLevel();
    void setPoopLevel(int level);
    void addPoopLevel(int amount);
    boolean canPoop();
    int getMaxPoopLevel();

    // 【修改】将 isFull() 重命名为 isPoopFull()
    boolean isPoopFull();

    boolean hasFullWarningShown();
    void markFullWarningShown();

    @Override
    CompoundTag serializeNBT();
    @Override
    void deserializeNBT(CompoundTag nbt);
}
