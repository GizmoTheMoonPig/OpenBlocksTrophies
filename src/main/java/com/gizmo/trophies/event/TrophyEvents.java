package com.gizmo.trophies.event;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.config.TrophyConfig;
import com.gizmo.trophies.init.TrophyComponents;
import com.gizmo.trophies.init.TrophyItems;
import com.gizmo.trophies.item.TrophyHelper;
import com.gizmo.trophies.network.SyncTrophyConfigsPacket;
import com.gizmo.trophies.trophy.Trophy;
import com.mojang.logging.LogUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.npc.villager.VillagerDataHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class TrophyEvents {

	private static final Logger LOGGER = LogUtils.getLogger();
	public static final RandomSource TROPHY_RANDOM = RandomSource.create();

	public static void syncTrophiesToClient(OnDatapackSyncEvent event) {
		if (event.getPlayer() != null) {
			PacketDistributor.sendToPlayer(event.getPlayer(), new SyncTrophyConfigsPacket(Trophy.getTrophies()));
			OpenBlocksTrophies.LOGGER.debug("Sent {} trophy configs to {} from server.", Trophy.getTrophies().size(), event.getPlayer().getDisplayName().getString());
		} else {
			event.getPlayerList().getPlayers().forEach(player -> {
				PacketDistributor.sendToPlayer(player, new SyncTrophyConfigsPacket(Trophy.getTrophies()));
				OpenBlocksTrophies.LOGGER.debug("Sent {} trophy configs to {} from server.", Trophy.getTrophies().size(), player.getDisplayName().getString());
			});
		}
	}

	//TODO unhardcode into new system.
	//
	public static void grantAdvancementBasedTrophies(AdvancementEvent.AdvancementEarnEvent event) {
		if (ModList.get().isLoaded("the_bumblezone")) {
			if (event.getAdvancement().id().equals(Identifier.fromNamespaceAndPath("the_bumblezone", "the_bumblezone/the_queens_desire/journeys_end"))) {
				ItemStack trophy = TrophyHelper.loadEntityToTrophy(Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.fromNamespaceAndPath("the_bumblezone", "bee_queen")))).create();
				if (event.getEntity().addItem(trophy)) {
					event.getEntity().drop(trophy, false);
				}
			}
			if (event.getAdvancement().id().equals(Identifier.fromNamespaceAndPath("the_bumblezone", "the_bumblezone/beehemoth/queen_beehemoth"))) {
				ItemStack trophy = TrophyHelper.loadVariantToTrophy(Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.fromNamespaceAndPath("the_bumblezone", "beehemoth"))), Util.make(new CompoundTag(), tag -> tag.putBoolean("queen", true))).create();
				if (event.getEntity().addItem(trophy)) {
					event.getEntity().drop(trophy, false);
				}
			}
		}
	}

	public static void maybeDropTrophy(LivingDropsEvent event) {
		//follow gamerules and mob drop requirements
		if (!(event.getEntity().level() instanceof ServerLevel level) || !event.getEntity().shouldDropLoot(level))
			return;

		//players are a bit special.
		//charged creepers can make players drop trophies, and player trophies come loaded with the dead player's name
		if (event.getEntity() instanceof Player player) {
			double dropChance;
			if (event.getSource().getEntity() instanceof Creeper creeper && creeper.isPowered() && TrophyConfig.playerChargedCreeperDropChance > 0.0D) {
				dropChance = TrophyConfig.playerChargedCreeperDropChance - TROPHY_RANDOM.nextDouble();
			} else {
				//don't drop trophies if the config doesn't allow this source to
				if (!(event.getSource().getEntity() instanceof Player) && TrophyConfig.trophyDropSource != TrophyConfig.TrophySourceDrop.ALL)
					return;
				if (event.getSource().getEntity() instanceof FakePlayer && TrophyConfig.trophyDropSource != TrophyConfig.TrophySourceDrop.FAKE_PLAYER)
					return;
				Trophy trophy = Trophy.getTrophies().getOrDefault(BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.PLAYER), new Trophy.Builder(EntityType.PLAYER).build());
				dropChance = ((getLootingLevel(level, event.getSource()) + (TROPHY_RANDOM.nextDouble() / 4)) * OpenBlocksTrophies.getTrophyDropChance(trophy)) - TROPHY_RANDOM.nextDouble();
			}
			if (dropChance > 0.0D) {
				ItemStack stack = TrophyHelper.loadEntityToTrophy(EntityType.PLAYER).create();
				stack.set(DataComponents.ITEM_NAME, Component.literal(player.getDisplayName().getString()));
				stack.set(DataComponents.PROFILE, ResolvableProfile.createResolved(player.getGameProfile()));
				stack.set(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT.withHidden(DataComponents.PROFILE, true));
				event.getDrops().add(new ItemEntity(level, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), stack));
			}
		} else {
			//don't drop trophies if the config doesn't allow this source to
			if (!(event.getSource().getEntity() instanceof Player) && TrophyConfig.trophyDropSource != TrophyConfig.TrophySourceDrop.ALL)
				return;
			if (event.getSource().getEntity() instanceof FakePlayer && TrophyConfig.trophyDropSource != TrophyConfig.TrophySourceDrop.FAKE_PLAYER)
				return;

			if (Trophy.getTrophies().containsKey(BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType()))) {
				Trophy trophy = Trophy.getTrophies().get(BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType()));
				if (trophy != null) {
					double chance = ((getLootingLevel(level, event.getSource()) + (TROPHY_RANDOM.nextDouble() / 4)) * OpenBlocksTrophies.getTrophyDropChance(trophy)) - TROPHY_RANDOM.nextDouble();
					if (chance > 0.0D) {
						event.getDrops().add(new ItemEntity(level, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), TrophyHelper.loadVariantToTrophy(trophy.type(), fetchVariantIfAny(event.getEntity(), trophy)).create()));
					}
				}
			}
		}
	}

	//[VanillaCopy] of EnchantmentHelper.processEquipmentDropChance (the first loop), but actually returns the level instead of just processing it as a chance
	private static int getLootingLevel(ServerLevel level, DamageSource source) {
		AtomicInteger looting = new AtomicInteger();
		if (source.getEntity() instanceof LivingEntity living) {
			EnchantmentHelper.runIterationOnEquipment(living, (enchantment, i, item) -> {
				LootContext lootcontext = Enchantment.damageContext(level, i, living, source);
				enchantment.value().getEffects(EnchantmentEffectComponents.EQUIPMENT_DROPS).forEach(effect -> {
					if (effect.enchanted() == EnchantmentTarget.ATTACKER && effect.affected() == EnchantmentTarget.VICTIM && effect.matches(lootcontext)) {
						int realLevel = EventHooks.getEntityLootEnchantmentLevel(enchantment, i, lootcontext);
						if (realLevel == 0) return;
						looting.addAndGet(realLevel);
					}
				});
			});
		}
		return looting.get();
	}

	private static CompoundTag fetchVariantIfAny(LivingEntity entity, Trophy trophy) {
		List<CompoundTag> possibleVariants = trophy.getVariants(entity.level().registryAccess());
		if (!possibleVariants.isEmpty()) {
			try (ProblemReporter.ScopedCollector collector = new ProblemReporter.ScopedCollector(entity.problemPath(), LOGGER)) {
				TagValueOutput output = TagValueOutput.createWithContext(collector, entity.registryAccess());
				entity.saveWithoutId(output);

				CompoundTag tag = output.buildResult();
				for (CompoundTag currentVariant : possibleVariants) {
					boolean matchingTag = true;
					for (String s : currentVariant.keySet()) {
						//villager-like mobs are a special case.
						//Due to 2 parts of the villager data using a registry (type and profession), I cant exactly check these via the registry variant stuff.
						//TODO might consider a custom parser for this case but for now it can remain
						if (entity instanceof VillagerDataHolder villager) {
							if (s.equals("profession") && !villager.getVillagerData().profession().is(Identifier.parse(currentVariant.getStringOr(s, "")))) {
								matchingTag = false;
								break;
							} else if (s.equals("type") && !villager.getVillagerData().type().is(Identifier.parse(currentVariant.getStringOr(s, "")))) {
								matchingTag = false;
								break;
							}
						} else {
							//we need to ensure ALL defined tags match our entity since jsons can define as many as they want to
							if (!tag.contains(s) || tag.get(s) != currentVariant.get(s)) {
								matchingTag = false;
								break;
							}
						}
					}
					if (matchingTag) {
						return currentVariant;
					}
				}
			}
		}
		return new CompoundTag();
	}

	//fixes a silly issue where the old player skin is still shown while renaming due to the profile not updating until you pick up the item
	public static void dontVisuallyShowSkinsWhileRenaming(AnvilUpdateEvent event) {
		if (event.getOutput().is(TrophyItems.TROPHY) && event.getOutput().has(DataComponents.PROFILE)) {
			Trophy trophy = TrophyHelper.getTrophy(event.getOutput().getComponents().get(TrophyComponents.TROPHY_INFO));
			if (trophy != null && trophy.type() == EntityType.PLAYER) {
				event.getOutput().remove(DataComponents.PROFILE);
			}
		}
	}
}
