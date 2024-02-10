package com.gizmo.trophies.misc;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.TrophyBlock;
import com.gizmo.trophies.block.TrophyBlockEntity;
import com.gizmo.trophies.item.TrophyItem;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@SuppressWarnings("unused")
public class TrophyRegistries {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, OpenBlocksTrophies.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, OpenBlocksTrophies.MODID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, OpenBlocksTrophies.MODID);
	public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, OpenBlocksTrophies.MODID);
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, OpenBlocksTrophies.MODID);
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OpenBlocksTrophies.MODID);

	public static final DeferredHolder<Block, Block> TROPHY = BLOCKS.register("trophy", () -> new TrophyBlock(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).forceSolidOn()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrophyBlockEntity>> TROPHY_BE = BLOCK_ENTITIES.register("trophy", () -> BlockEntityType.Builder.of(TrophyBlockEntity::new, TROPHY.get()).build(null));
	public static final DeferredHolder<Item, Item> TROPHY_ITEM = ITEMS.register("trophy", () -> new TrophyItem(TROPHY.get(), new Item.Properties().stacksTo(1).fireResistant()));

	public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<AddTrophyModifier>> ADD_QUEST_RAM_TROPHY = LOOT_MODIFIERS.register("add_trophy", () -> AddTrophyModifier.CODEC);

	public static final DeferredHolder<SoundEvent, SoundEvent> OOF = SOUNDS.register("entity.obtrophies.player.oof", () -> SoundEvent.createVariableRangeEvent(OpenBlocksTrophies.location("entity.obtrophies.player.oof")));

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TROPHY_TAB = TABS.register("trophies", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.obtrophies"))
			.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
			.withSearchBar()
			.icon(TrophyTabHelper::makeIcon)
			.displayItems((params, output) -> TrophyTabHelper.getAllTrophies(output, params.holders(), params.enabledFeatures()))
			.build()
	);
}
