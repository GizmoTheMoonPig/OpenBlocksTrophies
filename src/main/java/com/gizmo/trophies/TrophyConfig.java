package com.gizmo.trophies;

import net.minecraftforge.common.ForgeConfigSpec;

public class TrophyConfig {

	public static CommonConfig COMMON_CONFIG;

	public static class CommonConfig {

		public ForgeConfigSpec.BooleanValue fakePlayersDropTrophies;
		public ForgeConfigSpec.BooleanValue anySourceDropsTrophies;


		public CommonConfig(ForgeConfigSpec.Builder builder) {
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
		}
	}
}
