package com.gizmo.trophies.misc;

import com.gizmo.trophies.item.TrophyItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class AddTrophyModifier extends LootModifier {
	public static final Codec<AddTrophyModifier> CODEC = RecordCodecBuilder.create(inst -> LootModifier.codecStart(inst).and(inst.group(
			BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(o -> o.entity),
			Codec.INT.optionalFieldOf("variant", 0).forGetter(o -> o.variant))
	).apply(inst, AddTrophyModifier::new));

	private final EntityType<?> entity;
	private final int variant;

	public AddTrophyModifier(LootItemCondition[] conditions, EntityType<?> entity) {
		this(conditions, entity, 0);
	}

	public AddTrophyModifier(LootItemCondition[] conditions, EntityType<?> entity, int variant) {
		super(conditions);
		this.entity = entity;
		this.variant = variant;
	}

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		generatedLoot.add(TrophyItem.loadEntityToTrophy(this.entity, this.variant, false));
		return generatedLoot;
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC;
	}
}
