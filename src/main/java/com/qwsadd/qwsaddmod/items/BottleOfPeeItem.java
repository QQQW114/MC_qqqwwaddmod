package com.qwsadd.qwsaddmod.items;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

public class BottleOfPeeItem extends Item {
    public BottleOfPeeItem(Properties pProperties) {
        super(pProperties); // 默认可以堆叠到64
    }

    // 喝完后的逻辑
    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        if (pLivingEntity instanceof Player player) {
            // 添加负面效果
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        }

        // 如果是生存模式，消耗一个瓶子，并尝试返还一个空瓶
        if (pLivingEntity instanceof Player player && !player.getAbilities().instabuild) {
            pStack.shrink(1); // 消耗当前物品
            if (!player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE))) {
                // 如果背包满了，就把空瓶扔在地上
                player.drop(new ItemStack(Items.GLASS_BOTTLE), false);
            }
        }

        return pStack.isEmpty() ? ItemStack.EMPTY : pStack;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pUsedHand);
    }
}