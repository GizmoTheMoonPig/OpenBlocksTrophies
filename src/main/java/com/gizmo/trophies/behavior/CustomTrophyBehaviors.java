package com.gizmo.trophies.behavior;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class CustomTrophyBehaviors {
	public static final ResourceKey<Registry<CustomBehaviorType>> BEHAVIOR_KEY = ResourceKey.createRegistryKey(OpenBlocksTrophies.location("custom_behavior"));
	public static final DeferredRegister<CustomBehaviorType> CUSTOM_BEHAVIORS = DeferredRegister.create(BEHAVIOR_KEY, OpenBlocksTrophies.MODID);
	public static final Supplier<IForgeRegistry<CustomBehaviorType>> BEHAVIOR_REGISTRY = CUSTOM_BEHAVIORS.makeRegistry(() -> new RegistryBuilder<CustomBehaviorType>().hasTags());

	public static final Codec<CustomBehavior> CODEC = ExtraCodecs.lazyInitializedCodec(() -> BEHAVIOR_REGISTRY.get().getCodec()).dispatch("type", CustomBehavior::getType, CustomBehaviorType::codec);

	public static final RegistryObject<CustomBehaviorType> ITEM_DROP = CUSTOM_BEHAVIORS.register("item", () -> new CustomBehaviorType(ItemDropBehavior.CODEC));
	public static final RegistryObject<CustomBehaviorType> ELDER_GUARDIAN_CURSE = CUSTOM_BEHAVIORS.register("guardian_curse", () -> new CustomBehaviorType(ElderGuardianCurseBehavior.CODEC));
	public static final RegistryObject<CustomBehaviorType> CLICK_WITH_ITEM = CUSTOM_BEHAVIORS.register("right_click_item", () -> new CustomBehaviorType(ClickWithItemBehavior.CODEC));
	public static final RegistryObject<CustomBehaviorType> EXPLOSION = CUSTOM_BEHAVIORS.register("explosion", () -> new CustomBehaviorType(ExplosionBehavior.CODEC));
	public static final RegistryObject<CustomBehaviorType> MOB_EFFECT = CUSTOM_BEHAVIORS.register("mob_effect", () -> new CustomBehaviorType(MobEffectBehavior.CODEC));
	public static final RegistryObject<CustomBehaviorType> PLACE_BLOCK = CUSTOM_BEHAVIORS.register("place_block", () -> new CustomBehaviorType(PlaceBlockBehavior.CODEC));
	public static final RegistryObject<CustomBehaviorType> SET_FIRE = CUSTOM_BEHAVIORS.register("set_fire", () -> new CustomBehaviorType(PlayerSetFireBehavior.CODEC));
	public static final RegistryObject<CustomBehaviorType> LOOT_TABLE = CUSTOM_BEHAVIORS.register("loot_table", () -> new CustomBehaviorType(PullFromLootTableBehavior.CODEC));
	public static final RegistryObject<CustomBehaviorType> ARROW = CUSTOM_BEHAVIORS.register("arrow", () -> new CustomBehaviorType(ShootArrowBehavior.CODEC));
	public static final RegistryObject<CustomBehaviorType> ENDER_PEARL = CUSTOM_BEHAVIORS.register("ender_pearl", () -> new CustomBehaviorType(ShootEnderPearlBehavior.CODEC));
	public static final RegistryObject<CustomBehaviorType> LLAMA_SPIT = CUSTOM_BEHAVIORS.register("llama_spit", () -> new CustomBehaviorType(ShootLlamaSpitBehavior.CODEC));
	public static final RegistryObject<CustomBehaviorType> TOTEM_OF_UNDYING = CUSTOM_BEHAVIORS.register("totem_of_undying", () -> new CustomBehaviorType(TotemOfUndyingEffectBehavior.CODEC));

}
