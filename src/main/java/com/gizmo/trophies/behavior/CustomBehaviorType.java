package com.gizmo.trophies.behavior;

import com.mojang.serialization.Codec;

public record CustomBehaviorType(Codec<? extends CustomBehavior> codec) {
}
