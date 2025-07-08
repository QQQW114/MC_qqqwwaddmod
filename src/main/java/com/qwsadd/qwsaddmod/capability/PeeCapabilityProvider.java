// 文件路径: com/qwsadd/qwsaddmod/capability/PeeCapabilityProvider.java
package com.qwsadd.qwsaddmod.capability;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// 【修复】这是一个全新的、完整的实现
public class PeeCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    // 【修复】使用 IPeeCapability 和 PeeCapabilityImpl
    private final IPeeCapability backend = new PeeCapabilityImpl();
    private final LazyOptional<IPeeCapability> optionalData = LazyOptional.of(() -> backend);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        // 【修复】返回 PEE_CAPABILITY
        return QwsaddModMain.PEE_CAPABILITY.orEmpty(cap, this.optionalData);
    }

    @Override
    public CompoundTag serializeNBT() {
        return backend.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        backend.deserializeNBT(nbt);
    }
}
