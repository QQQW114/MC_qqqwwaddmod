package com.qwsadd.qwsaddmod.items;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.core.ModArmorMaterials;
import com.qwsadd.qwsaddmod.network.PoopLevelPacket;
import net.minecraft.core.particles.ParticleTypes; // 新增 import
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3; // 新增 import
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class PoopJetpackItem extends ArmorItem {

    private static final String ARMOR_TEXTURE = QwsaddModMain.MODID + ":textures/entity/armor/poop_jetpack_layer_1.png";

    public PoopJetpackItem(Properties pProperties) {
        super(ModArmorMaterials.POOP, Type.CHESTPLATE, pProperties);
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return ARMOR_TEXTURE;
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        // 检查玩家是否穿着这个喷气背包
        if (player.getItemBySlot(EquipmentSlot.CHEST).is(this)) {
            // --- 服务端逻辑 ---
            if (!level.isClientSide()) {
                player.getCapability(QwsaddModMain.POOP_CAPABILITY).ifPresent(poopCap -> {
                    if (poopCap.getPoopLevel() >= 1) {
                        player.getAbilities().mayfly = true;
                        if (player.getAbilities().flying) {
                            if (player.tickCount % 20 == 0) {
                                int currentLevel = poopCap.getPoopLevel();
                                poopCap.setPoopLevel(currentLevel - 1);
                                if (player instanceof ServerPlayer serverPlayer) {
                                    QwsaddModMain.NETWORK_CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new PoopLevelPacket(poopCap.getPoopLevel()));
                                }
                            }
                        }
                    } else {
                        if (!player.isCreative() && !player.isSpectator()) {
                            player.getAbilities().mayfly = false;
                            player.getAbilities().flying = false;
                        }
                    }
                    player.onUpdateAbilities();
                });
            }
            // --- 客户端逻辑 ---
            else {
                // ==================== ↓↓↓ 新增代码：添加粒子效果 ↓↓↓ ====================
                // 检查玩家是否正在飞行
                if (player.getAbilities().flying) {
                    // 获取玩家的位置
                    Vec3 playerPos = player.position();
                    // 在玩家身后稍微偏下的位置生成粒子
                    double x = playerPos.x();
                    double y = playerPos.y() + 1.0; // 在玩家身体中部偏上的位置
                    double z = playerPos.z();

                    // 随机生成一些粒子，让效果更自然
                    for (int i = 0; i < 3; i++) {
                        // 在玩家周围一个小范围内随机偏移
                        double randomX = x + (level.random.nextDouble() - 0.5) * 0.4;
                        double randomY = y + (level.random.nextDouble() - 0.5) * 0.4;
                        double randomZ = z + (level.random.nextDouble() - 0.5) * 0.4;

                        // 添加粒子，参数分别是：粒子类型，x, y, z坐标，x, y, z轴速度
                        // 这里我们让粒子稍微向下喷射
                        level.addParticle(ParticleTypes.SNEEZE, randomX, randomY, randomZ, 0, -0.2, 0);
                    }
                }
                // ==================== ↑↑↑ 新增代码结束 ↑↑↑ ====================
            }
        }
    }
}
