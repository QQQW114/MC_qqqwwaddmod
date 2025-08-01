package com.qwsadd.qwsaddmod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.menu.PoopFermenterMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PoopFermenterScreen extends AbstractContainerScreen<PoopFermenterMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(QwsaddModMain.MODID, "textures/gui/poop_fermenter_gui.png");

    public PoopFermenterScreen(PoopFermenterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void init() {
        super.init();
        // 这两行代码的作用是隐藏原版的 "Inventory" 和方块标题，如果你的GUI贴图上已经画了标题，这就很有用
        // 如果你想显示标题，可以注释掉它们
        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        if(menu.isCrafting()) {
            // 这里的 176, 0 是进度条在贴图文件 (poop_fermenter_gui.png) 上的起始坐标 (u, v)
            // 这里的 17 是进度条的高度
            guiGraphics.blit(TEXTURE, x + 80, y + 35, 176, 0, menu.getScaledProgress(), 17);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        // 【核心修正】调用1.20.1版本的 renderBackground 方法
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
