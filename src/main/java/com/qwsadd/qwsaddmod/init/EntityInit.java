package com.qwsadd.qwsaddmod.init;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.entity.PeeEntity;
import com.qwsadd.qwsaddmod.entity.PoopEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityInit {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, QwsaddModMain.MODID);

    // 原有的 Poop Entity
    public static final RegistryObject<EntityType<PoopEntity>> POOP_ENTITY =
            ENTITIES.register("poop",
                    () -> EntityType.Builder.<PoopEntity>of(PoopEntity::new, MobCategory.MISC)
                            .sized(0.6F, 0.6F)
                            .clientTrackingRange(4)
                            .build("poop")
            );

    // 【新增】注册“一滩尿”实体类型
    public static final RegistryObject<EntityType<PeeEntity>> PEE_ENTITY =
            ENTITIES.register("pee",
                    () -> EntityType.Builder.<PeeEntity>of(PeeEntity::new, MobCategory.MISC)
                            .sized(1.0F, 0.1F) // 碰撞箱比较宽，但是非常扁
                            .clientTrackingRange(4)
                            .build("pee")
            );
}