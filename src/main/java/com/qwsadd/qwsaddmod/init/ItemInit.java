package com.qwsadd.qwsaddmod.init;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.items.BottleOfPeeItem;
import com.qwsadd.qwsaddmod.items.PeeBucketItem;
import com.qwsadd.qwsaddmod.items.PoopItem;
import net.minecraft.world.item.Item;
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
}