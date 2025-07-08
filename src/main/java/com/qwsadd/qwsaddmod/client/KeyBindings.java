package com.qwsadd.qwsaddmod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_U;

// 导入GLFW来使用按键常量，例如 GLFW.GLFW_KEY_V
import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;

public class KeyBindings {
    // 按键的分类名称，会显示在控制菜单里
    public static final String KEY_CATEGORY_QWSADD = "key.categories.qwsaddmod";

    // 定义我们的“排泄”按键
    public static final KeyMapping POOP_KEY = new KeyMapping(
            "key.qwsaddmod.poop", // 按键的唯一名称，用于语言文件
            KeyConflictContext.IN_GAME, // 这个按键只在游戏中生效
            InputConstants.Type.KEYSYM, // 按键类型，这里指键盘
            GLFW_KEY_V,                 // 默认绑定的键（V键）
            KEY_CATEGORY_QWSADD         // 按键所属的分类
    );
    public static final KeyMapping PEE_KEY = new KeyMapping(
            "key.qwsaddmod.pee",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW_KEY_U, // 默认为 U 键
            KEY_CATEGORY_QWSADD
    );
}