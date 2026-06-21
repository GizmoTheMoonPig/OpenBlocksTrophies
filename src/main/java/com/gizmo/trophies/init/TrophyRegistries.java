package com.gizmo.trophies.init;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.misc.AddTrophyModifier;
import com.gizmo.trophies.misc.TranslatableStrings;
import com.gizmo.trophies.misc.TrophyTabHelper;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@SuppressWarnings("unused")
public class TrophyRegistries {

	public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, OpenBlocksTrophies.MODID);
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, OpenBlocksTrophies.MODID);
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OpenBlocksTrophies.MODID);

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
