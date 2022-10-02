package mod.crend.autohud.compat;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.ComponentState;
import net.minecraft.client.network.ClientPlayerEntity;

public class OneBarCompat implements AutoHudApi {
	public static Component OneBarComponent = new Component("OneBar", AutoHud.config.health(), true);
	static {
		Component.registerComponent(OneBarComponent);
		Component.ExperienceBar.addStackComponent(OneBarComponent);
		// Fake this API being inserted via entry point
		AutoHud.apis.add(new OneBarCompat());
	}

	@Override
	public void initState(ClientPlayerEntity player) {
		// Disable vanilla elements that are handled by OneBar
		AutoHud.targetExperienceBar = false;
		AutoHud.targetStatusBars = false;
		OneBarComponent.state = new ComponentState(OneBarComponent);
		OneBarComponent.hideNow();
	}

	@Override
	public void tickState(ClientPlayerEntity player) {
		OneBarComponent.synchronizeFrom(Component.Health);
		OneBarComponent.synchronizeFrom(Component.Hunger);
		OneBarComponent.synchronizeFrom(Component.Air);
		OneBarComponent.synchronizeFrom(Component.Armor);
	}
}
