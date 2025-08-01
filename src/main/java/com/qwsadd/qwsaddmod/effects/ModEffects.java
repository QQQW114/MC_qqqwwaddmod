package com.qwsadd.qwsaddmod.effects;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, QwsaddModMain.MODID);

    public static final RegistryObject<MobEffect> DIARRHEA = EFFECTS.register("diarrhea",
            () -> new DiarrheaEffect(MobEffectCategory.HARMFUL, 0x7A5901)); // 棕色

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}
