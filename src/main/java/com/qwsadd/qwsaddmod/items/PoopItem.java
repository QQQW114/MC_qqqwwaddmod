package com.qwsadd.qwsaddmod.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PoopItem extends Item {

    // 定义食物属性
    private static final FoodProperties FOOD_PROPERTIES = new FoodProperties.Builder()
            .nutrition(1) // 恢复1点饥饿值 (半个鸡腿)
            .saturationMod(0.1F) // 恢复极少量饱和度
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 200, 0), 1.0F) // 100%几率获得10秒(200ticks)中毒I
            .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 100, 0), 1.0F) // 100%几率获得5秒(100ticks)反胃
            .alwaysEat() // 即使饥饿值是满的也能吃
            .build();

    public PoopItem(Properties properties) {
        // 将食物属性应用到物品上
        super(properties.food(FOOD_PROPERTIES));
    }

    // 重写这个方法来添加自定义的物品提示
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);

        // 检查物品是否有NBT数据，并且NBT里是否有"OwnerName"这个键
        if (pStack.hasTag() && pStack.getTag().contains("OwnerName")) {
            String ownerName = pStack.getTag().getString("OwnerName");
            // 添加 "这是 xxx 拉的大便" 的提示，并设置为灰色
            pTooltipComponents.add(Component.translatable("tooltip.qwsaddmod.poop.owner", ownerName)
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}