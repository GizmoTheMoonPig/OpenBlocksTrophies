package com.gizmo.trophies.mixin;

import com.gizmo.trophies.init.TrophyComponents;
import com.gizmo.trophies.init.TrophyItems;
import com.gizmo.trophies.item.TrophyHelper;
import com.gizmo.trophies.trophy.Trophy;
import com.mojang.util.UndashedUuid;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public class AnvilMenuMixin {

	@Shadow(remap = false)
	private @Nullable String itemName;

	@Inject(method = "onTake", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/ContainerLevelAccess;execute(Ljava/util/function/BiConsumer;)V", shift = At.Shift.AFTER), remap = false)
	public void setProfileOnTake(Player player, ItemStack output, CallbackInfo ci) {
		if (output.is(TrophyItems.TROPHY) && this.itemName != null) {
			Trophy trophy = TrophyHelper.getTrophy(output.getComponents().get(TrophyComponents.TROPHY_INFO));
			if (trophy != null && trophy.type() == EntityType.PLAYER) {
				ResolvableProfile profile = trophies$fetchProfile(this.itemName);
				if (profile != null) {
					output.remove(DataComponents.CUSTOM_NAME);
					output.set(DataComponents.ITEM_NAME, Component.literal(this.itemName));
					output.set(DataComponents.PROFILE, profile);
					output.set(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT.withHidden(DataComponents.PROFILE, true));
				}
			}
		}
	}

	@Unique
	@Nullable
	private static ResolvableProfile trophies$fetchProfile(@Nullable String input) {
		if (input != null) {
			input = input.trim();
		}

		if (input != null && !input.isBlank()) {
			try {
				if (StringUtil.isValidPlayerName(input)) {
					return ResolvableProfile.createUnresolved(input);
				}
			} catch (IllegalArgumentException _) {
			}

			try {
				return ResolvableProfile.createUnresolved(UndashedUuid.fromStringLenient(input));
			} catch (IllegalArgumentException var2) {
				return null;
			}
		} else {
			return null;
		}
	}
}
