// 文件路径: com/qwsadd/qwsaddmod/client/ClientSetup.java
package com.qwsadd.qwsaddmod.client;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.client.renderer.PeeModel;
import com.qwsadd.qwsaddmod.client.renderer.PeeRenderer;
import com.qwsadd.qwsaddmod.client.renderer.PoopModel;
import com.qwsadd.qwsaddmod.client.renderer.PoopRenderer;
import com.qwsadd.qwsaddmod.init.EntityInit;
import com.qwsadd.qwsaddmod.network.PeeActionPacket;
import com.qwsadd.qwsaddmod.network.PoopActionPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import com.qwsadd.qwsaddmod.menu.ModMenuTypes;
import com.qwsadd.qwsaddmod.screen.PoopFermenterScreen; // 新增 import
import net.minecraft.client.gui.screens.MenuScreens; // 新增 import

@Mod.EventBusSubscriber(modid = QwsaddModMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    // 【修复】将按键监听器从 FMLClientSetupEvent 移到构造函数或静态初始化块中
    // 但更好的做法是直接在 FMLClientSetupEvent 中注册到 FORGE 总线
    public static void init(final FMLClientSetupEvent event) {
        // 【修复】将按键监听器注册到 FORGE 事件总线，而不是 MOD 事件总线

        MinecraftForge.EVENT_BUS.register(ClientSetup.class);
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.POOP_FERMENTER_MENU.get(), PoopFermenterScreen::new);
        });
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.POOP_KEY);
        event.register(KeyBindings.PEE_KEY);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.POOP_ENTITY.get(), PoopRenderer::new);
        event.registerEntityRenderer(EntityInit.PEE_ENTITY.get(), PeeRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(PoopModel.LAYER_LOCATION, PoopModel::createBodyLayer);
        // 【修复】确保 PeeModel 也被注册
        event.registerLayerDefinition(PeeModel.LAYER_LOCATION, PeeModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
        // 【修复】使用新的 ExcretionLevelOverlay 类
        event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), "excretion_level", ExcretionLevelOverlay::render);
    }

    // 【修复】这个监听器需要注册到 FORGE 事件总线，所以需要一个不同的注解
    @Mod.EventBusSubscriber(modid = QwsaddModMain.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ForgeBusEvents {
        @SubscribeEvent
        public static void onKeyInput(final InputEvent.Key event) {
            if (KeyBindings.POOP_KEY.consumeClick()) {
                QwsaddModMain.NETWORK_CHANNEL.sendToServer(new PoopActionPacket());
            }
            if (KeyBindings.PEE_KEY.consumeClick()) {
                QwsaddModMain.NETWORK_CHANNEL.sendToServer(new PeeActionPacket());
            }
        }
    }
}
