package com.gizmo.trophies.init;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.TrophyInfo;
import com.gizmo.trophies.trophy.DisplayTrophy;
import com.gizmo.trophies.trophy.TrophyInfoPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TrophyComponents {

	public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, OpenBlocksTrophies.MODID);
	public static final DeferredRegister<DataComponentPredicate.Type<?>> COMPONENT_PREDICATES = DeferredRegister.create(Registries.DATA_COMPONENT_PREDICATE_TYPE, OpenBlocksTrophies.MODID);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<TrophyInfo>> TROPHY_INFO = COMPONENTS.register("trophy_info", () -> DataComponentType.<TrophyInfo>builder().persistent(TrophyInfo.CODEC).networkSynchronized(TrophyInfo.STREAM_CODEC).cacheEncoding().build());
	public static final DeferredHolder<DataComponentType<?>, DataComponentType<DisplayTrophy>> DISPLAY_TROPHY_INFO = COMPONENTS.register("display_trophy_info", () -> DataComponentType.<DisplayTrophy>builder().persistent(DisplayTrophy.CODEC).networkSynchronized(ByteBufCodecs.fromCodec(DisplayTrophy.CODEC)).cacheEncoding().build());

	public static final DeferredHolder<DataComponentPredicate.Type<?>, DataComponentPredicate.Type<TrophyInfoPredicate>> TROPHY_INFO_PREDICATE = COMPONENT_PREDICATES.register("trophy_info", () -> new DataComponentPredicate.ConcreteType<>(TrophyInfoPredicate.CODEC));

}
