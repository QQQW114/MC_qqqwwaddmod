package com.qwsadd.qwsaddmod.core;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ModDamageTypes {

    /**
     * 这是数据驱动伤害类型的“钥匙” (ResourceKey)。
     * 我们将继续使用这个key来从注册表中获取伤害类型数据。
     */
    public static final ResourceKey<DamageType> POOP_DAMAGE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            new ResourceLocation(QwsaddModMain.MODID, "poop_damage")
    );

    /**
     * 【新增】工厂方法，用于创建我们带有自定义死亡消息的特殊伤害源。
     * 在 PlayerEvents 中，我们将调用 ModDamageTypes.incontinence(player) 来获取伤害源。
     * @param player 受到伤害的玩家
     * @return 一个 IncontinenceDamageSource 实例
     */
    public static DamageSource incontinence(Player player) {
        Registry<DamageType> registry = player.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        Holder<DamageType> holder = registry.getHolderOrThrow(POOP_DAMAGE);
        return new IncontinenceDamageSource(holder);
    }

    /**
     * 【新增】内部静态类，用于处理自定义死亡消息。
     * 它被集成到了这个文件中，避免了创建新文件。
     */
    public static class IncontinenceDamageSource extends DamageSource {

        public IncontinenceDamageSource(Holder<DamageType> damageTypeHolder) {
            super(damageTypeHolder);
        }

        @Override
        public Component getLocalizedDeathMessage(LivingEntity pKilledEntity) {
            // 这个key "death.attack.qwsaddmod.incontinence" 将在语言文件中被翻译为 "xx 憋死了"
            String s = "death.attack.qwsaddmod.incontinence";
            return Component.translatable(s, pKilledEntity.getDisplayName());
        }
    }
}
