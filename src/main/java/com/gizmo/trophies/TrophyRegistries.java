package com.gizmo.trophies;

import com.gizmo.trophies.block.TrophyBlock;
import com.gizmo.trophies.block.TrophyBlockEntity;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.trophy.AddTrophyModifier;
import com.gizmo.trophies.trophy.TrophyTabHelper;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TrophyRegistries {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, OpenBlocksTrophies.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, OpenBlocksTrophies.MODID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, OpenBlocksTrophies.MODID);
	public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, OpenBlocksTrophies.MODID);
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OpenBlocksTrophies.MODID);

	public static final RegistryObject<Block> TROPHY = BLOCKS.register("trophy", () -> new TrophyBlock(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).forceSolidOn()));
	public static final RegistryObject<BlockEntityType<TrophyBlockEntity>> TROPHY_BE = BLOCK_ENTITIES.register("trophy", () -> BlockEntityType.Builder.of(TrophyBlockEntity::new, TROPHY.get()).build(null));
	public static final RegistryObject<Item> TROPHY_ITEM = ITEMS.register("trophy", () -> new TrophyItem(TROPHY.get(), new Item.Properties().stacksTo(1).fireResistant()));

	public static final RegistryObject<Codec<AddTrophyModifier>> ADD_QUEST_RAM_TROPHY = LOOT_MODIFIERS.register("add_trophy", () -> AddTrophyModifier.CODEC);

	public static final RegistryObject<CreativeModeTab> TROPHY_TAB = TABS.register("trophies", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.obtrophies"))
			.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
			.withSearchBar()
			.icon(TrophyTabHelper::makeIcon)
			.displayItems((params, output) -> TrophyTabHelper.getAllTrophies(output, params.holders()))
			.build()
	);
}
