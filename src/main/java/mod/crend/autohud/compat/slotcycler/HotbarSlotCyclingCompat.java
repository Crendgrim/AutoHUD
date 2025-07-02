//? if hotbarslotcycling {
/*package mod.crend.autohud.compat.slotcycler;

import mod.crend.autohud.api.AutoHudApi;

import mod.crend.autohud.component.Components;
import net.minecraft.client.network.ClientPlayerEntity;

public class HotbarSlotCyclingCompat implements AutoHudApi {
	@Override
	public String modId() {
		return "hotbarslotcycling";
	}

	@Override
	public void init() {
		WrappedCyclingSlotsRenderer.register();
	}

	@Override
	public void initState(ClientPlayerEntity player) {
		HotbarSlotCyclingComponents.HOTBAR_SLOT_CYCLING_COMPONENT.reveal();
	}

	@Override
	public void tickState(ClientPlayerEntity player) {
		// With bad timing, we can sometimes be one tick off from the hotbar.
		// That will make the slot cycler start moving just a bit earlier, which looks bad.
		if (HotbarSlotCyclingComponents.HOTBAR_SLOT_CYCLING_COMPONENT.fullyRevealed()) {
			HotbarSlotCyclingComponents.HOTBAR_SLOT_CYCLING_COMPONENT.synchronizeFrom(Components.Hotbar);
		}
	}

}
*///?}
