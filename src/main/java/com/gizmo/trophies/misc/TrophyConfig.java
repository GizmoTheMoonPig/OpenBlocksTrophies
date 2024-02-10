package com.gizmo.trophies.misc;

import net.neoforged.neoforge.common.ModConfigSpec;

public class TrophyConfig {

	public static ClientConfig CLIENT_CONFIG;
	public static CommonConfig COMMON_CONFIG;

	public static class ClientConfig {
		public final ModConfigSpec.BooleanValue playersRenderNames;
		public final ModConfigSpec.BooleanValue renderNameColorsAndIcons;

		public ClientConfig(ModConfigSpec.Builder builder) {
			this.playersRenderNames = builder.
					translation("obtrophies.config.players_render_names").
					comment("If true, player trophies will render their names over their head similar to how players do.").
					define("player_trophies_render_names", true);

			this.renderNameColorsAndIcons = builder.
					translation("obtrophies.config.render_name_gizmos").
					comment("""
							If true, some player trophies will render with special icons or name colors.
							If you find this to be too distracting for some reason, you can turn this off to keep the names plainly formatted.""").
					define("render_player_trophy_name_decorators", true);
		}
	}

	public static class CommonConfig {

		public final ModConfigSpec.BooleanValue fakePlayersDropTrophies;
		public final ModConfigSpec.BooleanValue anySourceDropsTrophies;
		public final ModConfigSpec.DoubleValue dropChanceOverride;
		public final ModConfigSpec.DoubleValue playerChargedCreeperDropChance;

		public CommonConfig(ModConfigSpec.Builder builder) {
			this.fakePlayersDropTrophies = builder.
					translation("obtrophies.config.fake_player_drops").
					comment("""
							If true, fake players will allow mobs to drop trophies.
							This can allow mob grinders from other mobs to drop trophies, such as the mob masher from MobGrindingUtils.""").
					define("fake_player_drops", false);

			this.anySourceDropsTrophies = builder.
					translation("obtrophies.config.any_kill_drops").
					comment("""
							If true, allows trophies to drop whenever a mob dies. This can be to fall damage, another mob, etc.
							Basically, a kill doesnt have to count as a player kill for a trophy to drop.""").
					define("any_kill_drops", false);

			this.dropChanceOverride = builder.
					translation("obtrophies.config.drop_chance").
					comment("""
							The chance a trophy will drop from its respective mob.
							All trophy drop chances are defined in their trophy json, but if you want to override that chance without going through and changing every json this is for you.
							This value works as a percentage (number * 100), so 0.2 would be a 20% chance for example.
							Set this value to any negative number to disable the override.""").
					defineInRange("trophy_drop_override", -1.0D, -1.0D, 1.0D);

			this.playerChargedCreeperDropChance = builder.
					translation("obtrophies.config.player_creeper_chance").
					comment("""
							The chance a player will drop a trophy when killed by a charged creeper.
							This config mostly exists for singleplayer worlds where getting a player kill on a player is rather difficult.
							This value works as a percentage (number * 100), so 0.2 would be a 20% chance for example.
							Set this value to 0.0 to disable drops from charged creeper kills.""").
					defineInRange("charged_creeper_player_trophy_chance", 0.2D, 0.0D, 1.0D);
		}
	}
}
