package com.gizmo.trophies.client;

import com.gizmo.trophies.block.TrophyBlock;
import com.gizmo.trophies.block.TrophyBlockEntity;
import com.gizmo.trophies.trophy.Trophy;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TrophyRenderer implements BlockEntityRenderer<TrophyBlockEntity> {

	private static List<ResourceLocation> keys = new ArrayList<>();

	private static final LoadingCache<EntityContext<?>, Entity> ENTITY_CACHE = CacheBuilder.newBuilder().build(
			new CacheLoader<>() {
				@Override
				public Entity load(EntityContext<?> context) {
					return context.createEntity();
				}
			}
	);

	public TrophyRenderer(BlockEntityRendererProvider.Context unused) {
	}

	public static void renderEntity(@Nullable TrophyBlockEntity be, int variant, String name, Level level, BlockPos pos, Trophy trophy, PoseStack stack, MultiBufferSource source, int light, boolean cycling) {
		stack.pushPose();
		if (keys.isEmpty() && !Trophy.getTrophies().isEmpty()) {
			keys = Trophy.getTrophies().keySet().stream().toList();
		}
		if (cycling && !keys.isEmpty()) {
			trophy = Trophy.getTrophies().get(keys.get((int) (level.getGameTime() / 20 % keys.size())));
		}
		Entity entity = fetchEntity(trophy.getType(), level);
		EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		boolean hitboxes = dispatcher.shouldRenderHitBoxes();
		dispatcher.setRenderShadow(false);
		dispatcher.setRenderHitBoxes(false);
		entity.setYRot(0.0F);
		entity.setYHeadRot(0.0F);
		entity.setYBodyRot(0.0F);
		entity.setOnGround(true);
		entity.hasImpulse = false;
		if (entity instanceof Mob mob) {
			mob.setNoAi(true);
		}
		entity.setCustomName(!name.isEmpty() ? Component.literal(name) : null);
		entity.setCustomNameVisible(false);
		//tick named sheep so the jeb_ name easter egg works properly. Lucky us the sheep doesnt need the tickCount for anything animation related so this works well.
		//I cant do this for every mob because mobs such as the blaze or pufferfish move when the tickCount is incremented, and I HATE moving trophies
		if (entity instanceof Sheep && entity.hasCustomName()) {
			entity.tickCount = (int) level.getLevelData().getGameTime();
		} else {
			entity.tickCount = 0;
		}
		entity.setPos(pos.getX() + 0.5D, pos.getY() + 0.25D + trophy.getVerticalOffset(), pos.getZ() + 0.5D);
		stack.translate(0.5F, 0.25D + trophy.getVerticalOffset(), 0.5F);
		if (be != null) {
			//they watch
			if (LocalDate.of(LocalDate.now().getYear(), 4, 1).equals(LocalDate.now())) {
				Player player = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 64.0D, false);
				if (player != null) {
					Vec3 vec3 = new Vec3(player.getX(), player.getEyeY(), player.getZ());
					Vec3 vec31 = new Vec3(pos.getX(), pos.getY() + (entity.getEyeHeight() / 1.5), pos.getZ());
					Vec3 vec32 = vec3.subtract(vec31);
					vec32 = vec32.normalize();
					float f5 = (float) Math.acos(vec32.y());
					float f6 = (float) Math.atan2(vec32.z(), vec32.x());
					stack.mulPose(Axis.YP.rotationDegrees((Mth.HALF_PI - f6) * Mth.RAD_TO_DEG));
					stack.mulPose(Axis.XP.rotationDegrees(f5 * (180F / (float) Math.PI) - 90.0F));
				}
			} else {
				stack.mulPose(Axis.YP.rotationDegrees(getCorrectRotation(be.getBlockState().getValue(TrophyBlock.FACING).getOpposite())));
			}
		}

		if (entity instanceof EnderDragon) {
			stack.mulPose(Axis.YP.rotationDegrees(180.0F));
		}

		stack.scale(0.4F, 0.4F, 0.4F);
		stack.scale(trophy.getScale(), trophy.getScale(), trophy.getScale());
		if (TrophyExtraRendering.getRenderMap().containsKey(trophy.getType())) {
			TrophyExtraRendering.getRenderForEntity(trophy.getType()).createExtraRender(entity);
		}
		if (!trophy.getVariants(Minecraft.getInstance().level.registryAccess()).isEmpty() && entity instanceof LivingEntity living) {
			if (entity instanceof VillagerDataHolder villager) {
				trophy.getVariants(Minecraft.getInstance().level.registryAccess()).get(variant).forEach((s, s2) -> villager.setVillagerData(new VillagerData(VillagerType.PLAINS, Objects.requireNonNull(Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.VILLAGER_PROFESSION).get(ResourceLocation.tryParse(s2))), 1)));
			} else {
				CompoundTag tag = new CompoundTag();
				trophy.getVariants(Minecraft.getInstance().level.registryAccess()).get(Mth.clamp(variant, 0, trophy.getVariants(Minecraft.getInstance().level.registryAccess()).size() - 1)).forEach((s, s2) -> convertStringToProperPrimitive(tag, s, s2));
				living.readAdditionalSaveData(tag);
			}
		}

		//hate everything about this
		if (ModList.get().isLoaded("alexsmobs")) {
//			RenderLaviathan.renderWithoutShaking = true;
//			RenderMurmurBody.renderWithHead = true;
//			if (entity instanceof EntityLaviathan leviathan) {
//				leviathan.prevHeadHeight = 0.0F;
//				leviathan.setChillTime(0);
//			}
		}

		RenderSystem.runAsFancy(() -> dispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, stack, source, light));
		dispatcher.setRenderShadow(true);
		dispatcher.setRenderHitBoxes(hitboxes);

		if (ModList.get().isLoaded("alexsmobs")) {
//			RenderLaviathan.renderWithoutShaking = false;
//			RenderMurmurBody.renderWithHead = false;
		}

		stack.popPose();
	}

	public static void convertStringToProperPrimitive(CompoundTag tag, String variantID, String primitive) {
		if (StringUtils.isNumeric(primitive)) {
			tag.putInt(variantID, Integer.parseInt(primitive));
		} else if (primitive.equals("false") || primitive.equals("true")) {
			tag.putBoolean(variantID, Boolean.parseBoolean(primitive));
		} else {
			tag.putString(variantID, primitive);
		}
	}

	private static float getCorrectRotation(Direction direction) {
		return switch (direction) {
			case DOWN, UP, NORTH -> 0.0F;
			case SOUTH -> 180.0F;
			case EAST -> 270.0F;
			case WEST -> 90.0F;
		};
	}

	@Nullable
	public static <T extends Entity> Entity fetchEntity(@Nullable EntityType<T> type, Level level) {
		if (type == null)
			return null;
		return ENTITY_CACHE.getUnchecked(EntityContext.of(type, level));
	}

	@Override
	public void render(TrophyBlockEntity blockEntity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		if (blockEntity.getTrophy() != null) {
			stack.pushPose();
			if (!blockEntity.getBlockState().getValue(TrophyBlock.PEDESTAL)) {
				stack.translate(0.0D, -0.25D, 0.0D);
			}
			renderEntity(blockEntity, blockEntity.getVariant(), blockEntity.getTrophyName(), blockEntity.getLevel(), blockEntity.getBlockPos(), blockEntity.getTrophy(), stack, source, light, blockEntity.isCycling());
			stack.popPose();
		}
	}

	public record EntityContext<T extends Entity>(EntityType<T> type, Level level) {
		public static <T extends Entity> EntityContext<T> of(EntityType<T> type, Level level) {
			return new EntityContext<>(type, level);
		}

		public T createEntity() {
			return Objects.requireNonNull(this.type.create(this.level));
		}
	}
}
