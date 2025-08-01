package com.qwsadd.qwsaddmod.items;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

// 我们让它继承 SwordItem，这样它就拥有了剑的所有基本特性（包括横扫）
public class PoopStaffItem extends SwordItem {

    public PoopStaffItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    /**
     * 当此物品用于伤害一个生物时调用 (从 PoopSwordItem 复制而来)
     */
    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        // 给目标添加中毒效果
        pTarget.addEffect(new MobEffectInstance(MobEffects.POISON, 150, 0));
        // 给目标添加反胃效果
        pTarget.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 300, 1));
        // 给目标添加缓慢效果
        pTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 3));

        // 调用父类的方法，确保耐久度等正常消耗
        return super.hurtEnemy(pStack, pTarget, pAttacker);
    }
}
