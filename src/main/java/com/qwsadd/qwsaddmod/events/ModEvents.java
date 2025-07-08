// 文件路径: com/qwsadd/qwsaddmod/events/ModEvents.java
package com.qwsadd.qwsaddmod.events;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.capability.IPeeCapability;
import com.qwsadd.qwsaddmod.capability.IPoopCapability;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// 【修复】我们只在这里处理 Capability 的注册事件
// 网络包注册已经在 QwsaddModMain 中正确处理了
@Mod.EventBusSubscriber(modid = QwsaddModMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    // 【修复】移除 onCommonSetup 方法，因为它与 QwsaddModMain 中的逻辑重复

    @SubscribeEvent
    public static void onRegisterCapabilities(final RegisterCapabilitiesEvent event) {
        event.register(IPoopCapability.class);
        event.register(IPeeCapability.class);
    }
}
