package com.qwsadd.qwsaddmod.events;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.core.ModDamageTypes;
// 【重大修复】导入正确的物品注册类 ItemInit，它在 init 包下
import com.qwsadd.qwsaddmod.init.ItemInit;
import com.qwsadd.qwsaddmod.entity.PoopEntity;
import com.qwsadd.qwsaddmod.network.PeeLevelPacket;
import com.qwsadd.qwsaddmod.network.PoopLevelPacket;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

public class PlayerEvents {

    private static final int PASSIVE_PEE_INTERVAL = 1200;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }
        ServerPlayer player = (ServerPlayer) event.player;
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        // ==================== ↓↓↓ 新增代码开始 ↓↓↓ ====================
        // 喷气背包安全检查
        // 检查玩家是否穿着喷气背包
        boolean isWearingJetpack = player.getItemBySlot(EquipmentSlot.CHEST).is(ItemInit.POOP_JETPACK.get());

        // 如果玩家没有穿喷气背包，但他的mayfly能力是true（即可以飞行）
        if (!isWearingJetpack && player.getAbilities().mayfly) {
            // 强制关闭飞行能力
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            // 同步能力到客户端
            player.onUpdateAbilities();
        }
        // ==================== ↑↑↑ 新增代码结束 ↑↑↑ ====================
    }

    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.level().isClientSide) {
            return;
        }
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        ItemStack usedItem = event.getItem();
        Item item = usedItem.getItem();

        // 【重大修复】使用正确的物品变量名 POOP_ITEM
        if (item == ItemInit.POOP_ITEM.get()) {
            grantAdvancementIfNotDone(player, "eat_poop");
        }
        // 【重大修复】使用正确的物品变量名 PEE_BUCKET 和 BOTTLE_OF_PEE
        if (item == ItemInit.PEE_BUCKET.get() || item == ItemInit.BOTTLE_OF_PEE.get()) {
            grantAdvancementIfNotDone(player, "drink_pee");
        }

        if (usedItem.getUseAnimation() == UseAnim.DRINK) {
            player.getCapability(QwsaddModMain.PEE_CAPABILITY).ifPresent(peeCap -> {
                peeCap.addPeeLevel(20);
                if (peeCap.isPeeFull()) {
                    triggerIncontinencePenalty(player, "pee");
                } else {
                    QwsaddModMain.NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PeeLevelPacket(peeCap.getPeeLevel()));
                }
            });
        }

        if (usedItem.isEdible()) {
            player.getCapability(QwsaddModMain.POOP_CAPABILITY).ifPresent(poopCap -> {
                poopCap.addPoopLevel(15);
                if (poopCap.isPoopFull()) {
                    triggerIncontinencePenalty(player, "poop");
                } else {
                    QwsaddModMain.NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PoopLevelPacket(poopCap.getPoopLevel()));
                }
            });
            player.getCapability(QwsaddModMain.PEE_CAPABILITY).ifPresent(peeCap -> {
                peeCap.addPeeLevel(15);
                if (peeCap.isPeeFull()) {
                    triggerIncontinencePenalty(player, "pee");
                } else {
                    QwsaddModMain.NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PeeLevelPacket(peeCap.getPeeLevel()));
                }
            });
        }
    }

    private static void triggerIncontinencePenalty(ServerPlayer player, String type) {
        DamageSource damageSource = ModDamageTypes.incontinence(player);
        Component message;

        if ("pee".equals(type)) {
            grantAdvancementIfNotDone(player, "pee_incontinence");
            player.playSound(SoundEvents.PLAYER_SPLASH, 1.0F, 1.0F);
            player.getCapability(QwsaddModMain.PEE_CAPABILITY).ifPresent(cap -> {
                cap.setPeeLevel(0);
                QwsaddModMain.NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PeeLevelPacket(0));
            });
            player.hurt(damageSource, player.getMaxHealth() * 0.75f);
            message = Component.translatable("message.qwsaddmod.pee_incontinence_broadcast", player.getDisplayName());

        } else if ("poop".equals(type)) {
            grantAdvancementIfNotDone(player, "poop_incontinence");
            PoopEntity poopEntity = new PoopEntity(player.level(), player.getX(), player.getY(), player.getZ());
            poopEntity.setOwnerName(player.getGameProfile().getName());
            player.level().addFreshEntity(poopEntity);
            player.playSound(SoundEvents.SLIME_SQUISH, 1.0F, 0.8F);
            player.getCapability(QwsaddModMain.POOP_CAPABILITY).ifPresent(cap -> {
                cap.setPoopLevel(0);
                QwsaddModMain.NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PoopLevelPacket(0));
            });
            player.hurt(damageSource, player.getMaxHealth() * 0.9f);
            message = Component.translatable("message.qwsaddmod.poop_incontinence_broadcast", player.getDisplayName());
        } else {
            return;
        }

        if (player.getServer() != null) {
            player.getServer().getPlayerList().broadcastSystemMessage(message, false);
        }
    }

    private static void grantAdvancementIfNotDone(ServerPlayer player, String advancementId) {
        if (player.getServer() == null) return;

        ResourceLocation res = ResourceLocation.fromNamespaceAndPath(QwsaddModMain.MODID, advancementId);
        Advancement advancement = player.getServer().getAdvancements().getAdvancement(res);

        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) {
                for (String criterion : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancement, criterion);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            Player originalPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();
            originalPlayer.getCapability(QwsaddModMain.POOP_CAPABILITY).ifPresent(oldCap -> newPlayer.getCapability(QwsaddModMain.POOP_CAPABILITY).ifPresent(newCap -> newCap.deserializeNBT(oldCap.serializeNBT())));
            originalPlayer.getCapability(QwsaddModMain.PEE_CAPABILITY).ifPresent(oldCap -> newPlayer.getCapability(QwsaddModMain.PEE_CAPABILITY).ifPresent(newCap -> newCap.deserializeNBT(oldCap.serializeNBT())));
        }
    }
}
