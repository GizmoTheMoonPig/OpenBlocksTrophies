package com.gizmo.trophies.behavior;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;

public record CustomBehaviorType(Codec<? extends CustomBehavior> codec) {
	public static final Codec<CustomBehavior> DISPATCH_CODEC = ExtraCodecs.lazyInitializedCodec(() -> OpenBlocksTrophies.CUSTOM_BEHAVIORS.byNameCodec()).dispatch("type", CustomBehavior::getType, CustomBehaviorType::codec);
}
