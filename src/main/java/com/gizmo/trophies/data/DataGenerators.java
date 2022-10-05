package com.gizmo.trophies.data;

import com.gizmo.trophies.OpenBlocksTrophies;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = OpenBlocksTrophies.MODID)
public class DataGenerators {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		event.getGenerator().addProvider(new TrophyGenerator(event.getGenerator()));
	}
}
