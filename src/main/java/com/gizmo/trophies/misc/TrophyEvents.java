package com.gizmo.trophies.misc;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.config.TrophyConfig;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.network.SyncTrophyConfigsPacket;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class TrophyEvents {

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

	public static void grantAdvancementBasedTrophies(AdvancementEvent.AdvancementEarnEvent event) {
		if (ModList.get().isLoaded("the_bumblezone")) {
			if (event.getAdvancement().id().equals(ResourceLocation.fromNamespaceAndPath("the_bumblezone", "the_bumblezone/the_queens_desire/journeys_end"))) {
				ItemStack trophy = TrophyItem.loadEntityToTrophy(Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getValue(ResourceLocation.fromNamespaceAndPath("the_bumblezone", "bee_queen"))));
				if (event.getEntity().addItem(trophy)) {
					event.getEntity().drop(trophy, false);
				}
			}
			if (event.getAdvancement().id().equals(ResourceLocation.fromNamespaceAndPath("the_bumblezone", "the_bumblezone/beehemoth/queen_beehemoth"))) {
				ItemStack trophy = TrophyItem.loadVariantToTrophy(Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getValue(ResourceLocation.fromNamespaceAndPath("the_bumblezone", "beehemoth"))), Util.make(new CompoundTag(), tag -> tag.putBoolean("queen", true)));
				if (event.getEntity().addItem(trophy)) {
					event.getEntity().drop(trophy, false);
				}
			}
		}
	}

	public static void maybeDropTrophy(LivingDropsEvent event) {
		//follow gamerules and mob drop requirements
		if (!(event.getEntity().level() instanceof ServerLevel level) || !level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT) || !event.getEntity().shouldDropLoot())
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
				ItemStack stack = TrophyItem.loadEntityToTrophy(EntityType.PLAYER);
				stack.set(DataComponents.ITEM_NAME, Component.literal(player.getDisplayName().getString()));
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
						event.getDrops().add(new ItemEntity(level, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), TrophyItem.loadVariantToTrophy(trophy.type(), fetchVariantIfAny(event.getEntity(), trophy))));
					}
				}
			}
		}
	}

	private static int getLootingLevel(ServerLevel level, DamageSource source) {
		AtomicInteger looting = new AtomicInteger();
		if (source.getEntity() instanceof LivingEntity living) {
			EnchantmentHelper.runIterationOnEquipment(living, (enchantment, i, item) -> {
				LootContext lootcontext = Enchantment.damageContext(level, i, living, source);
				enchantment.value().getEffects(EnchantmentEffectComponents.EQUIPMENT_DROPS).forEach(effect -> {
					if (effect.enchanted() == EnchantmentTarget.ATTACKER && effect.affected() == EnchantmentTarget.VICTIM && effect.matches(lootcontext)) {
						looting.addAndGet(i);
					}
				});
			});
		}
		return looting.get();
	}

	private static CompoundTag fetchVariantIfAny(LivingEntity entity, Trophy trophy) {
		if (!trophy.getVariants(entity.level().registryAccess()).isEmpty()) {
			CompoundTag tag = new CompoundTag();
			entity.addAdditionalSaveData(tag);
			for (int i = 0; i < trophy.getVariants(entity.level().registryAccess()).size(); i++) {
				CompoundTag variantKeys = trophy.getVariants(entity.level().registryAccess()).get(i);
				for (String s : variantKeys.getAllKeys()) {
					if (entity instanceof VillagerDataHolder villager) {
						if (BuiltInRegistries.VILLAGER_PROFESSION.getKey(villager.getVillagerData().getProfession()).toString().equals(variantKeys.getString(s))) {
							return variantKeys;
						}
					} else {
						Tag tagVer = tag.get(s);
						Tag variantVer = variantKeys.get(s);
						if (variantVer instanceof NumericTag num) {
							//most values save as bytes in the json, but sometimes they also be things like shorts.
							//we'll compare both numbers to long as they should always match this way.
							//comparing to int is going to cause issues for doubles
							if (tagVer instanceof NumericTag number && number.getAsLong() == num.getAsLong()) {
								return variantKeys;
							}
						} else if (Objects.equals(tagVer, variantVer)) {
							return variantKeys;
						}
					}
				}
			}
		}
		return new CompoundTag();
	}
}
