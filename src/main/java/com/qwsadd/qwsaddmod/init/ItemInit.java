package com.qwsadd.qwsaddmod.init;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.core.ModTiers; // 新增 import
import com.qwsadd.qwsaddmod.items.*;
import net.minecraft.world.item.AxeItem; // 新增 import
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, QwsaddModMain.MODID);

    // 原有的 Poop Item
    public static final RegistryObject<Item> POOP_ITEM = ITEMS.register("poop",
            () -> new PoopItem(new Item.Properties())
    );

    // 【新增】注册“尿液桶”
    public static final RegistryObject<Item> PEE_BUCKET = ITEMS.register("pee_bucket",
            () -> new PeeBucketItem(new Item.Properties())
    );

    // 【新增】注册“一瓶尿”
    public static final RegistryObject<Item> BOTTLE_OF_PEE = ITEMS.register("bottle_of_pee",
            () -> new BottleOfPeeItem(new Item.Properties())
    );

    // ==================== ↓↓↓ 新增代码开始 ↓↓↓ ====================
    public static final RegistryObject<Item> POOP_AXE = ITEMS.register("poop_axe",
            () -> new AxeItem(ModTiers.POOP, 5.0F, -3.1F, new Item.Properties())
    );
    public static final RegistryObject<Item> POOP_PICKAXE = ITEMS.register("poop_pickaxe",
            () -> new PickaxeItem(ModTiers.POOP, 1, -2.8F, new Item.Properties())
    );
    public static final RegistryObject<Item> POOP_SWORD = ITEMS.register("poop_sword",
            () -> new PoopSwordItem(ModTiers.POOP, 3, -2.4F, new Item.Properties())
    );
    public static final RegistryObject<Item> POOP_JETPACK = ITEMS.register("poop_jetpack",
            () -> new PoopJetpackItem(new Item.Properties())
    );
    public static final RegistryObject<Item> POOP_HOE = ITEMS.register("poop_hoe",
            () -> new PoopHoeItem(ModTiers.POOP, -2, -1.0F, new Item.Properties())
    );
    // ==================== ↑↑↑ 新增代码结束 ↑↑↑ ====================
}
