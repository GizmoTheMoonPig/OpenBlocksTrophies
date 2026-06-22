package com.gizmo.trophies.client;

import com.gizmo.trophies.init.TrophyRegistries;
import com.gizmo.trophies.item.TrophyHelper;
import com.gizmo.trophies.trophy.Trophy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.CreativeModeTabSearchRegistry;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class CreativeModeVariantToggle {

	private static CreativeModeTab lastTab = CreativeModeTabs.getDefaultTab();
	private static String lastSearchQuery = "";
	@Nullable
	public static VariantToggleButton showVariants;
	private static int guiCenterX = 0;
	private static int guiCenterY = 0;

	public static void setupButton() {
		NeoForge.EVENT_BUS.addListener(CreativeModeVariantToggle::addVariantButton);
		NeoForge.EVENT_BUS.addListener(CreativeModeVariantToggle::setupVariantButton);
	}

	private static void addVariantButton(ScreenEvent.Init.Post event) {
		if (event.getScreen() instanceof CreativeModeInventoryScreen creativeScreen) {
			guiCenterX = creativeScreen.getLeftPos();
			guiCenterY = creativeScreen.getTopPos();

			event.addListener(showVariants = new VariantToggleButton(guiCenterX + 174, guiCenterY + 3, Component.literal("Show variants"), false, button -> {
				Screen screen = Minecraft.getInstance().gui.screen();
				if (screen instanceof CreativeModeInventoryScreen creative) {
					CreativeModeVariantToggle.updateItems(creative);
				}
			}));

			onSwitchCreativeTab(CreativeModeInventoryScreen.selectedTab, creativeScreen);
		}
	}

	private static void setupVariantButton(ScreenEvent.Render.Post event) {
		if (event.getScreen() instanceof CreativeModeInventoryScreen creativeScreen) {
			guiCenterX = creativeScreen.getLeftPos();
			guiCenterY = creativeScreen.getTopPos();

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
		if (showVariants != null) {
			if (tab == TrophyRegistries.TROPHY_TAB.get()) {
				showVariants.visible = true;
				updateItems(screen);
			} else {
				showVariants.visible = false;
			}
		}
	}

	private static void updateItems(CreativeModeInventoryScreen screen) {
		var params = buildParams();
		CreativeModeInventoryScreen.selectedTab.buildContents(params);
		CreativeModeInventoryScreen.ItemPickerMenu menu = screen.getMenu();
		menu.items.clear();
		menu.items.addAll(getTrophyList(Minecraft.getInstance().level.registryAccess(), params.enabledFeatures(), CreativeModeTabSearchRegistry.getNameSearchKey(CreativeModeInventoryScreen.selectedTab), screen.searchBox.getValue()));
		menu.scrollTo(0);
		screen.scrollOffs = 0.0F;
	}

	private static List<ItemStack> getTrophyList(RegistryAccess access, FeatureFlagSet set, SessionSearchTrees.@Nullable Key searchTree, String queriedSearch) {
		List<ItemStack> trophies = new ArrayList<>();
		if (!Trophy.getTrophies().isEmpty()) {
			Map<Identifier, Trophy> sortedTrophies = new TreeMap<>(Comparator.naturalOrder());
			sortedTrophies.putAll(Trophy.getTrophies());
			for (Map.Entry<Identifier, Trophy> trophyEntry : sortedTrophies.entrySet()) {
				if (trophyEntry.getValue().type().isEnabled(set)) {
					if (!trophyEntry.getValue().getVariants(access).isEmpty() && showVariants.isSelected()) {
						trophyEntry.getValue().getVariants(access).forEach(tag -> trophies.add(TrophyHelper.loadVariantToTrophy(trophyEntry.getValue().type(), tag).create()));
					} else {
						trophies.add(TrophyHelper.loadEntityToTrophy(trophyEntry.getValue().type()).create());
					}
				}
			}
		}

		return searchTree != null && !queriedSearch.isEmpty() ? Minecraft.getInstance().getConnection().searchTrees().creativeNameSearch(searchTree).search(queriedSearch) : trophies;
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
