package com.gizmo.trophies;

import com.gizmo.trophies.behavior.CustomBehaviorType;
import com.gizmo.trophies.behavior.CustomTrophyBehaviors;
import com.gizmo.trophies.client.ClientEvents;
import com.gizmo.trophies.client.CreativeModeVariantToggle;
import com.gizmo.trophies.config.ConfigSetup;
import com.gizmo.trophies.config.TrophyConfig;
import com.gizmo.trophies.data.LangGenerator;
import com.gizmo.trophies.data.LootModifierGenerator;
import com.gizmo.trophies.data.TrophyAdvancementProvider;
import com.gizmo.trophies.data.TrophyGenerator;
import com.gizmo.trophies.misc.*;
import com.gizmo.trophies.network.SyncCommonConfigPacket;
import com.gizmo.trophies.network.SyncTrophyConfigsPacket;
import com.gizmo.trophies.command.TrophiesCommands;
import com.gizmo.trophies.trophy.Trophy;
import com.gizmo.trophies.trophy.TrophyReloadListener;
import com.google.common.reflect.Reflection;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Locale;

@Mod(OpenBlocksTrophies.MODID)
public class OpenBlocksTrophies {
	public static final String MODID = "obtrophies";

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static final List<EntityType<?>> UNUSED_TYPES = List.of(EntityType.GIANT, EntityType.ILLUSIONER, EntityType.ZOMBIE_HORSE);

	public static final ResourceKey<Registry<CustomBehaviorType>> CUSTOM_BEHAVIORS_KEY = ResourceKey.createRegistryKey(prefix("custom_behavior"));
	public static final Registry<CustomBehaviorType> CUSTOM_BEHAVIORS = new RegistryBuilder<>(CUSTOM_BEHAVIORS_KEY).sync(true).create();

	public OpenBlocksTrophies(IEventBus bus, Dist dist) {
		Reflection.initialize(ConfigSetup.class);
		ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> ConfigurationScreen::new);
		if (dist.isClient()) {
			ClientEvents.init(bus);
			CreativeModeVariantToggle.setupButton();
		}

		bus.addListener(this::gatherData);

		bus.addListener(NewRegistryEvent.class, event -> event.register(CUSTOM_BEHAVIORS));
		bus.addListener(this::registerPacket);

		bus.addListener(ConfigSetup::loadConfigs);
		bus.addListener(ConfigSetup::reloadConfigs);
		NeoForge.EVENT_BUS.addListener(ConfigSetup::syncConfigOnLogin);

		NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, event -> TrophiesCommands.register(event.getDispatcher(), event.getBuildContext()));
		NeoForge.EVENT_BUS.addListener(AddServerReloadListenersEvent.class, event -> event.addListener(prefix("trophies"), new TrophyReloadListener()));
		NeoForge.EVENT_BUS.addListener(TrophyEvents::maybeDropTrophy);
		NeoForge.EVENT_BUS.addListener(TrophyEvents::syncTrophiesToClient);
		NeoForge.EVENT_BUS.addListener(TrophyEvents::grantAdvancementBasedTrophies);

		TrophyRegistries.BLOCKS.register(bus);
		TrophyRegistries.BLOCK_ENTITIES.register(bus);
		TrophyRegistries.COMPONENTS.register(bus);
		TrophyRegistries.COMPONENT_PREDICATES.register(bus);
		TrophyRegistries.ITEMS.register(bus);
		TrophyRegistries.LOOT_MODIFIERS.register(bus);
		TrophyRegistries.SOUNDS.register(bus);
		TrophyRegistries.TABS.register(bus);

		CustomTrophyBehaviors.CUSTOM_BEHAVIORS.register(bus);
	}

	public void gatherData(GatherDataEvent.Client event) {
		event.getGenerator().addProvider(true, new LangGenerator(event.getGenerator().getPackOutput()));
		event.getGenerator().addProvider(true, new LootModifierGenerator(event.getGenerator().getPackOutput(), event.getLookupProvider()));
		event.getGenerator().addProvider(true, new TrophyGenerator(event.getGenerator().getPackOutput()));
		event.getGenerator().addProvider(true, new TrophyAdvancementProvider(event.getGenerator().getPackOutput(), event.getLookupProvider()));
	}

	public void registerPacket(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar(MODID).versioned("1.0.1");
		registrar.playToClient(SyncCommonConfigPacket.TYPE, SyncCommonConfigPacket.STREAM_CODEC, SyncCommonConfigPacket::handle);
		registrar.playToClient(SyncTrophyConfigsPacket.TYPE, SyncTrophyConfigsPacket.STREAM_CODEC, SyncTrophyConfigsPacket::handle);
	}

	public static double getTrophyDropChance(Trophy trophy) {
		return TrophyConfig.dropChanceOverride >= 0.0D ? TrophyConfig.dropChanceOverride : trophy.dropChance();
	}

	public static ResourceLocation prefix(String name) {
		return ResourceLocation.fromNamespaceAndPath(MODID, name.toLowerCase(Locale.ROOT));
	}
}
