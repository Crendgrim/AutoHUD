//? if appleskin && neoforge {
/*package mod.crend.autohud.compat;

import mod.crend.autohud.AutoHudGui;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.render.ComponentRenderer;
import squeek.appleskin.client.HUDOverlayHandler;

public class AppleSkinCompat implements AutoHudApi {
	@Override
	public String modId() {
		return "appleskin";
	}

	@Override
	public void init() {
		AutoHudGui.COMPONENT_RENDERERS.put(HUDOverlayHandler.HealthOverlay.ID, ComponentRenderer.HEALTH);
		AutoHudGui.COMPONENT_RENDERERS.put(HUDOverlayHandler.HungerOverlay.ID, ComponentRenderer.HUNGER);
		AutoHudGui.COMPONENT_RENDERERS.put(HUDOverlayHandler.SaturationOverlay.ID, ComponentRenderer.HUNGER);
		AutoHudGui.COMPONENT_RENDERERS.put(HUDOverlayHandler.ExhaustionOverlay.ID, ComponentRenderer.HUNGER);
	}
}
*///?}
