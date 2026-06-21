package com.gizmo.trophies.command;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.trophy.Trophy;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TrophiesCommands {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
		dispatcher.register(Commands.literal("obtrophies")
			.then(Commands.literal("debug")
				.requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
				.then(DumpRegistryCommand.register())
				.then(GenerateTrophyStubCommand.register()))
			.then(CreateDisplayTrophyCommand.register(context))
			.then(Commands.literal("count").executes(TrophiesCommands::count))
			.then(PlaceTrophiesCommand.register()));
	}

	public static List<String> getLoadedModIds() {
		List<String> modids = new ArrayList<>();
		for (IModInfo info : ModList.get().getMods()) {
			modids.add(info.getModId());
		}
		modids.add(0, "all");
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

	public static boolean writeToFile(JsonElement element, Path path) {
		try {
			ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
			HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha256(), bytearrayoutputstream);

			try (JsonWriter jsonwriter = new JsonWriter(new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8))) {
				jsonwriter.setSerializeNulls(false);
				jsonwriter.setIndent("  ");
				GsonHelper.writeValue(jsonwriter, element, Comparator.naturalOrder());
			}

			if (!Files.exists(path)) {
				Files.createDirectories(path.getParent());
				Files.write(path, bytearrayoutputstream.toByteArray());
				return true;
			}
		} catch (IOException ioexception) {
			OpenBlocksTrophies.LOGGER.error("Failed to save file to {}", path, ioexception);
		}
		return false;
	}
}
