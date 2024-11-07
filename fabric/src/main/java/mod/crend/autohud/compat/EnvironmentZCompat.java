package mod.crend.autohud.compat;

//? if environmentz {
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.state.ValueComponentState;
import mod.crend.autohud.config.ConfigHandler;
import mod.crend.autohud.render.RenderWrapper;
import net.environmentz.access.TemperatureManagerAccess;
import net.environmentz.temperature.TemperatureManager;
import net.environmentz.temperature.Temperatures;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.function.Supplier;

public class EnvironmentZCompat implements AutoHudApi {
	@Override
	public String modId() {
		return "environmentz";
	}

	public static Component Temperature = Component.builder("Player Temperature").config(ConfigHandler.DummyPolicyComponent).inMainHud().build();
	public static Component Thermometer = Component.builder("Thermometer").config(ConfigHandler.DummyPolicyComponent).inMainHud().build();
	public static RenderWrapper TEMPERATURE_WRAPPER = RenderWrapper.of(Temperature);
	public static RenderWrapper THERMOMETER_WRAPPER = RenderWrapper.of(Thermometer);

	static {
		// Fake this API being inserted via entry point
		AutoHud.addApi(new EnvironmentZCompat());
	}

	static int temperatureState() {
		TemperatureManager temperatureManager = ((TemperatureManagerAccess) MinecraftClient.getInstance().player).getTemperatureManager();
		int playerTemperature = temperatureManager.getPlayerTemperature();
		if (playerTemperature < Temperatures.getBodyTemperatures(2)) {
			// cold
			if (playerTemperature < Temperatures.getBodyTemperatures(1)) {
				// very cold
				return -2;
			}
			return -1;
		} else if (playerTemperature > Temperatures.getBodyTemperatures(4)) {
			// hot
			if (playerTemperature > Temperatures.getBodyTemperatures(5)) {
				// very hot
				return 2;
			}
			return 1;
		}
		return 0;
	}

	static int thermometerState() {
		TemperatureManager temperatureManager = ((TemperatureManagerAccess) MinecraftClient.getInstance().player).getTemperatureManager();
		int thermometerTemperature = temperatureManager.getThermometerTemperature();
		if (thermometerTemperature <= Temperatures.getThermometerTemperatures(0)) {
			return -2; // very cold
		} else if (thermometerTemperature <= Temperatures.getThermometerTemperatures(1)) {
			return -1; // cold
		} else if (thermometerTemperature <= Temperatures.getThermometerTemperatures(2)) {
			return 0; // medium
		} else if (thermometerTemperature <= Temperatures.getThermometerTemperatures(3)) {
			return 1; // hot
		} else {
			return 2; // very hot
		}
	}

	static class EnvironmentZState extends ValueComponentState<Integer> {
		public EnvironmentZState(Component component, Supplier<Integer> newValueSupplier) {
			super(component, newValueSupplier, true);
		}

		@Override
		protected boolean doReveal(Integer newValue) {
			return newValue < -1 || newValue > 1 || super.doReveal(newValue);
		}
	}


	@Override
	public void initState(ClientPlayerEntity player) {
		Component.registerComponent(Temperature);
		Component.registerComponent(Thermometer);

		Temperature.state = new EnvironmentZState(Temperature, EnvironmentZCompat::temperatureState);
		Thermometer.state = new EnvironmentZState(Thermometer, EnvironmentZCompat::thermometerState);
	}

	@Override
	public void tickState(ClientPlayerEntity player) {
		TemperatureManager temperatureManager = ((TemperatureManagerAccess) player).getTemperatureManager();

		if (temperatureManager.getThermometerCalm() > 0) {
			Thermometer.reveal();
		}
	}
}
//?} else {
/*public class EnvironmentZCompat {
}
*///?}
