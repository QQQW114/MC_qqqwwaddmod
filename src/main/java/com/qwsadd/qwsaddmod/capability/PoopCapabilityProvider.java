package com.qwsadd.qwsaddmod.capability;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PoopCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    private final IPoopCapability backend = new PoopCapabilityImpl();
    private final LazyOptional<IPoopCapability> optionalData = LazyOptional.of(() -> backend);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return QwsaddModMain.POOP_CAPABILITY.orEmpty(cap, this.optionalData);
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