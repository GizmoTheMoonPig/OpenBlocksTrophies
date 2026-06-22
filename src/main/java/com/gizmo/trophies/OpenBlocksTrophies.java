package com.gizmo.trophies;

import com.gizmo.trophies.behavior.CustomBehaviorType;
import com.gizmo.trophies.client.ClientEvents;
import com.gizmo.trophies.client.CreativeModeVariantToggle;
import com.gizmo.trophies.command.TrophiesCommands;
import com.gizmo.trophies.config.ConfigSetup;
import com.gizmo.trophies.config.TrophyConfig;
import com.gizmo.trophies.criteria.TrophyCriteriaType;
import com.gizmo.trophies.event.CriteriaEvents;
import com.gizmo.trophies.event.TrophyEvents;
import com.gizmo.trophies.init.*;
import com.gizmo.trophies.network.SyncCommonConfigPacket;
import com.gizmo.trophies.network.SyncTrophyConfigsPacket;
import com.gizmo.trophies.trophy.Trophy;
import com.gizmo.trophies.trophy.listener.TrophyCriteriaReloadListener;
import com.gizmo.trophies.trophy.listener.TrophyReloadListener;
import com.google.common.reflect.Reflection;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
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

	public static final List<EntityType<?>> UNUSED_TYPES = List.of(EntityType.GIANT, EntityType.ILLUSIONER);

	public static final ResourceKey<Registry<CustomBehaviorType>> CUSTOM_BEHAVIORS_KEY = ResourceKey.createRegistryKey(prefix("custom_behavior"));
	public static final Registry<CustomBehaviorType> CUSTOM_BEHAVIORS = new RegistryBuilder<>(CUSTOM_BEHAVIORS_KEY).sync(true).create();

	public static final ResourceKey<Registry<TrophyCriteriaType>> TROPHY_CRITERIA_KEY = ResourceKey.createRegistryKey(prefix("trophy_criteria"));
	public static final Registry<TrophyCriteriaType> TROPHY_CRITERIA = new RegistryBuilder<>(TROPHY_CRITERIA_KEY).sync(true).create();

	public OpenBlocksTrophies(IEventBus bus, ModContainer container, Dist dist) {
		Reflection.initialize(ConfigSetup.class);
		container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
		if (dist.isClient()) {
			ClientEvents.init(bus);
			CreativeModeVariantToggle.setupButton();
		}

		bus.addListener(NewRegistryEvent.class, event -> {
			event.register(CUSTOM_BEHAVIORS);
			event.register(TROPHY_CRITERIA);
		});
		bus.addListener(this::registerPackets);

		bus.addListener(ConfigSetup::loadConfigs);
		bus.addListener(ConfigSetup::reloadConfigs);
		NeoForge.EVENT_BUS.addListener(ConfigSetup::syncConfigOnLogin);

		NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, event -> TrophiesCommands.register(event.getDispatcher(), event.getBuildContext()));
		NeoForge.EVENT_BUS.addListener(AddServerReloadListenersEvent.class, event -> {
			event.addListener(prefix("trophies"), new TrophyReloadListener(event.getServerResources().getRegistryLookup()));
			event.addListener(prefix("trophy_criteria"), new TrophyCriteriaReloadListener(event.getServerResources().getRegistryLookup()));
		});
		NeoForge.EVENT_BUS.addListener(TrophyEvents::maybeDropTrophy);
		NeoForge.EVENT_BUS.addListener(TrophyEvents::syncTrophiesToClient);
		NeoForge.EVENT_BUS.addListener(TrophyEvents::dontVisuallyShowSkinsWhileRenaming);

		NeoForge.EVENT_BUS.addListener(CriteriaEvents::grantAdvancementTrophies);
		NeoForge.EVENT_BUS.addListener(CriteriaEvents::grantInteractionTrophies);
		NeoForge.EVENT_BUS.addListener(CriteriaEvents::grantSpecialKillTrophies);

		TrophyBlocks.BLOCKS.register(bus);
		TrophyBlockEntities.BLOCK_ENTITIES.register(bus);
		TrophyComponents.COMPONENTS.register(bus);
		TrophyComponents.COMPONENT_PREDICATES.register(bus);
		TrophyItems.ITEMS.register(bus);
		TrophyRegistries.LOOT_MODIFIERS.register(bus);
		TrophyRegistries.SOUNDS.register(bus);
		TrophyRegistries.TABS.register(bus);

		TrophyBehaviors.CUSTOM_BEHAVIORS.register(bus);
		TrophyCriteria.TROPHY_CRITERIA.register(bus);
	}

	public void registerPackets(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar(MODID).versioned("1.0.1");
		registrar.playToClient(SyncCommonConfigPacket.TYPE, SyncCommonConfigPacket.STREAM_CODEC, SyncCommonConfigPacket::handle);
		registrar.playToClient(SyncTrophyConfigsPacket.TYPE, SyncTrophyConfigsPacket.STREAM_CODEC, SyncTrophyConfigsPacket::handle);
	}

	public static double getTrophyDropChance(Trophy trophy) {
		return TrophyConfig.dropChanceOverride >= 0.0D ? TrophyConfig.dropChanceOverride : trophy.dropChance();
	}

	public static Identifier prefix(String name) {
		return Identifier.fromNamespaceAndPath(MODID, name.toLowerCase(Locale.ROOT));
	}
}
