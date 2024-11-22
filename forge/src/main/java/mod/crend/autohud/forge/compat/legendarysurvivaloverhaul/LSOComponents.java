//? if legendary_survival_overhaul {
package mod.crend.autohud.forge.compat.legendarysurvivaloverhaul;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Components;
import mod.crend.autohud.component.state.BooleanComponentState;
import mod.crend.autohud.component.state.PolicyComponentState;
import sfiomn.legendarysurvivaloverhaul.LegendarySurvivalOverhaul;
import sfiomn.legendarysurvivaloverhaul.common.capabilities.thirst.ThirstCapability;
import sfiomn.legendarysurvivaloverhaul.registry.MobEffectRegistry;
import sfiomn.legendarysurvivaloverhaul.util.CapabilityUtil;

public class LSOComponents {
	public static Component FOOD_BAR_COLD_EFFECT = Component.builder(LegendarySurvivalOverhaul.MOD_ID, "cold_hunger")
			.isTargeted(Components.TARGET_STATUS_BARS)
			.config(AutoHud.config.hunger())
			.inMainHud()
			.state(player -> new BooleanComponentState(LSOComponents.FOOD_BAR_COLD_EFFECT, () -> player.hasStatusEffect(MobEffectRegistry.COLD_HUNGER.get()), true))
			.build();

	static ThirstCapability THIRST_CAP;
	public static Component THIRST = Component.builder(LegendarySurvivalOverhaul.MOD_ID, "thirst")
			.isTargeted(Components.TARGET_STATUS_BARS)
			.config(AutoHud.config.hunger())
			.inMainHud()
			.state(player -> new PolicyComponentState(LSOComponents.THIRST, () -> {
				if (THIRST_CAP == null || player.age % 20 == 0) {
					THIRST_CAP = CapabilityUtil.getThirstCapability(player);
				}
				return THIRST_CAP.getHydrationLevel();
			}, () -> 20, true))
			.build();

	public static Component TEMPERATURE = Component.builder(LegendarySurvivalOverhaul.MOD_ID, "temperature")
			.inMainHud()
			.state(TemperatureState::new)
			.build();

}
//?}
