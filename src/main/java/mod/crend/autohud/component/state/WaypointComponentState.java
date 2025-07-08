//? if >=1.21.6 {
/*package mod.crend.autohud.component.state;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.mixin.ClientWaypointHandlerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class WaypointComponentState extends ValueComponentState<Integer> {
	public WaypointComponentState(Component component) {
		super(component, WaypointComponentState::numberOfWaypoints, true);
	}

	private static int numberOfWaypoints() {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null) return 0;
		return ((ClientWaypointHandlerAccessor) player.networkHandler.getWaypointHandler()).getWaypoints().size();
	}

	@Override
	public void update() {
		if (AutoHud.config.revealLocatorBarWhenNearby() > 0) {
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			int threshold = AutoHud.config.revealLocatorBarWhenNearby() * AutoHud.config.revealLocatorBarWhenNearby();
			if (player != null && ((ClientWaypointHandlerAccessor) player.networkHandler.getWaypointHandler()).getWaypoints().values().stream().anyMatch(
					waypoint -> waypoint.squaredDistanceTo(player) <= threshold
			)) {
				component.revealCombined();
			}
		}
		super.update();
	}

	@Override
	protected boolean doReveal(Integer newValue) {
		return newValue != 0 && super.doReveal(newValue);
	}
}
*///?}
