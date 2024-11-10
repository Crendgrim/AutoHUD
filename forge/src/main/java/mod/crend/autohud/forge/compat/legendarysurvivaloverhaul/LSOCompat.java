package mod.crend.autohud.forge.compat.legendarysurvivaloverhaul;

import mod.crend.autohud.forge.AutoHudGui;

public class LSOCompat {
	public static void register() {
		AutoHudGui.COMPONENT_RENDERERS.put(LSOComponents.FOOD_BAR_COLD_EFFECT.identifier, LSOComponentRenderer.FOOD_BAR_COLD_EFFECT);
		AutoHudGui.COMPONENT_RENDERERS.put(LSOComponents.THIRST.identifier, LSOComponentRenderer.THIRST);
		AutoHudGui.COMPONENT_RENDERERS.put(LSOComponents.TEMPERATURE.identifier, LSOComponentRenderer.TEMPERATURE);
	}
}
