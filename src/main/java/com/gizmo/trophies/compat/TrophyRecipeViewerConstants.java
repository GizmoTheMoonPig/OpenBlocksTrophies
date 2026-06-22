package com.gizmo.trophies.compat;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.client.EntityCache;
import com.gizmo.trophies.misc.TranslatableStrings;
import com.gizmo.trophies.trophy.Trophy;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.commons.lang3.StringUtils;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class TrophyRecipeViewerConstants {
	public static final Identifier BACKGROUND = OpenBlocksTrophies.prefix("textures/gui/trophy_jei.png");
	public static final int WIDTH = 116;
	public static final int HEIGHT = 54;

	public static final Component PLAYER_DROP_ONLY = Component.translatable(TranslatableStrings.TROPHY_PLAYER).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY);
	public static final Component FAKE_PLAYER_DROPS = Component.translatable(TranslatableStrings.TROPHY_FAKE_PLAYER).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY);

	public static double getTrophyDropPercentage(Trophy trophy) {
		return OpenBlocksTrophies.getTrophyDropChance(trophy) * 100;
	}

	public static void renderEntity(GuiGraphicsExtractor graphics, @Nullable EntityType<?> type, int x, int y, CompoundTag variant, Optional<CompoundTag> defaultVariant) {
		if (type != null) {
			Entity entity = EntityCache.fetchEntity(type, Minecraft.getInstance().level, variant, defaultVariant);
			if (entity instanceof LivingEntity living) {
				if (entity instanceof AgeableMob mob) {
					mob.setBaby(false);
				}
				int scale = 16;
				float height = entity.getBbHeight();
				float width = entity.getBbWidth();
				//small mobs
				if (height <= 0.9F && width <= 0.6F) {
					scale = (int) (24 + (Math.min(height, width) * 10.0F));
				} else if (width > 1.0F && height <= 0.7F) { //wide mobs
					scale = (int) (16 + (Math.min(height, width) * 10.0F));
				} else if (height >= 2.0F) { //large mobs
					scale = (int)(scale / Math.max(height, width) + Math.min(6, Mth.square(Math.ceil(Math.max(height, width)))));
				}

				// catch exceptions drawing the entity to be safe, any caught exceptions blacklist the entity
				try {
					renderTheEntity(graphics, x, y, scale, living);
				} catch (Exception e) {
					OpenBlocksTrophies.LOGGER.error("Error drawing entity {}", BuiltInRegistries.ENTITY_TYPE.getKey(type), e);
					EntityCache.addEntityToBlacklist(type);
				}
			}
		}
	}

	//[VanillaCopy] of InventoryScreen.renderEntityInInventoryFollowsAngle, with added rotations and some other modified values
	private static void renderTheEntity(GuiGraphicsExtractor graphics, int x, int y, int scale, LivingEntity entity) {
		Quaternionf quaternion = Axis.ZP.rotationDegrees(180.0F);
		quaternion.mul(Axis.XP.rotationDegrees(-20.0F));
		quaternion.mul(Axis.YP.rotationDegrees(-135.0F));
		float f2 = entity.yBodyRot;
		float f3 = entity.getYRot();
		float f4 = entity.getXRot();
		float f5 = entity.yHeadRotO;
		float f6 = entity.yHeadRot;
		entity.yBodyRot = 0.0F;
		entity.setYRot(0.0F);
		entity.setXRot(0.0F);
		entity.yHeadRot = entity.getYRot();
		entity.yHeadRotO = entity.getYRot();

		EntityRenderState renderState = extractRenderState(entity);

		Vector3f translation = new Vector3f(entity.getBbWidth() > 1.0F ? -0.175F : 0.0F, renderState.boundingBoxHeight / 2.0F + 0.15F, 0.0F);
		scale = applyAdditionalTransforms(entity.getType(), translation, quaternion, scale);

		graphics.entity(renderState, scale * 0.75F, translation, quaternion, new Quaternionf(), x, y, x + 32, y + 32);

		entity.yBodyRot = f2;
		entity.setYRot(f3);
		entity.setXRot(f4);
		entity.yHeadRotO = f5;
		entity.yHeadRot = f6;
	}

	private static EntityRenderState extractRenderState(LivingEntity entity) {
		EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		EntityRenderer<? super LivingEntity, ?> renderer = entityRenderDispatcher.getRenderer(entity);
		EntityRenderState renderState = renderer.createRenderState(entity, 0.0F);
		renderState.shadowPieces.clear();
		renderState.outlineColor = 0;
		return renderState;
	}

	//certain entities are a pain. This exists to fix vanilla cases.
	private static int applyAdditionalTransforms(EntityType<?> entity, Vector3f translation, Quaternionf rotation, float scale) {
		if (entity == EntityType.GHAST || entity == EntityType.HAPPY_GHAST) {
			translation.add(0.0F, -1.25F, 0.0F);
			scale *= 0.5F;
		}
		if (entity == EntityType.ENDER_DRAGON) {
			translation.add(0.0F, -2.0F, 0.0F);
			rotation.mul(Axis.YP.rotationDegrees(180.0F));
			scale *= 0.5F;
		}
		if (entity == EntityType.WITHER) translation.add(0.0F, 0.25F, 0.0F);
		if (entity == EntityType.SQUID || entity == EntityType.GLOW_SQUID) translation.add(0.0F, -0.75F, 0.0F);
		return Math.round(scale);
	}

	public static List<Component> getMobTooltip(EntityType<?> type) {
		List<Component> components = new ArrayList<>();
		components.add(type.getDescription());
		if (Minecraft.getInstance().options.advancedItemTooltips) {
			components.add(Component.literal(BuiltInRegistries.ENTITY_TYPE.getKey(type).toString()).withStyle(ChatFormatting.DARK_GRAY));
		}
		return components;
	}

	public static String getModIdForTooltip(String modId) {
		return ModList.get().getModContainerById(modId)
			.map(ModContainer::getModInfo)
			.map(IModInfo::getDisplayName)
			.orElseGet(() -> StringUtils.capitalize(modId));
	}
}
