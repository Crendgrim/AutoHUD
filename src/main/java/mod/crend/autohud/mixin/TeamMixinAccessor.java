package mod.crend.autohud.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import mod.crend.autohud.component.state.ScoreboardComponentState;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * The only reason why this mixin was created was to avoid calling the scoreboard 
 * instance since that is always null when attempting to create a cache team, which 
 * is primarily used in {@link ScoreboardComponentState}.
 */
@Mixin(Team.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public interface TeamMixinAccessor {
    @Accessor("displayName")
    void autohud$setDisplayName(Text displayName);
    @Accessor("color")
    void autohud$setColor(Formatting color);

    /**
     * Please do not use this method directly unless you're absolutely sure that the
     * text being passed in the {@code prefix} parameter isn't {@code null}.
     * 
     * @param prefix sets the prefix for the team
     */
    @Accessor("prefix")
    void autohud$setPrefix(Text prefix);

    /**
     * Please do not use this method directly unless you're absolutely sure that the
     * text being passed in the {@code suffix} parameter isn't {@code null}.
     * 
     * @param suffix sets the suffix for the team
     */
    @Accessor("suffix")
    void autohud$setSuffix(Text suffix);
}
