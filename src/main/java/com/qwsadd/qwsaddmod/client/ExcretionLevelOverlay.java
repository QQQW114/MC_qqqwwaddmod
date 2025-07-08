// 文件路径: com/qwsadd/qwsaddmod/client/ExcretionLevelOverlay.java (注意文件名已改)
package com.qwsadd.qwsaddmod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;

// 【修复】重命名并重构此类以显示两个条
public class ExcretionLevelOverlay {

    public static void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTicks, int screenWidth, int screenHeight) {
        var player = Minecraft.getInstance().player;
        if (player == null || player.isCreative() || player.isSpectator()) {
            return;
        }

        // --- 绘制便意条 (棕色) ---
        int barMaxWidth = 81;
        int barHeight = 2;
        int x = screenWidth / 2 + 10;
        int yPoop = screenHeight - 39 - 3; // 在饥饿条上方

        int poopLevel = ClientPoopLevelData.getClientPoopLevel();
        int maxPoopLevel = 100;

        RenderSystem.enableBlend();
        // 背景
        guiGraphics.fill(x, yPoop, x + barMaxWidth, yPoop + barHeight, 0x80333333);
        // 前景
        int filledPoopWidth = (int) (((float) poopLevel / maxPoopLevel) * barMaxWidth);
        guiGraphics.fillGradient(x, yPoop, x + filledPoopWidth, yPoop + barHeight, 0xFF6B2503, 0xFF8B4513);

        // --- 绘制尿意条 (黄色) ---
        int yPee = yPoop - barHeight - 1; // 在便意条上方

        int peeLevel = ClientPeeLevelData.getClientPeeLevel(); // 【新增】获取尿意值
        int maxPeeLevel = 100;

        // 背景
        guiGraphics.fill(x, yPee, x + barMaxWidth, yPee + barHeight, 0x80333333);
        // 前景
        int filledPeeWidth = (int) (((float) peeLevel / maxPeeLevel) * barMaxWidth);
        guiGraphics.fillGradient(x, yPee, x + filledPeeWidth, yPee + barHeight, 0xFFFFFF00, 0xFFFFD700);

        RenderSystem.disableBlend();
    }
}
