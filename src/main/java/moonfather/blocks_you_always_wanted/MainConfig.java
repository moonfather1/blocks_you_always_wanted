package moonfather.blocks_you_always_wanted;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class MainConfig
{
	public static class Common
	{
		private static final boolean defaultHangingSignsEnabled = true;
		private static final boolean defaultFencesEnabled = true;
		private static final boolean defaultGatesEnabled = true;

		public final ModConfigSpec.ConfigValue<Boolean> HangingSignsEnabled;
		public final ModConfigSpec.ConfigValue<Boolean> FencesEnabled;
		public final ModConfigSpec.ConfigValue<Boolean> GatesEnabled;

		public Common(ModConfigSpec.Builder builder)
		{
			builder.push("An explanation about this config file");
			ModConfigSpec.ConfigValue<String> Dummy = builder.comment("This line is just an explanation of this uncommon config system. Config is implemented like this because normal config can not be used to not-register blocks. Registration happens before pretty-much-anything. Never mind the details, we have one limitation to go through: THIS FILE MUST BE THE SAME ON CLIENT AND SERVER SIDE. There is no synchronization. If you want to turn fences off, that is fine, but it must be done on the server and all clients.")
										.define("dummy option", "just a placeholder for the comment");
			builder.pop();
			builder.push("Block types");
			this.HangingSignsEnabled = builder.comment("Enables hanging signs that display items. if you turn this off, existing signs may disappear.")
					.define("Hanging signs enabled", defaultHangingSignsEnabled);
			this.FencesEnabled = builder.comment("Enables placing fences onto slabs. if you turn this off, existing fences may disappear.")
											  .define("Fences-on-slabs enabled", defaultFencesEnabled);
			this.GatesEnabled = builder.comment("Enables wide gates. if you turn this off, existing gates may disappear.")
											  .define("Wide gates enabled", defaultGatesEnabled);
			builder.pop();
		}
	}

	///////////////////////////////////////////////////

	public static final Common COMMON;
	public static final ModConfigSpec COMMON_SPEC;

	static //constructor
	{
		Pair<Common, ModConfigSpec> commonSpecPair = new ModConfigSpec.Builder().configure(Common::new);
		COMMON = commonSpecPair.getLeft();
		COMMON_SPEC = commonSpecPair.getRight();
	}
}
