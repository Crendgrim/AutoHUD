//? if coldsweat {
/*package mod.crend.autohud.compat.mixin.cold_sweat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import com.momosoftworks.coldsweat.client.gui.Overlays;

@Mixin(value = Overlays.class, remap = false)
public interface OverlaysMixin {
	@Accessor
	static double getBODY_TEMP() {
		return 0;
	}
}
*///?}
