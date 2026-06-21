package com.gizmo.trophies.misc;

import com.gizmo.trophies.item.TrophyHelper;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class AddTrophyModifier extends LootModifier {
	public static final MapCodec<AddTrophyModifier> CODEC = RecordCodecBuilder.mapCodec(inst -> LootModifier.codecStart(inst).and(inst.group(
			BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(o -> o.entity),
			CompoundTag.CODEC.optionalFieldOf("variant", new CompoundTag()).forGetter(o -> o.variant))
	).apply(inst, AddTrophyModifier::new));

	private final EntityType<?> entity;
	private final CompoundTag variant;

	public AddTrophyModifier(LootItemCondition[] conditions, int priority, EntityType<?> entity) {
		this(conditions, priority, entity, new CompoundTag());
	}

	public AddTrophyModifier(LootItemCondition[] conditions, int priority, EntityType<?> entity, CompoundTag variant) {
		super(conditions, priority);
		this.entity = entity;
		this.variant = variant;
	}

	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		generatedLoot.add(TrophyHelper.loadVariantToTrophy(this.entity, this.variant).create());
		return generatedLoot;
	}

	@Override
	public MapCodec<? extends IGlobalLootModifier> codec() {
		return CODEC;
	}
}
