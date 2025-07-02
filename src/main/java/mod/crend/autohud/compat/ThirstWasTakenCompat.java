//? if thirstwastaken {
/*package mod.crend.autohud.compat;

import dev.ghen.thirst.Thirst;
import dev.ghen.thirst.api.ThirstHelper;
import dev.ghen.thirst.foundation.common.capability.ModCapabilities;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.AutoHudGui;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.state.EnhancedPolicyComponentState;
import mod.crend.autohud.config.ConfigHandler;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class ThirstWasTakenCompat implements AutoHudApi {
	@Override
	public String modId() {
		return Thirst.ID;
	}

	public static Component THIRST = Component.builder(Thirst.ID, "thirst_level")
			.inMainHud()
			.config(ConfigHandler.DummyPolicyComponent)
			.state(player -> new ThirstState())
			.build();
	public static ComponentRenderer THIRST_RENDERER = ComponentRenderer.of(THIRST);

	public static class ThirstState extends EnhancedPolicyComponentState {
		public static int thirst = 0;
		public ThirstState() {
			super(THIRST, () -> thirst, 20, () -> AutoHud.config.revealBarsWhenHoldingConsumableItem() && canDrink(), true);
		}

		private static boolean canDrink() {
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			if (player == null) return false;
			return (!player.getMainHandStack().isEmpty() && ThirstHelper.itemRestoresThirst(player.getMainHandStack()))
					|| (!player.getOffHandStack().isEmpty() && ThirstHelper.itemRestoresThirst(player.getOffHandStack()));
		}
	}

	@Override
	public void tickState(ClientPlayerEntity player) {
		player.getCapability(ModCapabilities.PLAYER_THIRST).ifPresent(thirst -> ThirstState.thirst = thirst.getThirst());
	}

	@Override
	public void init() {
		AutoHudGui.COMPONENT_RENDERERS.put(THIRST.identifier, THIRST_RENDERER);
	}
}
*///?}