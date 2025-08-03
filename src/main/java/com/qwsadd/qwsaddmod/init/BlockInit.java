package com.qwsadd.qwsaddmod.init;

import com.qwsadd.qwsaddmod.QwsaddModMain;
import com.qwsadd.qwsaddmod.blocks.PoopFermenterBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.qwsadd.qwsaddmod.blocks.SquatToiletBlock;

import java.util.function.Supplier;

public class BlockInit {
    // 创建一个用于注册方块的 DeferredRegister
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, QwsaddModMain.MODID);

    // 注册“大便块”
    public static final RegistryObject<Block> POOP_BLOCK = registerBlock("poop_block",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DIRT) // 在地图上显示的颜色，泥土色
                    .strength(0.6F) // 硬度，和泥土差不多
                    .sound(SoundType.GRAVEL))); // 声音，使用沙砾的声音感觉更合适

    /**
     * 一个辅助方法，用于同时注册方块和其对应的物品
     * @param name 方块的注册名
     * @param block 方块对象的 Supplier
     * @return 注册后的方块对象
     */
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        // 注册方块本身
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        // 注册方块对应的物品
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    /**
     * 一个辅助方法，用于注册方块物品
     * @param name 物品的注册名
     * @param block 对应的方块对象
     */
    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ItemInit.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
    public static final RegistryObject<Block> POOP_FERMENTER = registerBlock("poop_fermenter",
            () -> new PoopFermenterBlock(BlockBehaviour.Properties.of().strength(3.5f).requiresCorrectToolForDrops()));
    public static final RegistryObject<Block> SQUAT_TOILET = registerBlock("squat_toilet",
            () -> new SquatToiletBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.COMPOSTER)));
}
