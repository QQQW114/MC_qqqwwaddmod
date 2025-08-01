package com.qwsadd.qwsaddmod.effects;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.events.PlayerEvents; // 新增 import
import com.qwsadd.qwsaddmod.network.PoopLevelPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;

public class DiarrheaEffect extends MobEffect {
    public DiarrheaEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (!pLivingEntity.level().isClientSide() && pLivingEntity instanceof ServerPlayer player) {
            player.getCapability(QwsaddModMain.POOP_CAPABILITY).ifPresent(poopCap -> {
                // 增加10点便意值
                poopCap.addPoopLevel(10);

                // 【核心新增】检查是否已满，并触发惩罚
                if (poopCap.isPoopFull()) {
                    // 调用 PlayerEvents 中的公共惩罚方法
                    PlayerEvents.triggerIncontinencePenalty(player, "poop");
                } else {
                    // 如果没满，才同步数值（因为惩罚方法自己会同步）
                    QwsaddModMain.NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PoopLevelPacket(poopCap.getPoopLevel()));
                }
            });
        }
        super.applyEffectTick(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        // 【核心修正】让此方法每秒 (20 tick) 执行一次
        // pDuration 是效果剩余的 tick 数
        return pDuration % 20 == 0;
    }
}
