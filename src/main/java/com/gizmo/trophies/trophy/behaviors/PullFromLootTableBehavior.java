package com.gizmo.trophies.trophy.behaviors;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.block.TrophyBlockEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Objects;

public class PullFromLootTableBehavior extends CustomBehavior {

	private final ResourceLocation lootTable;
	private final int tableRolls;
	private final int cooldown;

	public PullFromLootTableBehavior() {
		this(BuiltInLootTables.EMPTY, 0);
	}

	public PullFromLootTableBehavior(ResourceLocation lootTable, int cooldown) {
		this(lootTable, 1, cooldown);
	}

	public PullFromLootTableBehavior(ResourceLocation lootTable, int rolls, int cooldown) {
		this.lootTable = lootTable;
		this.tableRolls = rolls;
		this.cooldown = cooldown;
	}

	@Override
	public ResourceLocation getType() {
		return OpenBlocksTrophies.location("loot_table");
	}

	@Override
	public void serializeToJson(JsonObject object, JsonSerializationContext context) {
		object.add("loot_table", context.serialize(this.lootTable.toString()));
		object.add("rolls", context.serialize(this.tableRolls));
		if (this.cooldown != 0) {
			object.add("cooldown", context.serialize(this.cooldown));
		}
	}

	@Override
	public CustomBehavior fromJson(JsonObject object) {
		ResourceLocation table = ResourceLocation.tryParse(GsonHelper.getAsString(object, "loot_table"));
		int rolls = GsonHelper.getAsInt(object, "rolls", 1);
		int cooldown = GsonHelper.getAsInt(object, "cooldown", 0);
		return new PullFromLootTableBehavior(Objects.requireNonNull(table), rolls, cooldown);
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem) {
		for (int i = 0; i < this.tableRolls; i++) {
			LootContext.Builder builder = new LootContext.Builder(player.getLevel())
					.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(block.getBlockPos()))
					.withParameter(LootContextParams.THIS_ENTITY, player)
					.withLuck(player.getLuck())
					.withRandom(player.getRandom());
			player.getLevel().getServer().getLootTables().get(this.lootTable)
					.getRandomItems(builder.create(LootContextParamSets.ADVANCEMENT_REWARD)) //use advancement reward just so we only need to provide the pos and player
					.forEach(stack -> ItemHandlerHelper.giveItemToPlayer(player, stack.copy()));
		}

		return this.cooldown;
	}
}
