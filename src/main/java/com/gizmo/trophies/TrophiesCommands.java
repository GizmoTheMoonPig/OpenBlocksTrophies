package com.gizmo.trophies;

import com.gizmo.trophies.block.TrophyBlock;
import com.gizmo.trophies.block.TrophyBlockEntity;
import com.gizmo.trophies.trophy.Trophy;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.JsonOps;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;

public class TrophiesCommands {

	private static final DecimalFormat FORMAT = new DecimalFormat("#.##");

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("obtrophies")
				.then(Commands.literal("debug").requires(cs -> cs.hasPermission(3))
						.then(Commands.literal("dumpRegistry")
								.then(Commands.argument("registry", StringArgumentType.string())
										.executes(context -> getRegistryKeys(context, StringArgumentType.getString(context, "dumpRegistry")))))
						.then(Commands.literal("makeTemplatesFor")
								.then(Commands.argument("modid", StringArgumentType.string())
										.suggests((context, builder) -> SharedSuggestionProvider.suggest(getLoadedModIds(), builder))
										.executes(context -> writeTrophiesForMod(context, StringArgumentType.getString(context, "modid"))))))
				.then(Commands.literal("count")
						.executes(TrophiesCommands::count))
				.then(Commands.literal("placetrophies")
						.requires(cs -> cs.hasPermission(3))
						.executes(context -> placeAll(context, false, "all"))
						.then(Commands.argument("variants", BoolArgumentType.bool())
								.executes(context -> placeAll(context, BoolArgumentType.getBool(context, "variants"), "all"))
								.then(Commands.argument("modid", StringArgumentType.string())
										.suggests((context, builder) -> SharedSuggestionProvider.suggest(getLoadedModIds(), builder))
										.executes(context -> placeAll(context, BoolArgumentType.getBool(context, "variants"), StringArgumentType.getString(context, "modid")))))));
	}

	public static List<String> getLoadedModIds() {
		List<String> modids = new ArrayList<>();
		for (IModInfo info : ModList.get().getMods()) {
			modids.add(info.getModId());
		}
		Collections.sort(modids);
		return modids;
	}

	public static int getRegistryKeys(CommandContext<CommandSourceStack> context, String registryName) throws CommandSyntaxException {
		try {
			ResourceKey<? extends Registry<?>> key = ResourceKey.createRegistryKey(ResourceLocation.tryParse(registryName));
			context.getSource().registryAccess().registryOrThrow(key).entrySet().forEach(entry -> {
				context.getSource().sendSystemMessage(Component.literal(entry.getKey().location() + ": " + entry.getValue().toString()));
			});
			context.getSource().sendSystemMessage(Component.literal("Registry Size: " + context.getSource().registryAccess().registryOrThrow(key).entrySet().size()));
		} catch (Exception e) {
			throw new SimpleCommandExceptionType(Component.literal(e.getMessage())).create();
		}
		return Command.SINGLE_SUCCESS;
	}


	public static int count(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		if (Trophy.getTrophies().isEmpty()) {
			throw new SimpleCommandExceptionType(Component.translatable("command.obtrophies.empty_list").withStyle(ChatFormatting.RED)).create();
		}
		context.getSource().sendSuccess(() -> Component.translatable("command.obtrophies.count", Trophy.getTrophies().size()), false);
		return Command.SINGLE_SUCCESS;
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

	private static int writeTrophiesForMod(CommandContext<CommandSourceStack> context, String modid) throws CommandSyntaxException {
		if (!ModList.get().isLoaded(modid)) {
			throw new SimpleCommandExceptionType(Component.translatable("command.obtrophies.mod_not_loaded", modid).withStyle(ChatFormatting.RED)).create();
		}

		for (EntityType<?> entity : BuiltInRegistries.ENTITY_TYPE.stream().filter(type -> BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace().equals(modid)).toList()) {
			ResourceLocation entityName = BuiltInRegistries.ENTITY_TYPE.getKey(entity);
			Path path = context.getSource().getLevel().getServer().getWorldPath(LevelResource.GENERATED_DIR).resolve(modid).resolve(entityName.getPath() + ".json").normalize();
			Trophy.Builder dummy = new Trophy.Builder(entity);
			if (entity.is(Tags.EntityTypes.BOSSES)) dummy.setDropChance(0.0075D);
			if (entity.getHeight() > 0.0F) dummy.setScale(Float.parseFloat(FORMAT.format(Math.min(2.0F, 2.0F / entity.getHeight()))));
			try {
				ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
				HashingOutputStream hashingoutputstream = new HashingOutputStream(Hashing.sha256(), bytearrayoutputstream);

				try (JsonWriter jsonwriter = new JsonWriter(new OutputStreamWriter(hashingoutputstream, StandardCharsets.UTF_8))) {
					jsonwriter.setSerializeNulls(false);
					jsonwriter.setIndent("  ");
					GsonHelper.writeValue(jsonwriter, Trophy.CODEC.encodeStart(JsonOps.INSTANCE, dummy.build()).resultOrPartial(OpenBlocksTrophies.LOGGER::error).orElseThrow(), Comparator.naturalOrder());
				}

				if (!Files.exists(path)) {
					Files.createDirectories(path.getParent());
					Files.write(path, bytearrayoutputstream.toByteArray());
					context.getSource().sendSuccess(() -> Component.translatable("command.obtrophies.trophy_made", entityName.toString()), false);
				}
			} catch (IOException ioexception) {
				OpenBlocksTrophies.LOGGER.error("Failed to save file to {}", path, ioexception);
			}
		}
		return Command.SINGLE_SUCCESS;
	}
}
