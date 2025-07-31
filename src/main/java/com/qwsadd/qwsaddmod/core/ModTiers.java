package com.qwsadd.qwsaddmod.core;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.init.BlockInit; // 新增 import
import com.qwsadd.qwsaddmod.init.ItemInit;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.TierSortingRegistry;

import java.util.List;

public class ModTiers {
    public static final Tier POOP = TierSortingRegistry.registerTier(
            // 创建一个新的 ForgeTier 实例
            new ForgeTier(
                    2,                              // 挖掘等级 (2 = 铁级, 可以挖钻石)
                    Tiers.IRON.getUses(),           // 耐久度 (使用铁工具的耐久度: 250)
                    Tiers.GOLD.getSpeed(),          // 挖掘速度 (使用金工具的速度: 12.0F)
                    15.0f,                           // 攻击伤害加成 我设置了更高的伤害
                    15,                             // 附魔能力 (和铁一样: 15)
                    // 方块标签，用于决定此工具可以高效挖掘哪些方块
                    net.minecraft.tags.BlockTags.NEEDS_IRON_TOOL,
                    // 修复此工具所需的材料，这里修改为“大便块”
                    () -> Ingredient.of(BlockInit.POOP_BLOCK.get()) // <-- 已修改
            ),


            // 新 Tier 的唯一标识符
            ResourceLocation.fromNamespaceAndPath(QwsaddModMain.MODID, "poop"),
            // 定义此 Tier 在排序中的位置，我们让它在铁(IRON)之后
            List.of(Tiers.IRON),
            // 定义此 Tier 在排序中应该在哪些 Tier 之前，这里为空
            List.of()

    );

}
