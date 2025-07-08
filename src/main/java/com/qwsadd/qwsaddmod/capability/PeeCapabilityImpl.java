// 文件路径: com/qwsadd/qwsaddmod/capability/PeeCapabilityImpl.java
package com.qwsadd.qwsaddmod.capability;

import net.minecraft.nbt.CompoundTag;

// 类名必须是 PeeCapabilityImpl，并且必须实现 IPeeCapability 接口
public class PeeCapabilityImpl implements IPeeCapability {

    private int peeLevel = 0;
    private static final int MAX_PEE_LEVEL = 100;

    @Override
    public int getPeeLevel() {
        return this.peeLevel;
    }

    @Override
    public void setPeeLevel(int level) {
        this.peeLevel = Math.max(0, Math.min(level, this.getMaxPeeLevel()));
    }

    @Override
    public void addPeeLevel(int amount) {
        this.setPeeLevel(this.peeLevel + amount);
    }

    @Override
    public boolean canPee() {
        return this.peeLevel > 0;
    }

    @Override
    public int getMaxPeeLevel() {
        return MAX_PEE_LEVEL;
    }

    @Override
    public boolean isPeeFull() {
        // 这里是 isPeeFull() 方法的具体实现，解决了最初的编译错误
        return this.peeLevel >= this.getMaxPeeLevel();
    }

    // --- NBT 序列化 ---

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("pee_level", this.peeLevel);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.peeLevel = nbt.getInt("pee_level");
    }
}
