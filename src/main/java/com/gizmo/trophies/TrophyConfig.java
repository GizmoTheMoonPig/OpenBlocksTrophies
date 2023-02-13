package com.gizmo.trophies;

import net.minecraftforge.common.ForgeConfigSpec;

public class TrophyConfig {

	public static CommonConfig COMMON_CONFIG;

	public static class CommonConfig {

		public final ForgeConfigSpec.BooleanValue fakePlayersDropTrophies;
		public final ForgeConfigSpec.BooleanValue anySourceDropsTrophies;
		public final ForgeConfigSpec.DoubleValue dropChanceOverride;


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

			this.dropChanceOverride = builder.
					translation("obtrophies.config.drop_chance").
					comment("""
							The chance a trophy will drop from its respective mob.
							All trophy drop chances are defined in their trophy json, but if you want to override that chance without going through and changing every json this is for you.
							Set this value to -1 to disable.""").
					defineInRange("trophy_drop_override", -1.0D, -1.0D, 1.0D);
		}
	}
}
