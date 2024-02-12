package mod.crend.autohud.compat;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.state.ItemStackComponentState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;

public class HotbarSlotCyclingCompat implements AutoHudApi {
	public static Component HotbarSlotCyclerComponent = new Component("hotbarslotcycling", AutoHud.config.hotbar());
	public static ItemStack forwardStack = ItemStack.EMPTY;

	static {
		// Fake this API being inserted via entry point
		AutoHud.addApi(new HotbarSlotCyclingCompat());
	}

	@Override
	public String modId() {
		return "hotbarslotcycling";
	}

	@Override
	public void initState(ClientPlayerEntity player) {
		Component.registerComponent(HotbarSlotCyclerComponent);
		HotbarSlotCyclerComponent.state = new ItemStackComponentState(HotbarSlotCyclerComponent, () -> forwardStack, true);
	}

	@Override
	public void tickState(ClientPlayerEntity player) {
		// With bad timing, we can sometimes be one tick off from the hotbar.
		// That will make the slot cycler start moving just a bit earlier, which looks bad.
		if (HotbarSlotCyclerComponent.fullyRevealed()) {
			HotbarSlotCyclerComponent.synchronizeFrom(Component.Hotbar);
		}
	}
}
