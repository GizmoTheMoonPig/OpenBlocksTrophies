package com.gizmo.trophies;

import com.gizmo.trophies.block.TrophyBlock;
import com.gizmo.trophies.block.TrophyBlockEntity;
import com.gizmo.trophies.trophy.Trophy;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.JsonOps;
import net.jodah.typetools.TypeResolver;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforgespi.language.IModInfo;
import org.jetbrains.annotations.Nullable;

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
								.then(Commands.argument("registry", ResourceLocationArgument.id())
										.suggests((context, builder) -> SharedSuggestionProvider.suggestResource(getAllRegistries(context.getSource().getLevel()), builder))
										.then(Commands.argument("dumpToFile", BoolArgumentType.bool())
												.executes(context -> forEachRegistry(context, ResourceLocationArgument.getId(context, "registry"), BoolArgumentType.getBool(context, "dumpToFile"))))))
						.then(Commands.literal("makeTemplatesFor")
								.then(Commands.argument("modid", StringArgumentType.string())
										.suggests((context, builder) -> SharedSuggestionProvider.suggest(getLoadedModIds(), builder))
										.executes(context -> writeTrophiesForMod(context, StringArgumentType.getString(context, "modid"), true, false))
										.then(Commands.argument("check_existing_trophies", BoolArgumentType.bool())
												.executes(context -> writeTrophiesForMod(context, StringArgumentType.getString(context, "modid"), BoolArgumentType.getBool(context, "check_existing_trophies"), false))
												.then(Commands.argument("print_entities", BoolArgumentType.bool())
														.executes(context -> writeTrophiesForMod(context, StringArgumentType.getString(context, "modid"), BoolArgumentType.getBool(context, "check_existing_trophies"), BoolArgumentType.getBool(context, "print_entities"))))))))
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
		modids.add(0, "all");
		Collections.sort(modids);
		return modids;
	}

	public static List<ResourceLocation> getAllRegistries(ServerLevel level) {
		List<ResourceLocation> registries = new ArrayList<>(level.registryAccess().registries().map(registryEntry -> registryEntry.key().location()).toList());
		registries.add(0, new ResourceLocation("", "all"));
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
			context.getSource().registryAccess().registryOrThrow(key).entrySet().forEach(entry -> {
				registryArray.add(entry.getKey().location().toString());
			});
			object.add("entries", registryArray);
			writeToFile(object, path);
		} else {
			context.getSource().registryAccess().registryOrThrow(key).entrySet().forEach(entry ->
					context.getSource().sendSystemMessage(Component.literal(entry.getKey().location().toString())));
			context.getSource().sendSystemMessage(Component.literal("Registry Size: " + context.getSource().registryAccess().registryOrThrow(key).entrySet().size()));
		}
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

	private static int writeTrophiesForMod(CommandContext<CommandSourceStack> context, String modid, boolean checkExistingConfigs, boolean printEachEntity) throws CommandSyntaxException {
		if (!modid.equals("all") && !ModList.get().isLoaded(modid)) {
			throw new SimpleCommandExceptionType(Component.translatable("command.obtrophies.mod_not_loaded", modid).withStyle(ChatFormatting.RED)).create();
		}
		int successfulFilesMade = 0;
		for (EntityType<?> entity : BuiltInRegistries.ENTITY_TYPE.stream().filter(type -> (modid.equals("all") || BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace().equals(modid)) && checkExistingConfigs != Trophy.getTrophies().containsKey(BuiltInRegistries.ENTITY_TYPE.getKey(type))).toList()) {
			Class<?> instance = getEntityClass(entity);
			if (instance != null && Mob.class.isAssignableFrom(instance)) {
				ResourceLocation entityName = BuiltInRegistries.ENTITY_TYPE.getKey(entity);
				Path path = context.getSource().getLevel().getServer().getWorldPath(LevelResource.GENERATED_DIR).resolve(entityName.getNamespace()).resolve("trophies").resolve(entityName.getPath() + ".json").normalize();
				Trophy.Builder dummy = new Trophy.Builder(entity);
				if (entity.is(Tags.EntityTypes.BOSSES)) dummy.setDropChance(0.0075D);
				if (entity.getHeight() > 0.0F)
					dummy.setScale(Float.parseFloat(FORMAT.format(Math.min(2.0F, 2.0F / entity.getHeight()))));
				if (writeToFile(Trophy.BASE_CODEC.encodeStart(JsonOps.INSTANCE, dummy.build()).resultOrPartial(OpenBlocksTrophies.LOGGER::error).orElseThrow(), path)) {
					if (printEachEntity) {
						context.getSource().sendSuccess(() -> Component.translatable("command.obtrophies.trophy_made", entityName.toString()), false);
					}
					successfulFilesMade++;
				}
			}
		}
		int totalFiles = successfulFilesMade;
		context.getSource().sendSuccess(() -> Component.translatable("command.obtrophies.trophies_made", totalFiles), false);
		return Command.SINGLE_SUCCESS;
	}

	private static boolean writeToFile(JsonElement element, Path path) {
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

	@SuppressWarnings("unchecked")
	@Nullable
	//this is actual insanity
	private static <T extends Entity> Class<T> getEntityClass(EntityType<T> type) {
		final Class<T> entityClass = (Class<T>) TypeResolver.resolveRawArgument(EntityType.EntityFactory.class, type.factory.getClass());
		if ((Class<?>) entityClass == TypeResolver.Unknown.class) {
			OpenBlocksTrophies.LOGGER.error("Couldn't resolve entity class provided for entity {}", type.getDescriptionId());
			return null;
		}
		return entityClass;
	}
}
