package com.qwsadd.qwsaddmod.items;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class PeeBucketItem extends Item {
    public PeeBucketItem(Properties pProperties) {
        super(pProperties.stacksTo(1)); // 桶不能堆叠
    }

    // 这个方法在玩家喝完物品后被调用
    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        // 确保是玩家在喝
        if (pLivingEntity instanceof Player player) {
            // 添加负面效果
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0)); // 中毒10秒
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0)); // 反胃10秒
        }

        // 如果是创造模式，不消耗物品
        if (pLivingEntity instanceof Player player && player.getAbilities().instabuild) {
            return pStack;
        }

        // 生存模式下，喝完后返还一个空桶
        return new ItemStack(Items.BUCKET);
    }

    // 决定物品可以被使用多久（喝多久）
    @Override
    public int getUseDuration(ItemStack pStack) {
        return 32; // 和牛奶、药水一样
    }

    // 决定使用物品时的动画（喝的动画）
    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.DRINK;
    }

    // 这个方法在玩家开始右键使用物品时调用
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pUsedHand);
    }
}