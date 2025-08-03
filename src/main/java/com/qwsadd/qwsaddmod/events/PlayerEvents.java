package com.qwsadd.qwsaddmod.events;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.blocks.SquatToiletBlock;
import com.qwsadd.qwsaddmod.core.ModDamageTypes;
import com.qwsadd.qwsaddmod.init.BlockInit;
import com.qwsadd.qwsaddmod.init.ItemInit;
import com.qwsadd.qwsaddmod.entity.PoopEntity;
import com.qwsadd.qwsaddmod.network.PeeLevelPacket;
import com.qwsadd.qwsaddmod.network.PlayTotemEffectPacket; // 新增 import
import com.qwsadd.qwsaddmod.network.PoopLevelPacket;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes; // 新增 import
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel; // 新增 import
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance; // 新增 import
import net.minecraft.world.effect.MobEffects; // 新增 import
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent; // 新增 import
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraft.world.entity.decoration.ArmorStand;
import com.qwsadd.qwsaddmod.entity.SeatEntity; // 新增 import

public class PlayerEvents {

    private static final int PASSIVE_PEE_INTERVAL = 1200;

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncPlayerCapabilities(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncPlayerCapabilities(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncPlayerCapabilities(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }
        ServerPlayer player = (ServerPlayer) event.player;

        // ==================== ↓↓↓ 新增：周期性同步 (你的思路) ↓↓↓ ====================
        // 每5秒 (100 tick) 同步一次数据，作为最终的保险
        if (player.tickCount % 100 == 0) {
            syncPlayerCapabilities(player);
        }
        // ==================== ↑↑↑ 新增代码结束 ↑↑↑ ====================

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
        // 【核心修正】修改蹲坑检测逻辑
        // 【核心修正】修改蹲坑检测逻辑
        if (player.getVehicle() instanceof SeatEntity) {
            BlockPos pos = player.getVehicle().blockPosition();
            handleSquatting(player, pos);
        }

        if (player.isCreative() || player.isSpectator()) {
            return;
        }
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

    public static void triggerIncontinencePenalty(ServerPlayer player, String type) {
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
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // 1. 检查受伤的是否是玩家，且在服务端
        if (!(event.getEntity() instanceof ServerPlayer player) || player.level().isClientSide) {
            return;
        }

        // 2. 检查玩家是否穿着外骨骼
        if (player.getItemBySlot(EquipmentSlot.LEGS).is(ItemInit.PEE_EXOSKELETON.get())) {
            // 3. 检查这次伤害是否会致命
            if (player.getHealth() - event.getAmount() < 1.0F) {
                // 4. 检查尿意值是否足够
                player.getCapability(QwsaddModMain.PEE_CAPABILITY).ifPresent(peeCap -> {
                    if (peeCap.getPeeLevel() >= 40) {
                        // 5. 取消本次伤害事件！
                        event.setCanceled(true);
                        // 6. 触发保命效果
                        triggerExoskeletonSave(player, peeCap);
                    }
                });
            }
        }
    }

    /**
     * 触发外骨骼保命效果的公共方法
     * @param player 玩家
     * @param peeCap 玩家的尿意值 Capability
     */
    private static void triggerExoskeletonSave(ServerPlayer player, com.qwsadd.qwsaddmod.capability.IPeeCapability peeCap) {
        // 消耗40点尿意
        peeCap.setPeeLevel(peeCap.getPeeLevel() - 40);
        // 同步尿意值到客户端
        QwsaddModMain.NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PeeLevelPacket(peeCap.getPeeLevel()));

        // 提供强大的增益效果
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 4, false, true, true)); // 10秒的抗性 V
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 2, false, true, true)); // 5秒的生命恢复 III

        // 将生命值设置为最大生命值的 25%
        player.setHealth(player.getMaxHealth() * 0.25f);

        // 在服务端生成粒子效果
        player.serverLevel().sendParticles(ParticleTypes.DRIPPING_HONEY, player.getX(), player.getY() + 1, player.getZ(), 50, 0.5, 0.5, 0.5, 0.1);

        // 发送数据包，让客户端播放不死图腾特效
        QwsaddModMain.NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PlayTotemEffectPacket());
    }

    // ==================== ↑↑↑ 新增代码结束 ↑↑↑ ====================
    // ==================== ↓↓↓ 新增：公共同步方法 ↓↓↓ ====================
    /**
     * 将玩家的大小便数据从服务端同步到客户端
     * @param player 需要同步数据的玩家
     */
    private static void syncPlayerCapabilities(ServerPlayer player) {
        player.getCapability(QwsaddModMain.POOP_CAPABILITY).ifPresent(cap -> {
            QwsaddModMain.NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PoopLevelPacket(cap.getPoopLevel()));
        });
        player.getCapability(QwsaddModMain.PEE_CAPABILITY).ifPresent(cap -> {
            QwsaddModMain.NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PeeLevelPacket(cap.getPeeLevel()));
        });
    }
    // ==================== ↑↑↑ 新增代码结束 ↑↑↑ ====================
    // ... (在 syncPlayerCapabilities 方法之后)

    // ==================== ↓↓↓ 新增：蹲坑处理方法 ↓↓↓ ====================
    private static void handleSquatting(ServerPlayer player, BlockPos pos) {
        // 每秒检查一次
        if (player.tickCount % 20 != 0) {
            return;
        }

        BlockState blockState = player.level().getBlockState(pos);
        int currentLevel = blockState.getValue(SquatToiletBlock.LEVEL);

        // 如果蹲坑满了，就停止
        if (currentLevel >= SquatToiletBlock.MAX_LEVEL) {
            return;
        }

        final boolean[] poopReduced = {false};
        player.getCapability(QwsaddModMain.POOP_CAPABILITY).ifPresent(poopCap -> {
            if (poopCap.getPoopLevel() > 0) {
                poopCap.setPoopLevel(poopCap.getPoopLevel() - 1);
                poopReduced[0] = true;
                // 每消耗10点便意，增加一级堆肥
                if (poopCap.getPoopLevel() % 10 == 0) {
                    player.level().setBlock(pos, blockState.setValue(SquatToiletBlock.LEVEL, currentLevel + 1), 3);
                    player.level().playSound(null, pos, SoundEvents.COMPOSTER_FILL_SUCCESS, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
        });

        player.getCapability(QwsaddModMain.PEE_CAPABILITY).ifPresent(peeCap -> {
            if (peeCap.getPeeLevel() > 0) {
                peeCap.setPeeLevel(peeCap.getPeeLevel() - 1);
            }
        });

        // 如果消耗了任何东西，就同步
        if (poopReduced[0]) {
            syncPlayerCapabilities(player);
        }
    }
// ==================== ↑↑↑ 新增代码结束 ↑↑↑ ====================
}
