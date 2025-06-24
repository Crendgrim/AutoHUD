//? if legendary_survival_overhaul {
/*package mod.crend.autohud.compat.legendarysurvivaloverhaul;

import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Components;
import mod.crend.autohud.AutoHudGui;
import sfiomn.legendarysurvivaloverhaul.LegendarySurvivalOverhaul;

public class LSOCompat implements AutoHudApi {

	@Override
	public String modId() {
		return LegendarySurvivalOverhaul.MOD_ID;
	}

	@Override
	public void init() {
		Components.Hunger.addStackComponent(LSOComponents.THIRST);
		Components.ExperienceBar.addStackComponent(LSOComponents.TEMPERATURE);
		AutoHudGui.COMPONENT_RENDERERS.put(LSOComponents.FOOD_BAR_COLD_EFFECT.identifier, LSOComponentRenderer.FOOD_BAR_COLD_EFFECT);
		AutoHudGui.COMPONENT_RENDERERS.put(LSOComponents.THIRST.identifier, LSOComponentRenderer.THIRST);
		AutoHudGui.COMPONENT_RENDERERS.put(LSOComponents.TEMPERATURE.identifier, LSOComponentRenderer.TEMPERATURE);
	}
}
*///?}
