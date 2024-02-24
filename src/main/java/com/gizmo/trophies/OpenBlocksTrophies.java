package com.gizmo.trophies;

import com.gizmo.trophies.behavior.CustomBehaviorType;
import com.gizmo.trophies.behavior.CustomTrophyBehaviors;
import com.gizmo.trophies.client.ClientEvents;
import com.gizmo.trophies.data.LootModifierGenerator;
import com.gizmo.trophies.data.TrophyAdvancementProvider;
import com.gizmo.trophies.data.TrophyGenerator;
import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.misc.SyncTrophyConfigsPacket;
import com.gizmo.trophies.misc.TrophiesCommands;
import com.gizmo.trophies.misc.TrophyConfig;
import com.gizmo.trophies.misc.TrophyRegistries;
import com.gizmo.trophies.trophy.Trophy;
import com.gizmo.trophies.trophy.TrophyReloadListener;
import net.minecraft.DetectedVersion;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Optional;

@Mod(OpenBlocksTrophies.MODID)
public class OpenBlocksTrophies {
	public static final String MODID = "obtrophies";

	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static final RandomSource TROPHY_RANDOM = RandomSource.create();

	public static final ResourceKey<Registry<CustomBehaviorType>> CUSTOM_BEHAVIORS_KEY = ResourceKey.createRegistryKey(OpenBlocksTrophies.location("custom_behavior"));
	public static final Registry<CustomBehaviorType> CUSTOM_BEHAVIORS = new RegistryBuilder<>(CUSTOM_BEHAVIORS_KEY).sync(true).create();

	public OpenBlocksTrophies(IEventBus bus, Dist dist) {
		{
			final Pair<TrophyConfig.CommonConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(TrophyConfig.CommonConfig::new);
			ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, specPair.getRight());
			TrophyConfig.COMMON_CONFIG = specPair.getLeft();
		}
		{
			final Pair<TrophyConfig.ClientConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(TrophyConfig.ClientConfig::new);
			ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, specPair.getRight());
			TrophyConfig.CLIENT_CONFIG = specPair.getLeft();
		}

		if (dist.isClient()) {
			ClientEvents.init(bus);
		}

		bus.addListener(this::gatherData);

		bus.addListener(NewRegistryEvent.class, event -> event.register(CUSTOM_BEHAVIORS));
		bus.addListener(this::registerPacket);

		NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, event -> TrophiesCommands.register(event.getDispatcher()));
		NeoForge.EVENT_BUS.addListener(AddReloadListenerEvent.class, event -> event.addListener(new TrophyReloadListener()));
		NeoForge.EVENT_BUS.addListener(this::maybeDropTrophy);
		NeoForge.EVENT_BUS.addListener(this::syncTrophiesToClient);
		NeoForge.EVENT_BUS.addListener(this::grantBeeQueenViaDesireAdvancement);

		TrophyRegistries.BLOCKS.register(bus);
		TrophyRegistries.BLOCK_ENTITIES.register(bus);
		TrophyRegistries.ITEMS.register(bus);
		TrophyRegistries.LOOT_MODIFIERS.register(bus);
		TrophyRegistries.SOUNDS.register(bus);
		TrophyRegistries.TABS.register(bus);

		CustomTrophyBehaviors.CUSTOM_BEHAVIORS.register(bus);
	}

	public static ResourceLocation location(String path) {
		return new ResourceLocation(MODID, path);
	}

	public void gatherData(GatherDataEvent event) {
		event.getGenerator().addProvider(event.includeServer(), new LootModifierGenerator(event.getGenerator().getPackOutput()));
		event.getGenerator().addProvider(event.includeServer(), new TrophyGenerator(event.getGenerator().getPackOutput()));
		event.getGenerator().addProvider(event.includeServer(), new TrophyAdvancementProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
		event.getGenerator().addProvider(true, new PackMetadataGenerator(event.getGenerator().getPackOutput()).add(PackMetadataSection.TYPE, new PackMetadataSection(
				Component.literal("Trophy Resources"),
				DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES),
				Optional.of(new InclusiveRange<>(0, Integer.MAX_VALUE)))));
	}

	public void registerPacket(RegisterPayloadHandlerEvent event) {
		IPayloadRegistrar registrar = event.registrar(MODID).versioned("1.0.0").optional();
		registrar.play(SyncTrophyConfigsPacket.ID, SyncTrophyConfigsPacket::new, payload -> payload.client(SyncTrophyConfigsPacket::handle));

	}

	public void syncTrophiesToClient(OnDatapackSyncEvent event) {
		if (event.getPlayer() != null) {
			PacketDistributor.PLAYER.with(event.getPlayer()).send(new SyncTrophyConfigsPacket(Trophy.getTrophies()));
			OpenBlocksTrophies.LOGGER.debug("Sent {} trophy configs to {} from server.", Trophy.getTrophies().size(), event.getPlayer().getDisplayName().getString());
		} else {
			event.getPlayerList().getPlayers().forEach(player -> {
				PacketDistributor.PLAYER.with(player).send(new SyncTrophyConfigsPacket(Trophy.getTrophies()));
				OpenBlocksTrophies.LOGGER.debug("Sent {} trophy configs to {} from server.", Trophy.getTrophies().size(), player.getDisplayName().getString());
			});
		}
	}

	public void grantBeeQueenViaDesireAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
		if (ModList.get().isLoaded("the_bumblezone")) {
			if (event.getAdvancement().id().equals(new ResourceLocation("the_bumblezone", "the_bumblezone/the_queens_desire/journeys_end"))) {
				ItemStack trophy = TrophyItem.loadEntityToTrophy(Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation("the_bumblezone", "bee_queen"))), 0, false);
				if (event.getEntity().addItem(trophy)) {
					event.getEntity().drop(trophy, false);
				}
			}
			if (event.getAdvancement().id().equals(new ResourceLocation("the_bumblezone", "the_bumblezone/beehemoth/queen_beehemoth"))) {
				ItemStack trophy = TrophyItem.loadEntityToTrophy(Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation("the_bumblezone", "beehemoth"))), 1, false);
				if (event.getEntity().addItem(trophy)) {
					event.getEntity().drop(trophy, false);
				}
			}
		}
	}

	public void maybeDropTrophy(LivingDropsEvent event) {
		//follow gamerules and mob drop requirements
		if (!event.getEntity().level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT) || !event.getEntity().shouldDropLoot())
			return;

		//players are a bit special.
		//charged creepers can make players drop trophies, and player trophies come loaded with the dead player's name
		if (event.getEntity() instanceof Player player) {
			double dropChance;
			if (event.getSource().getEntity() instanceof Creeper creeper && creeper.isPowered() && TrophyConfig.COMMON_CONFIG.playerChargedCreeperDropChance.get() > 0.0D) {
				dropChance = TrophyConfig.COMMON_CONFIG.playerChargedCreeperDropChance.get() - TROPHY_RANDOM.nextDouble();
			} else {
				//don't drop trophies if the config doesn't allow this source to
				if (!(event.getSource().getEntity() instanceof Player) && !TrophyConfig.COMMON_CONFIG.anySourceDropsTrophies.get())
					return;
				if (event.getSource().getEntity() instanceof FakePlayer && !TrophyConfig.COMMON_CONFIG.fakePlayersDropTrophies.get())
					return;
				Trophy trophy = Trophy.getTrophies().getOrDefault(ForgeRegistries.ENTITY_TYPES.getKey(EntityType.PLAYER), new Trophy.Builder(EntityType.PLAYER).build());
				dropChance = ((event.getLootingLevel() + (TROPHY_RANDOM.nextDouble() / 4)) * getTrophyDropChance(trophy)) - TROPHY_RANDOM.nextDouble();
			}
			if (dropChance > 0.0D) {
				ItemStack stack = TrophyItem.loadEntityToTrophy(EntityType.PLAYER, 0, false);
				stack.setHoverName(Component.literal(player.getDisplayName().getString()));
				event.getDrops().add(new ItemEntity(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), stack));
			}
		} else {
			//don't drop trophies if the config doesn't allow this source to
			if (!(event.getSource().getEntity() instanceof Player) && !TrophyConfig.COMMON_CONFIG.anySourceDropsTrophies.get())
				return;
			if (event.getSource().getEntity() instanceof FakePlayer && !TrophyConfig.COMMON_CONFIG.fakePlayersDropTrophies.get())
				return;

			if (Trophy.getTrophies().containsKey(BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType()))) {
				Trophy trophy = Trophy.getTrophies().get(BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType()));
				if (trophy != null) {
					double chance = ((event.getLootingLevel() + (TROPHY_RANDOM.nextDouble() / 4)) * getTrophyDropChance(trophy)) - TROPHY_RANDOM.nextDouble();
					if (chance > 0.0D) {
						event.getDrops().add(new ItemEntity(event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), TrophyItem.loadEntityToTrophy(trophy.type(), this.fetchVariantIfAny(event.getEntity(), trophy), false)));
					}
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
						if (BuiltInRegistries.VILLAGER_PROFESSION.getKey(villager.getVillagerData().getProfession()).toString().equals(variantKeys.getString(s))) {
							return i;
						}
					} else {
						Tag tagVer = tag.get(s);
						Tag variantVer = variantKeys.get(s);
						if (variantVer instanceof NumericTag num) {
							//most values save as bytes in the json, but sometimes they also be things like shorts.
							//we'll compare both numbers to long as they should always match this way.
							//comparing to int is going to cause issues for floats and doubles
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

	public static double getTrophyDropChance(Trophy trophy) {
		return TrophyConfig.COMMON_CONFIG.dropChanceOverride.get() >= 0.0D ? TrophyConfig.COMMON_CONFIG.dropChanceOverride.get() : trophy.dropChance();
	}
}
