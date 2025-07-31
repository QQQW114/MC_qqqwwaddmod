package com.qwsadd.qwsaddmod.items;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class PoopSwordItem extends SwordItem {

    public PoopSwordItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    /**
     * 当此物品用于伤害一个生物时调用
     * @param pStack 正在使用的物品
     * @param pTarget 被攻击的目标实体
     * @param pAttacker 攻击者
     * @return 返回true表示成功
     */
    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        // 给目标添加中毒效果：持续5秒 (100 ticks)，等级I (amplifier 0)
        pTarget.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));

        // 给目标添加反胃效果：持续7秒 (140 ticks)，等级I (amplifier 0)
        pTarget.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 140, 0));

        // 给目标添加缓慢效果：持续5秒 (100 ticks)，等级II (amplifier 1)
        pTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));

        // 调用父类的方法，确保剑的耐久度等正常消耗
        return super.hurtEnemy(pStack, pTarget, pAttacker);
    }
}
