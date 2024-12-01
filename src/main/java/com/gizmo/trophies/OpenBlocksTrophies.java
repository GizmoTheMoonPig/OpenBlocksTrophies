package com.gizmo.trophies;

import com.gizmo.trophies.behavior.CustomBehaviorType;
import com.gizmo.trophies.behavior.CustomTrophyBehaviors;
import com.gizmo.trophies.client.ClientEvents;
import com.gizmo.trophies.client.CreativeModeVariantToggle;
import com.gizmo.trophies.config.ConfigSetup;
import com.gizmo.trophies.config.TrophyConfig;
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
import net.minecraft.DetectedVersion;
import net.minecraft.core.Registry;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@Mod(OpenBlocksTrophies.MODID)
public class OpenBlocksTrophies {
	public static final String MODID = "obtrophies";

	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static final ResourceKey<Registry<CustomBehaviorType>> CUSTOM_BEHAVIORS_KEY = ResourceKey.createRegistryKey(new ResourceLocation(MODID, "custom_behavior"));
	public static final Registry<CustomBehaviorType> CUSTOM_BEHAVIORS = new RegistryBuilder<>(CUSTOM_BEHAVIORS_KEY).sync(true).create();

	public OpenBlocksTrophies(IEventBus bus, Dist dist) {
		Reflection.initialize(ConfigSetup.class);
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
		NeoForge.EVENT_BUS.addListener(AddReloadListenerEvent.class, event -> event.addListener(new TrophyReloadListener()));
		NeoForge.EVENT_BUS.addListener(TrophyEvents::maybeDropTrophy);
		NeoForge.EVENT_BUS.addListener(TrophyEvents::syncTrophiesToClient);
		NeoForge.EVENT_BUS.addListener(TrophyEvents::grantAdvancementBasedTrophies);

		TrophyRegistries.BLOCKS.register(bus);
		TrophyRegistries.BLOCK_ENTITIES.register(bus);
		TrophyRegistries.ITEMS.register(bus);
		TrophyRegistries.LOOT_MODIFIERS.register(bus);
		TrophyRegistries.SOUNDS.register(bus);
		TrophyRegistries.TABS.register(bus);

		CustomTrophyBehaviors.CUSTOM_BEHAVIORS.register(bus);
	}

	public void gatherData(GatherDataEvent event) {
		event.getGenerator().addProvider(event.includeServer(), new LootModifierGenerator(event.getGenerator().getPackOutput()));
		event.getGenerator().addProvider(event.includeServer(), new TrophyGenerator(event.getGenerator().getPackOutput()));
		event.getGenerator().addProvider(event.includeServer(), new TrophyAdvancementProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), event.getExistingFileHelper()));
		event.getGenerator().addProvider(true, new PackMetadataGenerator(event.getGenerator().getPackOutput()).add(PackMetadataSection.TYPE, new PackMetadataSection(
				Component.literal("Trophy Resources"),
				DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA),
				Optional.of(new InclusiveRange<>(0, Integer.MAX_VALUE)))));
	}

	public void registerPacket(RegisterPayloadHandlerEvent event) {
		IPayloadRegistrar registrar = event.registrar(MODID).versioned("1.0.1");
		registrar.play(SyncCommonConfigPacket.ID, SyncCommonConfigPacket::new, payload -> payload.client(SyncCommonConfigPacket::handle));
		registrar.play(SyncTrophyConfigsPacket.ID, SyncTrophyConfigsPacket::new, payload -> payload.client(SyncTrophyConfigsPacket::handle));
	}

	public static double getTrophyDropChance(Trophy trophy) {
		return TrophyConfig.dropChanceOverride >= 0.0D ? TrophyConfig.dropChanceOverride : trophy.dropChance();
	}
}
