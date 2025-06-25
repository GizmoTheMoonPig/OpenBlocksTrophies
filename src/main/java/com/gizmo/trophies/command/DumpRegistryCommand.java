package com.gizmo.trophies.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DumpRegistryCommand {

	public static LiteralArgumentBuilder<CommandSourceStack> register() {
		return Commands.literal("dumpRegistry")
			.then(Commands.argument("registry", ResourceLocationArgument.id())
				.suggests((context, builder) -> SharedSuggestionProvider.suggestResource(getAllRegistries(context.getSource().getLevel()), builder))
				.then(Commands.argument("dumpToFile", BoolArgumentType.bool())
					.executes(context -> forEachRegistry(context, ResourceLocationArgument.getId(context, "registry"), BoolArgumentType.getBool(context, "dumpToFile")))));
	}

	public static List<ResourceLocation> getAllRegistries(ServerLevel level) {
		List<ResourceLocation> registries = new ArrayList<>(level.registryAccess().registries().map(registryEntry -> registryEntry.key().location()).toList());
		registries.addFirst(ResourceLocation.fromNamespaceAndPath("", "all"));
		return registries;
	}

	public static int forEachRegistry(CommandContext<CommandSourceStack> context, ResourceLocation registryName, boolean dumpToFile) {
		if (registryName.toString().equals("all")) {
			getAllRegistries(context.getSource().getLevel()).forEach(location -> getRegistryKeys(context, location, dumpToFile));
		} else {
			getRegistryKeys(context, registryName, dumpToFile);
		}

		return Command.SINGLE_SUCCESS;
	}

	public static void getRegistryKeys(CommandContext<CommandSourceStack> context, ResourceLocation registryName, boolean dumpToFile) {
		ResourceKey<? extends Registry<?>> key = ResourceKey.createRegistryKey(registryName);
		if (dumpToFile) {
			Path path = context.getSource().getLevel().getServer().getWorldPath(LevelResource.GENERATED_DIR).resolve("registries").resolve(registryName.getNamespace()).resolve(registryName.getPath() + ".json").normalize();
			JsonObject object = new JsonObject();
			JsonArray registryArray = new JsonArray();
			context.getSource().registryAccess().lookupOrThrow(key).entrySet().forEach(entry -> registryArray.add(entry.getKey().location().toString()));
			object.add("entries", registryArray);
			TrophiesCommands.writeToFile(object, path);
		} else {
			context.getSource().registryAccess().lookupOrThrow(key).entrySet().forEach(entry ->
				context.getSource().sendSystemMessage(Component.literal(entry.getKey().location().toString())));
			context.getSource().sendSystemMessage(Component.literal("Registry Size: " + context.getSource().registryAccess().lookupOrThrow(key).entrySet().size()));
		}
	}
}
