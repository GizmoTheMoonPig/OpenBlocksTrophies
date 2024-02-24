package com.gizmo.trophies.client;

import com.gizmo.trophies.item.TrophyItem;
import com.gizmo.trophies.misc.TrophyRegistries;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.CreativeModeTabSearchRegistry;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CreativeModeVariantToggle {

	private static CreativeModeTab lastTab = CreativeModeTabs.getDefaultTab();
	private static String lastSearchQuery = "";
	public static VariantToggleButton showVariants;
	private static int guiCenterX = 0;
	private static int guiCenterY = 0;

	public static void setupButton() {
		NeoForge.EVENT_BUS.addListener(CreativeModeVariantToggle::addVariantButton);
		NeoForge.EVENT_BUS.addListener(CreativeModeVariantToggle::setupVariantButton);
	}

	private static void addVariantButton(ScreenEvent.Init.Post event) {
		if (event.getScreen() instanceof CreativeModeInventoryScreen creativeScreen) {
			guiCenterX = creativeScreen.getGuiLeft();
			guiCenterY = creativeScreen.getGuiTop();

			event.addListener(showVariants = new VariantToggleButton(guiCenterX + 174, guiCenterY + 3, Component.literal("Show variants"), false, button -> {
				Screen screen = Minecraft.getInstance().screen;
				if (screen instanceof CreativeModeInventoryScreen creative) {
					CreativeModeVariantToggle.updateItems(creative);
				}
			}));

			onSwitchCreativeTab(CreativeModeInventoryScreen.selectedTab, creativeScreen);
		}
	}

	private static void setupVariantButton(ScreenEvent.Render.Post event) {
		if (event.getScreen() instanceof CreativeModeInventoryScreen creativeScreen) {
			guiCenterX = creativeScreen.getGuiLeft();
			guiCenterY = creativeScreen.getGuiTop();

			CreativeModeTab tab = CreativeModeInventoryScreen.selectedTab;
			if (lastTab != tab) {
				onSwitchCreativeTab(tab, creativeScreen);
				lastTab = tab;
			}

			if (tab == TrophyRegistries.TROPHY_TAB.get() && !creativeScreen.searchBox.getValue().equals(lastSearchQuery)) {
				tab.buildContents(buildParams());
				lastSearchQuery = creativeScreen.searchBox.getValue();
			}
		}
	}

	private static void onSwitchCreativeTab(CreativeModeTab tab, CreativeModeInventoryScreen screen) {
		if (tab == TrophyRegistries.TROPHY_TAB.get()) {
			showVariants.visible = true;
			updateItems(screen);
		} else {
			showVariants.visible = false;
		}
	}

	private static void updateItems(CreativeModeInventoryScreen screen) {
		CreativeModeInventoryScreen.selectedTab.buildContents(buildParams());
		CreativeModeInventoryScreen.ItemPickerMenu menu = screen.getMenu();
		menu.items.clear();
		menu.items.addAll(getTrophyList(Minecraft.getInstance().level.registryAccess(), CreativeModeTabSearchRegistry.getNameSearchKey(CreativeModeInventoryScreen.selectedTab), screen.searchBox.getValue()));
		menu.scrollTo(0);
		screen.scrollOffs = 0.0F;
	}

	private static List<ItemStack> getTrophyList(RegistryAccess access, @Nullable SearchRegistry.Key<ItemStack> searchTree, String queriedSearch) {
		List<ItemStack> trophies = new ArrayList<>();
		if (!Trophy.getTrophies().isEmpty()) {
			Map<ResourceLocation, Trophy> sortedTrophies = new TreeMap<>(Comparator.naturalOrder());
			sortedTrophies.putAll(Trophy.getTrophies());
			for (Map.Entry<ResourceLocation, Trophy> trophyEntry : sortedTrophies.entrySet()) {
				if (!trophyEntry.getValue().getVariants(access).isEmpty() && showVariants.isSelected()) {
					for (int i = 0; i < trophyEntry.getValue().getVariants(access).size(); i++) {
						trophies.add(TrophyItem.loadEntityToTrophy(trophyEntry.getValue().type(), i, false));
					}
				} else {
					trophies.add(TrophyItem.loadEntityToTrophy(trophyEntry.getValue().type(), 0, false));
				}
			}
		}

		return searchTree != null && !queriedSearch.isEmpty() ? Minecraft.getInstance().getSearchTree(searchTree).search(queriedSearch) : trophies;
	}

	private static CreativeModeTab.ItemDisplayParameters buildParams() {
		FeatureFlagSet features = Optional.of(Minecraft.getInstance())
				.map(m -> m.player)
				.map(p -> p.connection)
				.map(ClientPacketListener::enabledFeatures)
				.orElse(FeatureFlagSet.of());
		return new CreativeModeTab.ItemDisplayParameters(features, Minecraft.getInstance().options.operatorItemsTab().get(), Minecraft.getInstance().level.registryAccess());
	}
}
