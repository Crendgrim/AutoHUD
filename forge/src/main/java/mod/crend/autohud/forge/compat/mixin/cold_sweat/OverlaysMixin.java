package mod.crend.autohud.forge.compat.mixin.cold_sweat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
//? if coldsweat {
import com.momosoftworks.coldsweat.client.gui.Overlays;

@Mixin(value = Overlays.class, remap = false)
public interface OverlaysMixin {
	@Accessor
	static double getBODY_TEMP() {
		return 0;
	}
}
//?} else {

/*import mod.crend.libbamboo.VersionUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = VersionUtils.class, remap = false)
public class OverlaysMixin {
}
*///?}
