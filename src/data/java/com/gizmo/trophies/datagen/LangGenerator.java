package com.gizmo.trophies.datagen;

import com.gizmo.trophies.OpenBlocksTrophies;
import com.gizmo.trophies.config.ConfigComments;
import com.gizmo.trophies.init.TrophyBlocks;
import com.gizmo.trophies.misc.TranslatableStrings;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class LangGenerator extends LanguageProvider {
	public LangGenerator(PackOutput output) {
		super(output, OpenBlocksTrophies.MODID, "en_us");
	}

	@Override
	protected void addTranslations() {
		this.addAdvancement("root", "OpenBlocks Trophies", "Challenges revolving around trophy collection");
		this.addAdvancement("one_trophy", "What a Find!", "Collect a trophy dropped by a mob. How lucky!");
		this.addAdvancement("boss_trophy", "The True Reward", "Collect a trophy dropped by a boss or miniboss");
		this.addAdvancement("rarest_trophy", "Exotic Artifact Hunter", "Collect the rarest vanilla trophy: the blue Axolotl trophy");
		this.addAdvancement("all_horse_trophies", "Horsin' Around", "Collect every single horse variant as a trophy");
		this.addAdvancement("all_fish_trophies", "Fish Slapped", "Collect all 22 common fish trophies");
		this.addAdvancement("all_vanilla", "Dedicated Collector", "Collect a trophy from every single vanilla mob!");

		this.add("obtrophies.configuration.title", "OpenBlocks Trophies Config");
		this.add("obtrophies.configuration.section.obtrophies.client.toml", "Client Settings");
		this.add("obtrophies.configuration.section.obtrophies.client.toml.title", "Client Settings");
		this.add("obtrophies.configuration.section.obtrophies.common.toml", "Common Settings");
		this.add("obtrophies.configuration.section.obtrophies.common.toml.title", "Common Settings");
		this.configEntry("render_names", "Render Player Names", ConfigComments.RENDER_NAMES);
		this.configEntry("render_name_decorators", "Render Player Name Deco", ConfigComments.RENDER_NAME_DECO);
		this.configEntry("drop_chance", "Global Trophy Drop Chance", ConfigComments.DROP_OVERRIDE);
		this.configEntry("right_click_override", "Global Right Click Override", ConfigComments.RIGHT_CLICK_OVERRIDE);
		this.configEntry("player_creeper_chance", "Player Trophy Drop Chance", ConfigComments.CHARGED_CREEPER);
		this.configEntry("trophy_source_drop", "Trophy Drop Source", ConfigComments.DROP_SOURCE);

		this.add("config.obtrophies.source_drop.all", "Any Kill");
		this.add("config.obtrophies.source_drop.fake_player", "(Fake) Player Kill");
		this.add("config.obtrophies.source_drop.player", "Player Kill Only");

		this.add(TrophyBlocks.TROPHY.get(), "Trophy");
		this.add(TranslatableStrings.TROPHY_WITH_ENTITY, "%s Trophy");
		this.add(TrophyBlocks.DISPLAY_TROPHY.get(), "Display Trophy");
		this.add(TranslatableStrings.DISPLAY_TROPHY, "%s Trophy");
		this.add(TranslatableStrings.TROPHY_COUNT, "Trophy Count: %s");
		this.add(TranslatableStrings.EMPTY_TROPHY_LIST, "Trophy list is not populated!");
		this.add(TranslatableStrings.MOD_NOT_LOADED, "Mod %s does not exist!");
		this.add(TranslatableStrings.PLACED_TROPHIES, "Placed %s Trophies");
		this.add(TranslatableStrings.TROPHY_STUB_MADE, "Trophy stub made for entity %s");
		this.add(TranslatableStrings.TROPHY_STUBS_MADE, "Sucessfully made %s trophy configs!");
		this.add(TranslatableStrings.FROM_MOD_ID, "From: %s");
		this.add(TranslatableStrings.VARIANT_FORMATTER, "\"%s\": %s");
		this.add(TranslatableStrings.INVALID_DATA, "This trophy is missing its info component");

		this.add(TranslatableStrings.TROPHY_TAB, "Trophies");
		this.add(TranslatableStrings.TROPHY_DROP_CHANCE, "Chance: %s%%");
		this.add(TranslatableStrings.TROPHY_FAKE_PLAYER, "(fake players count)");
		this.add(TranslatableStrings.TROPHY_PLAYER, "Drops on player kill only");
		this.add(TranslatableStrings.TROPHY_CATEGORY, "Trophy Drops");
		//sadge
		this.add("emi.category.obtrophies.trophy", "Trophy Drops");
	}

	public void addAdvancement(String key, String title, String desc) {
		this.add("advancement.obtrophies." + key + ".title", title);
		this.add("advancement.obtrophies." + key + ".desc", desc);
	}

	public void configEntry(String key, String name, String description) {
		this.add("config.obtrophies." + key, name);
		this.add("config.obtrophies." + key + ".tooltip", description);
	}
}
