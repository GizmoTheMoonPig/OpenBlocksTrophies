package com.gizmo.trophies.behavior;

import com.gizmo.trophies.block.AbstractTrophyBlock;
import com.gizmo.trophies.block.TrophyBlock;
import com.gizmo.trophies.block.entity.TrophyBlockEntity;
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
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public record ShootProjectileBehavior(ItemStack projectile, int amount, boolean shootUpwards, Optional<SoundEvent> shootSound) implements CustomBehavior {

	public static final MapCodec<ShootProjectileBehavior> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ItemStack.SINGLE_ITEM_CODEC.fieldOf("projectile_item").validate(ShootProjectileBehavior::validate).forGetter(ShootProjectileBehavior::projectile),
		Codec.INT.optionalFieldOf("amount", 1).forGetter(ShootProjectileBehavior::amount),
		Codec.BOOL.fieldOf("shoot_upwards").forGetter(ShootProjectileBehavior::shootUpwards),
		BuiltInRegistries.SOUND_EVENT.byNameCodec().optionalFieldOf("shoot_sound").forGetter(ShootProjectileBehavior::shootSound)
	).apply(instance, ShootProjectileBehavior::new));

	private static DataResult<ItemStack> validate(ItemStack stack) {
		return stack.getItem() instanceof ProjectileItem ? DataResult.success(stack) : DataResult.error(() -> "Item must implement the ProjectileItem interface");
	}

	public ShootProjectileBehavior() {
		this(new ItemStack(Items.ARROW), 1, true, Optional.of(SoundEvents.ARROW_SHOOT));
	}

	@Override
	public CustomBehaviorType getType() {
		return CustomTrophyBehaviors.PROJECTILE.get();
	}

	@Override
	public int execute(TrophyBlockEntity block, ServerPlayer player, ItemStack usedItem) {
		BlockPos pos = block.getBlockPos();
		Level level = player.level();
		Direction shootDir = this.shootUpwards() ? Direction.UP : block.getBlockState().getValue(AbstractTrophyBlock.FACING);
		ProjectileItem item = ((ProjectileItem) this.projectile().getItem());

		for (int i = 0; i < this.amount(); i++) {
			Projectile projectile = item.asProjectile(level, Vec3.atCenterOf(pos), this.projectile(), shootDir);
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
