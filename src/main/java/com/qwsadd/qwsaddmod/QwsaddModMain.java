// 文件路径: com/qwsadd/qwsaddmod/QwsaddModMain.java
package com.qwsadd.qwsaddmod;

import com.qwsadd.qwsaddmod.blockentity.ModBlockEntities;
import com.qwsadd.qwsaddmod.capability.IPeeCapability;
import com.qwsadd.qwsaddmod.capability.IPoopCapability;
import com.qwsadd.qwsaddmod.capability.PeeCapabilityProvider;
import com.qwsadd.qwsaddmod.capability.PoopCapabilityProvider;
import net.minecraftforge.fml.DistExecutor;
import com.qwsadd.qwsaddmod.events.PlayerEvents;
import com.qwsadd.qwsaddmod.init.EntityInit;
import com.qwsadd.qwsaddmod.init.ItemInit;
import com.qwsadd.qwsaddmod.menu.ModMenuTypes;
import com.qwsadd.qwsaddmod.network.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import com.qwsadd.qwsaddmod.init.BlockInit; // 新增 import
import com.qwsadd.qwsaddmod.init.EntityInit;
import com.qwsadd.qwsaddmod.init.ItemInit;
import com.qwsadd.qwsaddmod.network.PlayTotemEffectPacket;
import com.qwsadd.qwsaddmod.effects.ModEffects;
import com.qwsadd.qwsaddmod.client.ClientPacketHandler;
import net.minecraftforge.api.distmarker.Dist;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraftforge.network.NetworkEvent;


@Mod(QwsaddModMain.MODID)
public class QwsaddModMain {
    public static final String MODID = "qwsaddmod";

    public static final SimpleChannel NETWORK_CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(MODID, "main"), () -> "1", "1"::equals, "1"::equals);

    public static final Capability<IPoopCapability> POOP_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<IPeeCapability> PEE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    public static final ResourceLocation POOP_CAPABILITY_ID = ResourceLocation.fromNamespaceAndPath(MODID, "poop_capability");
    public static final ResourceLocation PEE_CAPABILITY_ID = ResourceLocation.fromNamespaceAndPath(MODID, "pee_capability");

    // 【已修正】只保留需要的 DeferredRegister
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    // 【已删除】不再需要 DeferredRegister 来注册伤害类型，因为它是由 JSON 文件数据驱动的

    public static final RegistryObject<CreativeModeTab> QWSADD_TAB = CREATIVE_MODE_TABS.register("qwsadd_tab",
            () -> CreativeModeTab.builder().title(Component.translatable("creativetab." + MODID))
                    .icon(() -> new ItemStack(ItemInit.POOP_ITEM.get()))
                    .displayItems((params, output) -> {
                        output.accept(ItemInit.POOP_ITEM.get());
                        output.accept(ItemInit.FERMENTED_POOP.get()); // <-- 新增
                        output.accept(ItemInit.POOP_CORE.get());// <-- 新增
                        output.accept(ItemInit.HARDENED_POOP.get());
                        output.accept(BlockInit.SQUAT_TOILET.get());
                        output.accept(BlockInit.POOP_BLOCK.get());
                        output.accept(BlockInit.POOP_FERMENTER.get());
                        output.accept(ItemInit.POOP_AXE.get());
                        output.accept(ItemInit.POOP_PICKAXE.get());
                        output.accept(ItemInit.POOP_SWORD.get());
                        output.accept(ItemInit.POOP_HOE.get());
                        output.accept(ItemInit.POOP_FEEDER.get());
                        output.accept(ItemInit.PEE_FEEDER.get());
                        output.accept(ItemInit.POOP_JETPACK.get());
                        output.accept(ItemInit.PEE_EXOSKELETON.get());
                        output.accept(ItemInit.PEE_BUCKET.get());
                        output.accept(ItemInit.BOTTLE_OF_PEE.get());
                        output.accept(ItemInit.POOP_STAFF.get());
                        output.accept(ItemInit.POOP_CAKE.get());
                    }).build());

    public QwsaddModMain() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册我们所有的 DeferredRegister (通用代码)
        ItemInit.ITEMS.register(modEventBus);
        EntityInit.ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        BlockInit.BLOCKS.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModEffects.register(modEventBus);
        // 通用设置事件监听
        modEventBus.addListener(this::onCommonSetup);


// 【核心修正】使用 DistExecutor 来分离客户端和服务端逻辑
        DistExecutor.unsafeRunWhenOn(net.minecraftforge.api.distmarker.Dist.CLIENT, () -> () -> {
            // 这里的代码只会在客户端运行
            modEventBus.addListener(com.qwsadd.qwsaddmod.client.ClientSetup::init);
        });

        // 通用事件总线注册
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(PlayerEvents.class);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            int id = 0;

            // 客户端 -> 服务端 的数据包 (这些是安全的，保持不变)
            NETWORK_CHANNEL.registerMessage(id++, PoopActionPacket.class, PoopActionPacket::encode, PoopActionPacket::decode, PoopActionPacket::handle);
            NETWORK_CHANNEL.registerMessage(id++, PeeActionPacket.class, PeeActionPacket::encode, PeeActionPacket::decode, PeeActionPacket::handle);

            // 服务端 -> 客户端 的数据包 (使用新的注册方式)
            NETWORK_CHANNEL.registerMessage(id++, PoopLevelPacket.class, PoopLevelPacket::toBytes, PoopLevelPacket::new,
                    (BiConsumer<PoopLevelPacket, Supplier<NetworkEvent.Context>>) DistExecutor.safeRunForDist(() -> ClientPacketHandler::getPoopLevelConsumer, () -> null));

            NETWORK_CHANNEL.registerMessage(id++, PeeLevelPacket.class, PeeLevelPacket::toBytes, PeeLevelPacket::new,
                    (BiConsumer<PeeLevelPacket, Supplier<NetworkEvent.Context>>) DistExecutor.safeRunForDist(() -> ClientPacketHandler::getPeeLevelConsumer, () -> null));

            NETWORK_CHANNEL.registerMessage(id++, PlayPeeAnimationPacket.class, PlayPeeAnimationPacket::toBytes, PlayPeeAnimationPacket::new,
                    (BiConsumer<PlayPeeAnimationPacket, Supplier<NetworkEvent.Context>>) DistExecutor.safeRunForDist(() -> ClientPacketHandler::getPlayPeeAnimationConsumer, () -> null));

            NETWORK_CHANNEL.registerMessage(id++, PlayTotemEffectPacket.class, PlayTotemEffectPacket::toBytes, PlayTotemEffectPacket::new,
                    (BiConsumer<PlayTotemEffectPacket, Supplier<NetworkEvent.Context>>) DistExecutor.safeRunForDist(() -> ClientPacketHandler::getPlayTotemEffectConsumer, () -> null));
        });
    }

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(POOP_CAPABILITY_ID, new PoopCapabilityProvider());
            event.addCapability(PEE_CAPABILITY_ID, new PeeCapabilityProvider());
        }
    }
}
