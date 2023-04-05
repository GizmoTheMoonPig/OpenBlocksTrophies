package com.gizmo.trophies.data;

import com.gizmo.trophies.OpenBlocksTrophies;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = OpenBlocksTrophies.MODID)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		event.getGenerator().addProvider(event.includeServer(), new LootModifierGenerator(event.getGenerator()));
		event.getGenerator().addProvider(event.includeServer(), new TrophyGenerator(event.getGenerator()));
	}
}
