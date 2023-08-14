package com.gizmo.trophies;

import com.gizmo.trophies.block.TrophyBlock;
import com.gizmo.trophies.block.TrophyBlockEntity;
import com.gizmo.trophies.trophy.Trophy;
import com.google.common.collect.Maps;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
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
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.*;

public class TrophiesCommands {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("obtrophies")
				.then(Commands.literal("count")
						.executes(TrophiesCommands::count))
				.then(Commands.literal("placetrophies")
						.requires(cs -> cs.hasPermission(3))
						.executes(context -> placeAll(context, "all"))
						.then(Commands.argument("modid", StringArgumentType.string())
								.suggests((context, builder) -> SharedSuggestionProvider.suggest(getLoadedModIds(), builder))
								.executes(context -> placeAll(context, StringArgumentType.getString(context, "modid"))))));
	}

	public static List<String> getLoadedModIds() {
		List<String> modids = new ArrayList<>();
		for (IModInfo info : ModList.get().getMods()) {
			modids.add(info.getModId());
		}
		Collections.sort(modids);
		return modids;
	}

	public static int count(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		if (Trophy.getTrophies().isEmpty()) {
			throw new SimpleCommandExceptionType(Component.translatable("command.obtrophies.empty_list").withStyle(ChatFormatting.RED)).create();
		}
		context.getSource().sendSuccess(() -> Component.translatable("command.obtrophies.count", Trophy.getTrophies().size()), false);
		return Command.SINGLE_SUCCESS;
	}

	public static int placeAll(CommandContext<CommandSourceStack> context, String modid) throws CommandSyntaxException {
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
				BlockPos pos = BlockPos.containing(context.getSource().getPosition()).offset(i, 0, j);
				context.getSource().getLevel().setBlockAndUpdate(pos, TrophyRegistries.TROPHY.get().defaultBlockState().setValue(TrophyBlock.FACING, Direction.WEST));
				if (context.getSource().getLevel().getBlockEntity(pos) instanceof TrophyBlockEntity trophyBE) {
					trophyBE.setTrophy(sortedTrophies.entrySet().stream().toList().get(index).getValue());
				}
			}
		}
		context.getSource().sendSuccess(() -> Component.translatable("command.obtrophies.place", amount), false);
		return Command.SINGLE_SUCCESS;
	}
}
