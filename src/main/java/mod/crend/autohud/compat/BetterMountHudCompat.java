package mod.crend.autohud.compat;

import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.render.AutoHudRenderer;

public class BetterMountHudCompat implements AutoHudApi {
	@Override
	public String modId() {
		return "bettermounthud";
	}

	@Override
	public void init() {
		AutoHudRenderer.foodIsRenderedWithMountHealth = true;
	}
}
