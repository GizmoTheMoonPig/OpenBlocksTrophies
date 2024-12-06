package com.gizmo.trophies.command;

import com.gizmo.trophies.misc.TrophyRegistries;
import com.gizmo.trophies.trophy.DisplayTrophy;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public class CreateDisplayTrophyCommand {

	public static LiteralArgumentBuilder<CommandSourceStack> register(CommandBuildContext context) {
		return Commands.literal("createdisplay")
			.then(Commands.argument("targets", EntityArgument.players())
				.then(Commands.argument("item", ItemArgument.item(context))
					.executes(commandContext -> createDisplayTrophy(commandContext, EntityArgument.getPlayers(commandContext, "targets"), ItemArgument.getItem(commandContext, "item").getItem(), 1.0F, Vec3.ZERO, Vec3.ZERO, 0.0F, false, null))
					.then(Commands.argument("scale", FloatArgumentType.floatArg())
						.executes(commandContext -> createDisplayTrophy(commandContext, EntityArgument.getPlayers(commandContext, "targets"), ItemArgument.getItem(commandContext, "item").getItem(), FloatArgumentType.getFloat(commandContext, "scale"), Vec3.ZERO, Vec3.ZERO, 0.0F, false, null))
						.then(Commands.argument("offset", Vec3Argument.vec3(false))
							.executes(commandContext -> createDisplayTrophy(commandContext, EntityArgument.getPlayers(commandContext, "targets"), ItemArgument.getItem(commandContext, "item").getItem(), FloatArgumentType.getFloat(commandContext, "scale"), Vec3Argument.getVec3(commandContext, "offset"), Vec3.ZERO, 0.0F, false, null))
							.then(Commands.argument("rotation", Vec3Argument.vec3(false))
								.executes(commandContext -> createDisplayTrophy(commandContext, EntityArgument.getPlayers(commandContext, "targets"), ItemArgument.getItem(commandContext, "item").getItem(), FloatArgumentType.getFloat(commandContext, "scale"), Vec3Argument.getVec3(commandContext, "offset"), Vec3Argument.getVec3(commandContext, "rotation"), 0.0F, false, null))
								.then(Commands.argument("rotation_speed", FloatArgumentType.floatArg())
									.executes(commandContext -> createDisplayTrophy(commandContext, EntityArgument.getPlayers(commandContext, "targets"), ItemArgument.getItem(commandContext, "item").getItem(), FloatArgumentType.getFloat(commandContext, "scale"), Vec3Argument.getVec3(commandContext, "offset"), Vec3Argument.getVec3(commandContext, "rotation"), FloatArgumentType.getFloat(commandContext, "rotation_speed"), false, null))
									.then(Commands.argument("bob", BoolArgumentType.bool())
										.executes(commandContext -> createDisplayTrophy(commandContext, EntityArgument.getPlayers(commandContext, "targets"), ItemArgument.getItem(commandContext, "item").getItem(), FloatArgumentType.getFloat(commandContext, "scale"), Vec3Argument.getVec3(commandContext, "offset"), Vec3Argument.getVec3(commandContext, "rotation"), FloatArgumentType.getFloat(commandContext, "rotation_speed"), BoolArgumentType.getBool(commandContext, "bob"), null))
										.then(Commands.argument("sound", ResourceLocationArgument.id())
											.executes(commandContext -> createDisplayTrophy(commandContext, EntityArgument.getPlayers(commandContext, "targets"), ItemArgument.getItem(commandContext, "item").getItem(), FloatArgumentType.getFloat(commandContext, "scale"), Vec3Argument.getVec3(commandContext, "offset"), Vec3Argument.getVec3(commandContext, "rotation"), FloatArgumentType.getFloat(commandContext, "rotation_speed"), BoolArgumentType.getBool(commandContext, "bob"), ResourceLocationArgument.getId(commandContext, "sound")))))))))));
	}

	public static int createDisplayTrophy(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets, Item displayItem, float scale, Vec3 offset, Vec3 rotation, float rotationSpeed, boolean bob, @Nullable ResourceLocation rightClickSound) {
		DisplayTrophy trophy = new DisplayTrophy(displayItem, scale, offset, rotation, rotationSpeed, bob, Optional.ofNullable(BuiltInRegistries.SOUND_EVENT.getValue(rightClickSound)));
		for (ServerPlayer serverplayer : targets) {
			ItemStack newStack = new ItemStack(TrophyRegistries.DISPLAY_TROPHY_ITEM.get());
			newStack.set(TrophyRegistries.DISPLAY_TROPHY_INFO, trophy);
			newStack.set(DataComponents.RARITY, trophy.displayItem().getDefaultInstance().getRarity());
			if (serverplayer.getInventory().add(newStack)) {
				ItemEntity itementity = serverplayer.drop(newStack, false);
				if (itementity != null) {
					itementity.setNoPickUpDelay();
					itementity.setTarget(serverplayer.getUUID());
				}
			}
		}

		//TODO command output feedback

		return targets.size();
	}
}
