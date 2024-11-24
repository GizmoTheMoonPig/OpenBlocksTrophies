package com.gizmo.trophies.command;

import com.gizmo.trophies.TrophyRegistries;
import com.gizmo.trophies.block.TrophyBlock;
import com.gizmo.trophies.block.entity.TrophyBlockEntity;
import com.gizmo.trophies.trophy.Trophy;
import com.google.common.collect.Maps;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class PlaceTrophiesCommand {

	public static LiteralArgumentBuilder<CommandSourceStack> register() {
		return Commands.literal("placetrophies")
			.requires(cs -> cs.hasPermission(3))
			.executes(context -> placeAll(context, false, "all"))
			.then(Commands.argument("variants", BoolArgumentType.bool())
				.executes(context -> placeAll(context, BoolArgumentType.getBool(context, "variants"), "all"))
				.then(Commands.argument("modid", StringArgumentType.string())
					.suggests((context, builder) -> SharedSuggestionProvider.suggest(TrophiesCommands.getLoadedModIds(), builder))
					.executes(context -> placeAll(context, BoolArgumentType.getBool(context, "variants"), StringArgumentType.getString(context, "modid")))));
	}

	public static int placeAll(CommandContext<CommandSourceStack> context, boolean placeVariants, String modid) throws CommandSyntaxException {
		if (Trophy.getTrophies().isEmpty()) {
			throw new SimpleCommandExceptionType(Component.translatable("command.obtrophies.empty_list").withStyle(ChatFormatting.RED)).create();
		}

		Map<ResourceLocation, Trophy> sortedTrophies = new TreeMap<>(Comparator.naturalOrder());
		if (!modid.equals("all")) {
			sortedTrophies.putAll(Maps.filterKeys(Trophy.getTrophies(), input -> input.getNamespace().equals(modid)));
		} else {
			sortedTrophies.putAll(Trophy.getTrophies());
		}

		int amount = sortedTrophies.size();
		int sideLength = (int) Math.ceil(Math.sqrt(amount));

		for (int i = 0; i < sideLength; i++) {
			for (int j = 0; j < sideLength; j++) {
				int index = j + i * sideLength;
				if (index > amount - 1) break;
				Trophy trophy = sortedTrophies.entrySet().stream().toList().get(index).getValue();
				if (placeVariants && !trophy.getVariants(context.getSource().getLevel().registryAccess()).isEmpty()) {
					for (int v = 0; v < trophy.getVariants(context.getSource().getLevel().registryAccess()).size(); v++) {
						BlockPos pos = BlockPos.containing(context.getSource().getPosition()).offset(i, v, j);
						setupTrophy(context.getSource().getLevel(), pos, trophy, v);
					}
				} else {
					BlockPos pos = BlockPos.containing(context.getSource().getPosition()).offset(i, 0, j);
					setupTrophy(context.getSource().getLevel(), pos, trophy, -1);
				}
			}
		}
		context.getSource().sendSuccess(() -> Component.translatable("command.obtrophies.place", amount), false);
		return Command.SINGLE_SUCCESS;
	}

	private static void setupTrophy(Level level, BlockPos pos, Trophy trophy, int variant) {
		level.setBlockAndUpdate(pos, TrophyRegistries.TROPHY.get().defaultBlockState().setValue(TrophyBlock.FACING, Direction.WEST));
		if (level.getBlockEntity(pos) instanceof TrophyBlockEntity trophyBE) {
			trophyBE.setTrophy(trophy);
			if (variant != -1) {
				trophyBE.setVariant(variant);
			}
		}
	}
}
