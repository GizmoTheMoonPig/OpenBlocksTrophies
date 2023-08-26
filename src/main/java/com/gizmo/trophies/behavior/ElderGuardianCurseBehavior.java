package com.gizmo.trophies.behavior;

import com.gizmo.trophies.block.TrophyBlockEntity;
import com.mojang.serialization.Codec;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record ElderGuardianCurseBehavior() implements CustomBehavior {

	public static final Codec<ElderGuardianCurseBehavior> CODEC = Codec.unit(ElderGuardianCurseBehavior::new);

	@Override
	public CustomBehaviorType getType() {
		return CustomTrophyBehaviors.ELDER_GUARDIAN_CURSE.get();
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem) {
		player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT, 1.0F));
		return 100;
	}
}
