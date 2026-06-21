package com.gizmo.trophies.init;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.item.DisplayTrophyItem;
import com.gizmo.trophies.item.TrophyItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class TrophyItems {

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(OpenBlocksTrophies.MODID);

	public static final DeferredItem<Item> TROPHY = ITEMS.register("trophy", () -> new TrophyItem(TrophyBlocks.TROPHY.get(), new Item.Properties().stacksTo(1).useBlockDescriptionPrefix().fireResistant().setId(ResourceKey.create(Registries.ITEM, OpenBlocksTrophies.prefix("trophy")))));
	public static final DeferredItem<Item> DISPLAY_TROPHY = ITEMS.register("display_trophy", () -> new DisplayTrophyItem(TrophyBlocks.DISPLAY_TROPHY.get(), new Item.Properties().stacksTo(1).useBlockDescriptionPrefix().fireResistant().setId(ResourceKey.create(Registries.ITEM, OpenBlocksTrophies.prefix("display_trophy")))));

}
