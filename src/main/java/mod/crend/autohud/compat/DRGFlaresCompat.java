//? if drg_flares {
package mod.crend.autohud.compat;

import me.lizardofoz.drgflares.config.ServerSettings;
import me.lizardofoz.drgflares.util.DRGFlarePlayerAspect;
import me.lizardofoz.drgflares.util.DRGFlaresUtil;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Components;
import net.minecraft.client.network.ClientPlayerEntity;

public class DRGFlaresCompat implements AutoHudApi {
	@Override
	public String modId() {
		return "drg_flares";
	}

	@Override
	public void tickState(ClientPlayerEntity player) {
		if (!DRGFlaresUtil.hasUnlimitedRegeneratingFlares(player)) {
			int count = DRGFlarePlayerAspect.clientLocal.getFlaresLeft();
			if (count < ServerSettings.CURRENT.regeneratingFlaresMaxCharges.value) {
				Components.Hotbar.reveal();
			}
		}
	}
}
//?}
