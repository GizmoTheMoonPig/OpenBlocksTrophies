package com.gizmo.trophies.behavior;

import com.gizmo.trophies.block.TrophyBlockEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public interface CustomBehavior {

	CustomBehaviorType getType();

	int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem);
}
