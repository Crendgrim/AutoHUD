//? if stamina {
/*package mod.crend.autohud.compat;

import insane96mcp.stamina.Stamina;
import insane96mcp.stamina.stamina.StaminaFeature;
import insane96mcp.stamina.stamina.StaminaHandler;
import mod.crend.autohud.AutoHudGui;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Components;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;

public class StaminaCompat implements AutoHudApi {
	@Override
	public String modId() {
		return Stamina.MOD_ID;
	}

	@Override
	public void tickState(ClientPlayerEntity player) {
		if (StaminaHandler.getStamina(player) < StaminaHandler.getMaxStamina(player)) {
			Components.Health.reveal();
		}
	}

	@Override
	public void init() {
		AutoHudGui.COMPONENT_RENDERERS.put(Identifier.of(modId(), StaminaFeature.OVERLAY), ComponentRenderer.HEALTH);
	}
}
*///?}
