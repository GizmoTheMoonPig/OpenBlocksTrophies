package com.gizmo.trophies.misc;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.DisplayTrophyBlock;
import com.gizmo.trophies.block.TrophyBlock;
import com.gizmo.trophies.block.TrophyInfo;
import com.gizmo.trophies.block.entity.TrophyBlockEntity;
import com.gizmo.trophies.block.entity.DisplayTrophyBlockEntity;
import com.gizmo.trophies.item.DisplayTrophyItem;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.trophy.DisplayTrophy;
import com.gizmo.trophies.trophy.TrophyInfoPredicate;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
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
	public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, OpenBlocksTrophies.MODID);
	public static final DeferredRegister<DataComponentPredicate.Type<?>> COMPONENT_PREDICATES = DeferredRegister.create(Registries.DATA_COMPONENT_PREDICATE_TYPE, OpenBlocksTrophies.MODID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OpenBlocksTrophies.MODID);
	public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, OpenBlocksTrophies.MODID);
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, OpenBlocksTrophies.MODID);
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OpenBlocksTrophies.MODID);

	public static final DeferredBlock<Block> TROPHY = BLOCKS.register("trophy", () -> new TrophyBlock(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).forceSolidOn().setId(ResourceKey.create(Registries.BLOCK, OpenBlocksTrophies.prefix("trophy")))));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrophyBlockEntity>> TROPHY_BE = BLOCK_ENTITIES.register("trophy", () -> new BlockEntityType<>(TrophyBlockEntity::new, TROPHY.get()));
	public static final DeferredItem<Item> TROPHY_ITEM = ITEMS.register("trophy", () -> new TrophyItem(TROPHY.get(), new Item.Properties().stacksTo(1).useBlockDescriptionPrefix().fireResistant().setId(ResourceKey.create(Registries.ITEM, OpenBlocksTrophies.prefix("trophy")))));

	public static final DeferredBlock<Block> DISPLAY_TROPHY = BLOCKS.register("display_trophy", () -> new DisplayTrophyBlock(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).forceSolidOn().setId(ResourceKey.create(Registries.BLOCK, OpenBlocksTrophies.prefix("display_trophy")))));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DisplayTrophyBlockEntity>> DISPLAY_TROPHY_BE = BLOCK_ENTITIES.register("display_trophy", () -> new BlockEntityType<>(DisplayTrophyBlockEntity::new, DISPLAY_TROPHY.get()));
	public static final DeferredItem<Item> DISPLAY_TROPHY_ITEM = ITEMS.register("display_trophy", () -> new DisplayTrophyItem(DISPLAY_TROPHY.get(), new Item.Properties().stacksTo(1).useBlockDescriptionPrefix().fireResistant().setId(ResourceKey.create(Registries.ITEM, OpenBlocksTrophies.prefix("display_trophy")))));

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<TrophyInfo>> TROPHY_INFO = COMPONENTS.register("trophy_info", () -> DataComponentType.<TrophyInfo>builder().persistent(TrophyInfo.CODEC).networkSynchronized(TrophyInfo.STREAM_CODEC).cacheEncoding().build());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<DisplayTrophy>> DISPLAY_TROPHY_INFO = COMPONENTS.register("display_trophy_info", () -> DataComponentType.<DisplayTrophy>builder().persistent(DisplayTrophy.CODEC).networkSynchronized(ByteBufCodecs.fromCodec(DisplayTrophy.CODEC)).cacheEncoding().build());

	public static final DeferredHolder<DataComponentPredicate.Type<?>, DataComponentPredicate.Type<TrophyInfoPredicate>> TROPHY_INFO_PREDICATE = COMPONENT_PREDICATES.register("trophy_info", () -> new DataComponentPredicate.Type<>(TrophyInfoPredicate.CODEC));

	public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<AddTrophyModifier>> ADD_TROPHY = LOOT_MODIFIERS.register("add_trophy", () -> AddTrophyModifier.CODEC);

	public static final DeferredHolder<SoundEvent, SoundEvent> OOF = SOUNDS.register("entity.obtrophies.player.oof", () -> SoundEvent.createVariableRangeEvent(OpenBlocksTrophies.prefix("entity.obtrophies.player.oof")));

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TROPHY_TAB = TABS.register("trophies", () -> CreativeModeTab.builder()
			.title(Component.translatable(TranslatableStrings.TROPHY_TAB))
			.withSearchBar()
			.icon(TrophyTabHelper::makeIcon)
			.displayItems((params, output) -> TrophyTabHelper.getAllTrophies(output, params.holders(), params.enabledFeatures(), TrophyTabHelper.shouldShowVariants()))
			.build()
	);
}
