package com.gizmo.trophies.datagen;

import com.gizmo.trophies.OpenBlocksTrophies;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = OpenBlocksTrophies.MODID)
public class DataGenerator {

	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		event.getGenerator().addProvider(true, new ItemModelGenerator(event.getGenerator().getPackOutput()));
		event.getGenerator().addProvider(true, new LangGenerator(event.getGenerator().getPackOutput()));
		event.getGenerator().addProvider(true, new LootModifierGenerator(event.getGenerator().getPackOutput(), event.getLookupProvider()));
		event.getGenerator().addProvider(true, new TrophyGenerator(event.getGenerator().getPackOutput(), event.getLookupProvider()));
		event.getGenerator().addProvider(true, new TrophyAdvancementProvider(event.getGenerator().getPackOutput(), event.getLookupProvider()));
	}
}
