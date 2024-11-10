package mod.crend.autohud.forge.compat.legendarysurvivaloverhaul;

import mod.crend.autohud.component.state.ComponentState;
import net.minecraft.client.network.ClientPlayerEntity;
import sfiomn.legendarysurvivaloverhaul.api.temperature.TemperatureEnum;
import sfiomn.legendarysurvivaloverhaul.common.capabilities.temperature.TemperatureCapability;
import sfiomn.legendarysurvivaloverhaul.common.capabilities.wetness.WetnessCapability;
import sfiomn.legendarysurvivaloverhaul.util.CapabilityUtil;

import java.util.Objects;

public class TemperatureState extends ComponentState {
	ClientPlayerEntity player;
	TemperatureCapability TEMPERATURE_CAP;
	WetnessCapability WETNESS_CAP;
	TemperatureEnum oldTemperature;
	int oldWetness;

	public TemperatureState(ClientPlayerEntity player) {
		super(LSOComponents.TEMPERATURE, true);
		this.player = player;
		this.TEMPERATURE_CAP = CapabilityUtil.getTempCapability(player);
		this.WETNESS_CAP = CapabilityUtil.getWetnessCapability(player);
	}

	@Override
	public void update() {
		TemperatureEnum newTemperature = TEMPERATURE_CAP.getTemperatureEnum();
		int newWetness = WETNESS_CAP.getWetness();
		if (doReveal(newTemperature, newWetness)) {
			component.revealCombined();
		}
		oldTemperature = newTemperature;
		oldWetness = newWetness;
	}

	protected boolean doReveal(TemperatureEnum newTemperature, int newWetness) {
		return !component.config.active()
				|| (!Objects.equals(newTemperature, oldTemperature) || oldWetness != newWetness)
				|| newWetness > 0
				|| forceReveal()
				;
	}
	private boolean forceReveal() {
		return switch (TEMPERATURE_CAP.getTemperatureEnum()) {
			case FROSTBITE, HEAT_STROKE -> true;
			case COLD, NORMAL, HOT -> false;
		};
	}
}
