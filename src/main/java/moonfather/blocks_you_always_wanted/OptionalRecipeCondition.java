package moonfather.blocks_you_always_wanted;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.common.conditions.ICondition;

public class OptionalRecipeCondition implements ICondition
{
	private final String flagCode;

	private OptionalRecipeCondition(String value)
	{
		this.flagCode = value;
	}



	@Override
	public boolean test(IContext context)
	{
		if (this.flagCode.equals("hanging_signs_enabled"))
		{
			return MainConfig.COMMON.HangingSignsEnabled.get();
		}
		else if (this.flagCode.equals("gates_enabled"))
		{
			return MainConfig.COMMON.GatesEnabled.get();
		}
		else if (this.flagCode.equals("fences_enabled"))
		{
			return MainConfig.COMMON.FencesEnabled.get();
		}
		else
		{
			return false;
		}
	}

	/////////////////////////////////////////////////////

	@Override
	public MapCodec<? extends ICondition> codec()
	{
		return CODEC;
	}

	public static MapCodec<OptionalRecipeCondition> CODEC = RecordCodecBuilder.mapCodec(
			builder -> builder
					.group(
							Codec.STRING.fieldOf("flag_code").forGetter(orc -> orc.flagCode))
					.apply(builder, OptionalRecipeCondition::new));
}
