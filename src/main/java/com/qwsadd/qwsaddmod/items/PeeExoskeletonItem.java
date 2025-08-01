package com.qwsadd.qwsaddmod.items;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.core.ModArmorMaterials;
import com.qwsadd.qwsaddmod.network.PeeLevelPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PeeExoskeletonItem extends ArmorItem {

    private static final String ARMOR_TEXTURE = QwsaddModMain.MODID + ":textures/entity/armor/pee_exoskeleton_layer_2.png";

    public PeeExoskeletonItem(Properties pProperties) {
        // 注意这里是 Type.LEGGINGS
        super(ModArmorMaterials.POOP, Type.LEGGINGS, pProperties);
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return ARMOR_TEXTURE;
    }

    // ... (文件顶部的 import 保持不变)

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        // 确保逻辑只在服务端运行
        if (level.isClientSide()) {
            return;
        }

        // 检查玩家是否穿着这个外骨骼
        if (player.getItemBySlot(EquipmentSlot.LEGS).is(this)) {
            player.getCapability(QwsaddModMain.PEE_CAPABILITY).ifPresent(peeCap -> {
                // 检查尿意值是否足够提供常规效果
                if (peeCap.getPeeLevel() >= 10) {
                    // 持续消耗尿意值
                    if (player.tickCount % 40 == 0) { // 每2秒消耗1点
                        peeCap.setPeeLevel(peeCap.getPeeLevel() - 1);
                        // 同步到客户端
                        if (player instanceof ServerPlayer serverPlayer) {
                            QwsaddModMain.NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PeeLevelPacket(peeCap.getPeeLevel()));
                        }
                    }

                    // 提供常规增益效果
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 42, 0, false, false, true));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 42, 1, false, false, true));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 42, 0, false, false, true));
                }
                // 【已移除】紧急保命逻辑被移动到了 PlayerEvents.java 中
            });
        }
    }

    // ... (healToPercentage 辅助方法保持不变)
    // ==================== ↓↓↓ 新增辅助方法：修复补血逻辑 ↓↓↓ ====================
    /**
     * 将玩家的生命值恢复到其最大生命值的特定百分比以上
     * @param player 要治疗的玩家
     * @param targetPercentage 目标生命值百分比 (例如 0.25f 代表 25%)
     */
    private void healToPercentage(Player player, float targetPercentage) {
        float maxHealth = player.getMaxHealth();
        float currentHealth = player.getHealth();
        float targetHealth = maxHealth * targetPercentage;

        // 如果当前血量已经高于目标血量，则不进行治疗
        if (currentHealth >= targetHealth) {
            return;
        }

        // 计算需要恢复的血量
        float healthToHeal = targetHealth - currentHealth;

        // 执行治疗
        player.heal(healthToHeal);
    }
    // ==================== ↑↑↑ 新增辅助方法结束 ↑↑↑ ====================
    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        // 添加第一行描述：常规效果
        pTooltipComponents.add(Component.translatable("tooltip.qwsaddmod.pee_exoskeleton.passive_effects")
                .withStyle(ChatFormatting.GRAY));

        // 添加第二行描述：紧急保命效果
        pTooltipComponents.add(Component.translatable("tooltip.qwsaddmod.pee_exoskeleton.emergency_save")
                .withStyle(ChatFormatting.GOLD));

        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
