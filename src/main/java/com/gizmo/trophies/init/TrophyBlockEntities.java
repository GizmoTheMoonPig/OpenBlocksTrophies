package com.gizmo.trophies.init;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.entity.DisplayTrophyBlockEntity;
import com.gizmo.trophies.block.entity.TrophyBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TrophyBlockEntities {

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, OpenBlocksTrophies.MODID);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrophyBlockEntity>> TROPHY = BLOCK_ENTITIES.register("trophy", () -> new BlockEntityType<>(TrophyBlockEntity::new, TrophyBlocks.TROPHY.get()));
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DisplayTrophyBlockEntity>> DISPLAY_TROPHY = BLOCK_ENTITIES.register("display_trophy", () -> new BlockEntityType<>(DisplayTrophyBlockEntity::new, TrophyBlocks.DISPLAY_TROPHY.get()));

}
