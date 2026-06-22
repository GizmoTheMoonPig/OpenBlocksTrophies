package com.gizmo.trophies.command;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.trophy.Trophy;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.JsonOps;
import net.jodah.typetools.TypeResolver;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.Tags;
import org.jspecify.annotations.Nullable;

import java.nio.file.Path;
import java.text.DecimalFormat;

public class GenerateTrophyStubCommand {

	private static final DecimalFormat FORMAT = new DecimalFormat("#.##");

	public static LiteralArgumentBuilder<CommandSourceStack> register() {
		return Commands.literal("makeTemplatesFor")
			.then(Commands.argument("modid", StringArgumentType.string())
				.suggests((context, builder) -> SharedSuggestionProvider.suggest(TrophiesCommands.getLoadedModIds(), builder))
				.executes(context -> writeTrophiesForMod(context, StringArgumentType.getString(context, "modid"), true, false))
				.then(Commands.argument("check_existing_trophies", BoolArgumentType.bool())
					.executes(context -> writeTrophiesForMod(context, StringArgumentType.getString(context, "modid"), BoolArgumentType.getBool(context, "check_existing_trophies"), false))
					.then(Commands.argument("print_entities", BoolArgumentType.bool())
						.executes(context -> writeTrophiesForMod(context, StringArgumentType.getString(context, "modid"), BoolArgumentType.getBool(context, "check_existing_trophies"), BoolArgumentType.getBool(context, "print_entities"))))));
	}

	private static int writeTrophiesForMod(CommandContext<CommandSourceStack> context, String modid, boolean checkExistingConfigs, boolean printEachEntity) throws CommandSyntaxException {
		if (!modid.equals("all") && !ModList.get().isLoaded(modid)) {
			throw new SimpleCommandExceptionType(Component.translatable("command.obtrophies.mod_not_loaded", modid).withStyle(ChatFormatting.RED)).create();
		}
		int successfulFilesMade = 0;
		for (EntityType<?> entity : BuiltInRegistries.ENTITY_TYPE.stream().filter(type -> (modid.equals("all") || BuiltInRegistries.ENTITY_TYPE.getKey(type).getNamespace().equals(modid)) && checkExistingConfigs != Trophy.getTrophies().containsKey(BuiltInRegistries.ENTITY_TYPE.getKey(type))).toList()) {
			Class<?> instance = getEntityClass(entity);
			if (instance != null && Mob.class.isAssignableFrom(instance) && entity.getCategory() != MobCategory.MISC) {
				Identifier entityName = BuiltInRegistries.ENTITY_TYPE.getKey(entity);
				Path path = context.getSource().getLevel().getServer().getWorldPath(LevelResource.GENERATED_DIR).resolve(entityName.getNamespace()).resolve("trophies").resolve(entityName.getPath() + ".json").normalize();
				Trophy.Builder dummy = new Trophy.Builder(entity);
				if (entity.getTags().anyMatch(tag -> tag.equals(Tags.EntityTypes.BOSSES))) dummy.setDropChance(0.0075D);
				if (entity.getHeight() > 0.0F)
					dummy.setScale(Float.parseFloat(FORMAT.format(Math.min(2.0F, 2.0F / entity.getHeight()))));
				if (TrophiesCommands.writeToFile(Trophy.CODEC.encodeStart(JsonOps.INSTANCE, dummy.build()).resultOrPartial(OpenBlocksTrophies.LOGGER::error).orElseThrow(), path)) {
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

	@SuppressWarnings("unchecked")
	@Nullable
	//this is actual insanity
	public static <T extends Entity> Class<T> getEntityClass(EntityType<T> type) {
		final Class<T> entityClass = (Class<T>) TypeResolver.resolveRawArgument(EntityType.EntityFactory.class, type.factory.getClass());
		if ((Class<?>) entityClass == TypeResolver.Unknown.class) {
			OpenBlocksTrophies.LOGGER.error("Couldn't resolve entity class provided for entity {}", type.getDescriptionId());
			return null;
		}
		return entityClass;
	}
}
