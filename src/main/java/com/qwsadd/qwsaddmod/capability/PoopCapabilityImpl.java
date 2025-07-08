// 文件路径: com/qwsadd/qwsaddmod/capability/PoopCapabilityImpl.java
package com.qwsadd.qwsaddmod.capability;

import net.minecraft.nbt.CompoundTag;

public class PoopCapabilityImpl implements IPoopCapability {
    private int poopLevel = 0;
    private static final int MAX_POOP_LEVEL = 100;
    private boolean hasFullWarningShown = false;

    @Override
    public int getPoopLevel() { return poopLevel; }

    @Override
    public void setPoopLevel(int level) {
        this.poopLevel = Math.min(Math.max(level, 0), MAX_POOP_LEVEL);
        if (this.poopLevel < MAX_POOP_LEVEL) {
            hasFullWarningShown = false;
        }
    }

    @Override
    public void addPoopLevel(int amount) {
        setPoopLevel(this.poopLevel + amount);
    }

    @Override
    public int getMaxPoopLevel() { return MAX_POOP_LEVEL; }

    @Override
    public boolean canPoop() { return poopLevel >= 10; }

    // 【修改】将 isFull() 重命名为 isPoopFull()
    @Override
    public boolean isPoopFull() { return poopLevel >= MAX_POOP_LEVEL; }

    @Override
    public boolean hasFullWarningShown() { return hasFullWarningShown; }

    @Override
    public void markFullWarningShown() { this.hasFullWarningShown = true; }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("poopLevel", this.poopLevel);
        tag.putBoolean("hasFullWarningShown", this.hasFullWarningShown);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.poopLevel = nbt.getInt("poopLevel");
        this.hasFullWarningShown = nbt.getBoolean("hasFullWarningShown");
    }
}
