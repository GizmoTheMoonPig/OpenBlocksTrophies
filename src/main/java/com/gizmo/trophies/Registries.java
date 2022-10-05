package com.gizmo.trophies;

import com.gizmo.trophies.block.TrophyBlock;
import com.gizmo.trophies.block.TrophyBlockEntity;
import com.gizmo.trophies.item.TrophyItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registries {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, OpenBlocksTrophies.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, OpenBlocksTrophies.MODID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, OpenBlocksTrophies.MODID);

	public static final RegistryObject<Block> TROPHY = BLOCKS.register("trophy", () -> new TrophyBlock(BlockBehaviour.Properties.of(Material.STONE).strength(1.5F, 6.0F).requiresCorrectToolForDrops()));
	public static final RegistryObject<BlockEntityType<TrophyBlockEntity>> TROPHY_BE = BLOCK_ENTITIES.register("trophy", () -> BlockEntityType.Builder.of(TrophyBlockEntity::new, TROPHY.get()).build(null));
	public static final RegistryObject<Item> TROPHY_ITEM = ITEMS.register("trophy", () -> new TrophyItem(TROPHY.get(), new Item.Properties().tab(OpenBlocksTrophies.TROPHY_TAB)));
}
