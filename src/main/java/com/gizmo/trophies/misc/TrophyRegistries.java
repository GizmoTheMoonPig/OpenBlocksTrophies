package com.gizmo.trophies.misc;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.DisplayTrophyBlock;
import com.gizmo.trophies.block.TrophyBlock;
import com.gizmo.trophies.block.entity.TrophyBlockEntity;
import com.gizmo.trophies.block.entity.DisplayTrophyBlockEntity;
import com.gizmo.trophies.item.DisplayTrophyItem;
import com.gizmo.trophies.item.TrophyItem;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.*;

@SuppressWarnings("unused")
public class TrophyRegistries {

	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(OpenBlocksTrophies.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, OpenBlocksTrophies.MODID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OpenBlocksTrophies.MODID);
	public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, OpenBlocksTrophies.MODID);
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, OpenBlocksTrophies.MODID);
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OpenBlocksTrophies.MODID);

	public static final DeferredBlock<Block> TROPHY = BLOCKS.register("trophy", () -> new TrophyBlock(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).forceSolidOn()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrophyBlockEntity>> TROPHY_BE = BLOCK_ENTITIES.register("trophy", () -> BlockEntityType.Builder.of(TrophyBlockEntity::new, TROPHY.get()).build(null));
	public static final DeferredItem<Item> TROPHY_ITEM = ITEMS.register("trophy", () -> new TrophyItem(TROPHY.get(), new Item.Properties().stacksTo(1).fireResistant()));

	public static final DeferredBlock<Block> DISPLAY_TROPHY = BLOCKS.register("display_trophy", () -> new DisplayTrophyBlock(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).forceSolidOn()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DisplayTrophyBlockEntity>> DISPLAY_TROPHY_BE = BLOCK_ENTITIES.register("display_trophy", () -> BlockEntityType.Builder.of(DisplayTrophyBlockEntity::new, DISPLAY_TROPHY.get()).build(null));
	public static final DeferredItem<Item> DISPLAY_TROPHY_ITEM = ITEMS.register("display_trophy", () -> new DisplayTrophyItem(DISPLAY_TROPHY.get(), new Item.Properties().stacksTo(1).fireResistant()));

	public static final DeferredHolder<Codec<? extends IGlobalLootModifier>, Codec<AddTrophyModifier>> ADD_QUEST_RAM_TROPHY = LOOT_MODIFIERS.register("add_trophy", () -> AddTrophyModifier.CODEC);

	public static final DeferredHolder<SoundEvent, SoundEvent> OOF = SOUNDS.register("entity.obtrophies.player.oof", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(OpenBlocksTrophies.MODID, "entity.obtrophies.player.oof")));

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TROPHY_TAB = TABS.register("trophies", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup.obtrophies"))
			.withSearchBar()
			.icon(TrophyTabHelper::makeIcon)
			.displayItems((params, output) -> TrophyTabHelper.getAllTrophies(output, params.holders(), params.enabledFeatures(), TrophyTabHelper.shouldShowVariants()))
			.build()
	);
}
