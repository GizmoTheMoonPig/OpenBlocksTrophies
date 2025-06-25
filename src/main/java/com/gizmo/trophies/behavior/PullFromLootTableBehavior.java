package com.gizmo.trophies.behavior;

import com.gizmo.trophies.block.entity.TrophyBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemHandlerHelper;

public record PullFromLootTableBehavior(ResourceKey<LootTable> lootTable, int rolls, int cooldown) implements CustomBehavior {

	public static final MapCodec<PullFromLootTableBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ResourceKey.codec(Registries.LOOT_TABLE).fieldOf("loot_table").forGetter(PullFromLootTableBehavior::lootTable),
			Codec.INT.optionalFieldOf("rolls", 1).forGetter(PullFromLootTableBehavior::rolls),
			Codec.INT.optionalFieldOf("cooldown", 1000).forGetter(PullFromLootTableBehavior::cooldown)
	).apply(instance, PullFromLootTableBehavior::new));

	public PullFromLootTableBehavior(ResourceKey<LootTable> lootTable, int cooldown) {
		this(lootTable, 1, cooldown);
	}

	@Override
	public CustomBehaviorType getType() {
		return CustomTrophyBehaviors.LOOT_TABLE.get();
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem) {
		for (int i = 0; i < this.rolls(); i++) {
			LootParams.Builder builder = new LootParams.Builder(player.level())
					.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(block.getBlockPos()))
					.withParameter(LootContextParams.THIS_ENTITY, player)
					.withLuck(player.getLuck());
			player.level().getServer().reloadableRegistries().getLootTable(this.lootTable())
					.getRandomItems(builder.create(LootContextParamSets.ADVANCEMENT_REWARD)) //use advancement reward just so we only need to provide the pos and player
					.forEach(stack -> ItemHandlerHelper.giveItemToPlayer(player, stack.copy()));
		}

		return this.cooldown();
	}
}
