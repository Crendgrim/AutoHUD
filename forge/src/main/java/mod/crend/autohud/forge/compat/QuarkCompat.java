package mod.crend.autohud.forge.compat;

import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Components;
import net.minecraft.client.network.ClientPlayerEntity;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.management.module.HotbarChangerModule;

public class QuarkCompat implements AutoHudApi {
	@Override
	public String modId() {
		return Quark.MOD_ID;
	}

	@Override
	public void tickState(ClientPlayerEntity player) {
		if (HotbarChangerModule.hotbarChangeOpen) {
			Components.Hotbar.reveal();
		}
	}
}
