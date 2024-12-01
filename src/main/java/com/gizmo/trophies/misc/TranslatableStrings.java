package com.gizmo.trophies.misc;

public final class TranslatableStrings {
	public static final String TROPHY_TAB = "itemGroup.obtrophies";

	public static final String TROPHY_COUNT = "command.obtrophies.count";
	public static final String EMPTY_TROPHY_LIST = "command.obtrophies.empty_list";
	public static final String MOD_NOT_LOADED = "command.obtrophies.mod_not_loaded";
	public static final String PLACED_TROPHIES = "command.obtrophies.place";
	public static final String TROPHY_STUB_MADE = "command.obtrophies.trophy_made";
	public static final String TROPHY_STUBS_MADE = "command.obtrophies.trophies_made";

	public static final String TROPHY_WITH_ENTITY = TrophyRegistries.TROPHY.get().getDescriptionId() + ".entity";
	public static final String DISPLAY_TROPHY = TrophyRegistries.DISPLAY_TROPHY.get().getDescriptionId() + ".display";
	public static final String FROM_MOD_ID = TrophyRegistries.TROPHY_ITEM.get().getDescriptionId() + ".modid";
	public static final String VARIANT_FORMATTER = TrophyRegistries.TROPHY_ITEM.get().getDescriptionId() + ".variant";

	public static final String TROPHY_CATEGORY = "gui.obtrophies.trophy_category";
	public static final String TROPHY_DROP_CHANCE = "gui.obtrophies.drop_chance";
	public static final String TROPHY_FAKE_PLAYER = "gui.obtrophies.fake_player_drops";
	public static final String TROPHY_PLAYER = "gui.obtrophies.player_drops";

}
