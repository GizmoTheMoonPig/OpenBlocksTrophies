package com.gizmo.trophies.behavior;

import com.gizmo.trophies.block.TrophyBlockEntity;
import com.mojang.serialization.Codec;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record TotemOfUndyingEffectBehavior() implements CustomBehavior {

	public static final Codec<TotemOfUndyingEffectBehavior> CODEC = Codec.unit(TotemOfUndyingEffectBehavior::new);

	@Override
	public CustomBehaviorType getType() {
		return CustomTrophyBehaviors.TOTEM_OF_UNDYING.get();
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem) {
		player.connection.send(new ClientboundEntityEventPacket(player, (byte) 35));
		return 100;
	}
}
