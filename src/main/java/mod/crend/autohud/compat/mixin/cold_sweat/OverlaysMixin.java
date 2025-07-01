//? if coldsweat {
/*package mod.crend.autohud.compat.mixin.cold_sweat;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import com.momosoftworks.coldsweat.client.gui.Overlays;

@Mixin(value = Overlays.class, remap = false)
@MixinEnvironment(value = "compat", type = MixinEnvironment.Env.CLIENT)
public interface OverlaysMixin {
	@Accessor
	static double getBODY_TEMP() {
		return 0;
	}
}
*///?}
