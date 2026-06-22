package com.gizmo.trophies.event;

import com.gizmo.trophies.criteria.SpecialTrophyCriteria;
import com.gizmo.trophies.criteria.TrophyAdvancementCriteria;
import com.gizmo.trophies.criteria.TrophyInteractionCriteria;
import com.gizmo.trophies.criteria.TrophySpecialKillCriteria;
import com.gizmo.trophies.init.TrophyCriteria;
import com.gizmo.trophies.item.TrophyHelper;
import com.gizmo.trophies.trophy.listener.TrophyCriteriaReloadListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class CriteriaEvents {

	public static void grantAdvancementTrophies(AdvancementEvent.AdvancementEarnEvent event) {
		if (!(event.getEntity() instanceof FakePlayer)) {
			for (SpecialTrophyCriteria criteria : TrophyCriteriaReloadListener.getCriteriaMapper().get(TrophyCriteria.ADVANCEMENT.get())) {
				if (event.getAdvancement().id().equals(((TrophyAdvancementCriteria) criteria).getAdvancement())) {
					event.getEntity().getInventory().placeItemBackInInventory(TrophyHelper.loadVariantToTrophy(criteria.entity(), criteria.variant().orElse(new CompoundTag())).create());
				}
			}
		}
	}

	public static void grantInteractionTrophies(PlayerInteractEvent.EntityInteract event) {
		if (!(event.getEntity() instanceof FakePlayer)) {
			for (SpecialTrophyCriteria criteria : TrophyCriteriaReloadListener.getCriteriaMapper().get(TrophyCriteria.INTERACTION.get())) {
				TrophyInteractionCriteria interaction = (TrophyInteractionCriteria) criteria;
				if (event.getTarget().is(criteria.entity()) && interaction.getUseItem().test(event.getItemStack())) {
					if (interaction.shouldShrinkUseItem()) {
						event.getItemStack().consume(1, event.getEntity());
					}

					if (interaction.interactionSound() != null) {
						event.getTarget().playSound(interaction.interactionSound());
					}
					event.getEntity().getInventory().placeItemBackInInventory(TrophyHelper.loadVariantToTrophy(criteria.entity(), criteria.variant().orElse(new CompoundTag())).create());
				}
			}
		}
	}

	public static void grantSpecialKillTrophies(LivingDropsEvent event) {
		if (!(event.getSource().getEntity() instanceof FakePlayer)) {
			for (SpecialTrophyCriteria criteria : TrophyCriteriaReloadListener.getCriteriaMapper().get(TrophyCriteria.SPECIAL_KILL.get())) {
				TrophySpecialKillCriteria kill = (TrophySpecialKillCriteria) criteria;
				if (event.getEntity().is(criteria.entity()) && event.getSource().getEntity() instanceof ServerPlayer player && kill.getSource().matches(player, event.getSource())) {
					if (TrophyEvents.TROPHY_RANDOM.nextFloat() <= kill.getDropChance()) {
						event.getDrops().add(new ItemEntity(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), TrophyHelper.loadVariantToTrophy(criteria.entity(), criteria.variant().orElse(new CompoundTag())).create()));
					}
				}
			}
		}
	}
}
