package com.qwsadd.qwsaddmod.items;

import com.qwsadd.qwsaddmod.effects.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class PoopCakeItem extends Item {
    public PoopCakeItem(Properties pProperties) {
        super(pProperties);
    }

    // 定义食物属性
    public static final FoodProperties FOOD_PROPERTIES = new FoodProperties.Builder()
            .nutrition(2) // 恢复2点饥饿值
            .saturationMod(0.1f) // 少量饱和度
            .alwaysEat() // 饱了也能吃
            // 核心：吃完后给予10秒（200 tick）的腹泻效果
            .effect(() -> new MobEffectInstance(ModEffects.DIARRHEA.get(), 200, 0), 1.0f)
            .build();
}
