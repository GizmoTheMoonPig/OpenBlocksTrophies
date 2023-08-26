package com.gizmo.trophies;

import com.gizmo.trophies.behavior.CustomTrophyBehaviors;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

@Mod(OpenBlocksTrophies.MODID)
public class OpenBlocksTrophies {
	public static final String MODID = "obtrophies";

	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final RandomSource TROPHY_RANDOM = RandomSource.create();

	public OpenBlocksTrophies() {
		{
			final Pair<TrophyConfig.CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(TrophyConfig.CommonConfig::new);
			ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, specPair.getRight());
			TrophyConfig.COMMON_CONFIG = specPair.getLeft();
		}

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::commonSetup);
		MinecraftForge.EVENT_BUS.addListener(this::maybeDropTrophy);
		MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
		MinecraftForge.EVENT_BUS.addListener(Trophy::reloadTrophies);
		MinecraftForge.EVENT_BUS.addListener(Trophy::syncTrophiesToClient);

		MinecraftForge.EVENT_BUS.addListener(this::grantBeeQueenViaDesireAdvancement);

		TrophyRegistries.BLOCKS.register(bus);
		TrophyRegistries.BLOCK_ENTITIES.register(bus);
		TrophyRegistries.ITEMS.register(bus);
		TrophyRegistries.LOOT_MODIFIERS.register(bus);
		TrophyRegistries.TABS.register(bus);

		CustomTrophyBehaviors.CUSTOM_BEHAVIORS.register(bus);
	}

	public static ResourceLocation location(String path) {
		return new ResourceLocation(MODID, path);
	}

	public void commonSetup(FMLCommonSetupEvent event) {
		TrophyNetworkHandler.init();
	}

	public void registerCommands(RegisterCommandsEvent event) {
		TrophiesCommands.register(event.getDispatcher());
	}

	public void grantBeeQueenViaDesireAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
		if (ModList.get().isLoaded("the_bumblezone")) {
			if (event.getAdvancement().getId().equals(new ResourceLocation("the_bumblezone", "the_bumblezone/the_queens_desire/journeys_end"))) {
				ItemStack trophy = TrophyItem.loadEntityToTrophy(Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("the_bumblezone", "bee_queen"))), 0, false);
				if (event.getEntity().addItem(trophy)) {
					event.getEntity().drop(trophy, false);
				}
			}
			if (event.getAdvancement().getId().equals(new ResourceLocation("the_bumblezone", "the_bumblezone/beehemoth/queen_beehemoth"))) {
				ItemStack trophy = TrophyItem.loadEntityToTrophy(Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation("the_bumblezone", "beehemoth"))), 1, false);
				if (event.getEntity().addItem(trophy)) {
					event.getEntity().drop(trophy, false);
				}
			}
		}
	}

	public void maybeDropTrophy(LivingDropsEvent event) {
		if (!(event.getSource().getEntity() instanceof Player) && !TrophyConfig.COMMON_CONFIG.anySourceDropsTrophies.get())
			return;
		if (event.getSource().getEntity() instanceof FakePlayer && !TrophyConfig.COMMON_CONFIG.fakePlayersDropTrophies.get())
			return;

		if (Trophy.getTrophies().containsKey(ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType()))) {
			Trophy trophy = Trophy.getTrophies().get(ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType()));
			if (trophy != null) {
				double trophyDropChance = TrophyConfig.COMMON_CONFIG.dropChanceOverride.get() >= 0.0D ? TrophyConfig.COMMON_CONFIG.dropChanceOverride.get() : trophy.dropChance();
				double chance = ((event.getLootingLevel() + (TROPHY_RANDOM.nextDouble() / 4)) * trophyDropChance) - TROPHY_RANDOM.nextDouble();
				if (chance > 0.0D) {
					event.getDrops().add(new ItemEntity(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), TrophyItem.loadEntityToTrophy(trophy.type(), this.fetchVariantIfAny(event.getEntity(), trophy), false)));
				}
			}
		}
	}

	private int fetchVariantIfAny(LivingEntity entity, Trophy trophy) {
		if (!trophy.getVariants(entity.level().registryAccess()).isEmpty()) {
			CompoundTag tag = new CompoundTag();
			entity.addAdditionalSaveData(tag);
			for (int i = 0; i < trophy.getVariants(entity.level().registryAccess()).size(); i++) {
				CompoundTag variantKeys = trophy.getVariants(entity.level().registryAccess()).get(i);
				for (String s : variantKeys.getAllKeys()) {
					if (entity instanceof VillagerDataHolder villager) {
						if (ForgeRegistries.VILLAGER_PROFESSIONS.getKey(villager.getVillagerData().getProfession()).toString().equals(variantKeys.getString(s))) {
							return i;
						}
					} else {
						Tag tagVer = tag.get(s);
						Tag variantVer = variantKeys.get(s);
						if (variantVer instanceof NumericTag num) {
							//most values save as bytes in the json, but sometimes they also be things like shorts.
							//we'll compare both numbers to long as they should always match this way.
							//comparing to int is gonna cause issues for floats and doubles
							if (tagVer instanceof NumericTag number && number.getAsLong() == num.getAsLong()) {
								return i;
							}
						} else if (Objects.equals(tagVer, variantVer)) {
							return i;
						}
					}
				}
			}
		}
		return 0;
	}
}
