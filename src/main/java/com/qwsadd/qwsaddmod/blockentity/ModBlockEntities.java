package com.qwsadd.qwsaddmod.blockentity;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.init.BlockInit;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.qwsadd.qwsaddmod.blockentity.SquatToiletBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, QwsaddModMain.MODID);

    public static final RegistryObject<BlockEntityType<PoopFermenterBlockEntity>> POOP_FERMENTER_BE =
            BLOCK_ENTITIES.register("poop_fermenter_be", () ->
                    BlockEntityType.Builder.of(PoopFermenterBlockEntity::new,
                            BlockInit.POOP_FERMENTER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
    public static final RegistryObject<BlockEntityType<SquatToiletBlockEntity>> SQUAT_TOILET_BE =
            BLOCK_ENTITIES.register("squat_toilet_be", () ->
                    BlockEntityType.Builder.of(SquatToiletBlockEntity::new,
                            BlockInit.SQUAT_TOILET.get()).build(null));
}
