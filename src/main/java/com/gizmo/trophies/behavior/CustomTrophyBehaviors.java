package com.gizmo.trophies.behavior;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CustomTrophyBehaviors {
	public static final DeferredRegister<CustomBehaviorType> CUSTOM_BEHAVIORS = DeferredRegister.create(OpenBlocksTrophies.CUSTOM_BEHAVIORS_KEY, OpenBlocksTrophies.MODID);

	public static final DeferredHolder<CustomBehaviorType, CustomBehaviorType> ITEM_DROP = CUSTOM_BEHAVIORS.register("item", () -> new CustomBehaviorType(ItemDropBehavior.CODEC));
	public static final DeferredHolder<CustomBehaviorType, CustomBehaviorType> ELDER_GUARDIAN_CURSE = CUSTOM_BEHAVIORS.register("guardian_curse", () -> new CustomBehaviorType(ElderGuardianCurseBehavior.CODEC));
	public static final DeferredHolder<CustomBehaviorType, CustomBehaviorType> CLICK_WITH_ITEM = CUSTOM_BEHAVIORS.register("right_click_item", () -> new CustomBehaviorType(ClickWithItemBehavior.CODEC));
	public static final DeferredHolder<CustomBehaviorType, CustomBehaviorType> EXPLOSION = CUSTOM_BEHAVIORS.register("explosion", () -> new CustomBehaviorType(ExplosionBehavior.CODEC));
	public static final DeferredHolder<CustomBehaviorType, CustomBehaviorType> MOB_EFFECT = CUSTOM_BEHAVIORS.register("mob_effect", () -> new CustomBehaviorType(MobEffectBehavior.CODEC));
	public static final DeferredHolder<CustomBehaviorType, CustomBehaviorType> PLACE_BLOCK = CUSTOM_BEHAVIORS.register("place_block", () -> new CustomBehaviorType(PlaceBlockBehavior.CODEC));
	public static final DeferredHolder<CustomBehaviorType, CustomBehaviorType> SET_FIRE = CUSTOM_BEHAVIORS.register("set_fire", () -> new CustomBehaviorType(PlayerSetFireBehavior.CODEC));
	public static final DeferredHolder<CustomBehaviorType, CustomBehaviorType> LOOT_TABLE = CUSTOM_BEHAVIORS.register("loot_table", () -> new CustomBehaviorType(PullFromLootTableBehavior.CODEC));
	public static final DeferredHolder<CustomBehaviorType, CustomBehaviorType> ARROW = CUSTOM_BEHAVIORS.register("arrow", () -> new CustomBehaviorType(ShootArrowBehavior.CODEC));
	public static final DeferredHolder<CustomBehaviorType, CustomBehaviorType> ENDER_PEARL = CUSTOM_BEHAVIORS.register("ender_pearl", () -> new CustomBehaviorType(ShootEnderPearlBehavior.CODEC));
	public static final DeferredHolder<CustomBehaviorType, CustomBehaviorType> LLAMA_SPIT = CUSTOM_BEHAVIORS.register("llama_spit", () -> new CustomBehaviorType(ShootLlamaSpitBehavior.CODEC));
	public static final DeferredHolder<CustomBehaviorType, CustomBehaviorType> TOTEM_OF_UNDYING = CUSTOM_BEHAVIORS.register("totem_of_undying", () -> new CustomBehaviorType(TotemOfUndyingEffectBehavior.CODEC));

}
