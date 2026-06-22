package com.gizmo.trophies.behavior;

import com.gizmo.trophies.block.AbstractTrophyBlock;
import com.gizmo.trophies.block.entity.TrophyBlockEntity;
import com.gizmo.trophies.init.TrophyBehaviors;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public record ShootProjectileBehavior(ItemStackTemplate projectile, int amount, boolean shootUpwards, Optional<SoundEvent> shootSound) implements CustomBehavior {

	public static final MapCodec<ShootProjectileBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ItemStackTemplate.CODEC.fieldOf("projectile_item").validate(ShootProjectileBehavior::validate).forGetter(ShootProjectileBehavior::projectile),
		Codec.INT.optionalFieldOf("amount", 1).forGetter(ShootProjectileBehavior::amount),
		Codec.BOOL.fieldOf("shoot_upwards").forGetter(ShootProjectileBehavior::shootUpwards),
		BuiltInRegistries.SOUND_EVENT.byNameCodec().optionalFieldOf("shoot_sound").forGetter(ShootProjectileBehavior::shootSound)
	).apply(instance, ShootProjectileBehavior::new));

	private static DataResult<ItemStackTemplate> validate(ItemStackTemplate template) {
		return template.item().value() instanceof ProjectileItem ? DataResult.success(template) : DataResult.error(() -> "Item must implement the ProjectileItem interface");
	}

	public ShootProjectileBehavior() {
		this(new ItemStackTemplate(Items.ARROW), 1, true, Optional.of(SoundEvents.ARROW_SHOOT));
	}

	@Override
	public CustomBehaviorType getType() {
		return TrophyBehaviors.PROJECTILE.get();
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem) {
		BlockPos pos = block.getBlockPos();
		Level level = player.level();
		Direction shootDir = this.shootUpwards() ? Direction.UP : block.getBlockState().getValue(AbstractTrophyBlock.FACING);
		ProjectileItem item = ((ProjectileItem) this.projectile().item().value());

		for (int i = 0; i < this.amount(); i++) {
			Projectile projectile = item.asProjectile(level, Vec3.atCenterOf(pos), this.projectile().create(), shootDir);
			if (projectile instanceof AbstractArrow arrow) {
				arrow.setBaseDamage(0.1D);
				arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
			}
			item.shoot(projectile, shootDir.getStepX(), shootDir.getStepY(), shootDir.getStepZ(), 1.0F, 6.0F);
			//projectile.shoot(level.getRandom().nextInt(10) - 5, 40, level.getRandom().nextInt(10) - 5, 1.0F, 6.0F);
			if (this.shootSound().isPresent()) {
				level.playSound(null, player.blockPosition(), this.shootSound().get(), SoundSource.BLOCKS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + 0.5F);
			}
			level.addFreshEntity(projectile);
		}
		return 10 * this.amount();
	}
}
