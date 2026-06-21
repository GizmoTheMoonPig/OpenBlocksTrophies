package com.gizmo.trophies.init;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.DisplayTrophyBlock;
import com.gizmo.trophies.block.TrophyBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TrophyBlocks {

	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(OpenBlocksTrophies.MODID);

	public static final DeferredBlock<Block> TROPHY = BLOCKS.register("trophy", () -> new TrophyBlock(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).forceSolidOn().setId(ResourceKey.create(Registries.BLOCK, OpenBlocksTrophies.prefix("trophy")))));
	public static final DeferredBlock<Block> DISPLAY_TROPHY = BLOCKS.register("display_trophy", () -> new DisplayTrophyBlock(BlockBehaviour.Properties.of().strength(1.5F, 6.0F).forceSolidOn().setId(ResourceKey.create(Registries.BLOCK, OpenBlocksTrophies.prefix("display_trophy")))));

}
