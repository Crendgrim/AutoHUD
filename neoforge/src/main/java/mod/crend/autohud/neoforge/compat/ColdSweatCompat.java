package mod.crend.autohud.neoforge.compat;

//? if coldsweat {
import com.momosoftworks.coldsweat.ColdSweat;
import com.momosoftworks.coldsweat.api.util.Temperature;
import com.momosoftworks.coldsweat.client.gui.Overlays;
import com.momosoftworks.coldsweat.common.capability.handler.EntityTempManager;
import com.momosoftworks.coldsweat.common.capability.temperature.PlayerTempCap;
import com.momosoftworks.coldsweat.config.ConfigSettings;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.state.ValueComponentState;
import mod.crend.autohud.config.ConfigHandler;
import mod.crend.autohud.neoforge.AutoHudGui;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
//?}
import mod.crend.autohud.api.AutoHudApi;

public class ColdSweatCompat implements AutoHudApi {
	@Override
	public String modId() {
		return "cold_sweat";
	}

//? if coldsweat {
	public static Component BODY_TEMP_GAUGE = Component.builder(ColdSweat.MOD_ID, "body_temp")
			.inMainHud()
			.config(ConfigHandler.DummyBooleanComponent)
			.state(player -> new ValueComponentState<>(ColdSweatCompat.BODY_TEMP_GAUGE, () -> Overlays.BODY_TEMP, true))
			.build();
	public static Component WORLD_TEMP_GAUGE = Component.builder(ColdSweat.MOD_ID, "world_temp")
			.inMainHud()
			.config(ConfigHandler.DummyBooleanComponent)
			.state(player -> new TemperatureState(ColdSweatCompat.WORLD_TEMP_GAUGE))
			.build();
	public static Component VAGUE_TEMP_GAUGE = Component.builder(ColdSweat.MOD_ID, "vague_temp")
			.inMainHud()
			.config(ConfigHandler.DummyBooleanComponent)
			.state(player -> new TemperatureState(ColdSweatCompat.VAGUE_TEMP_GAUGE))
			.build();

	static class TemperatureState extends ValueComponentState<Integer> {
		static double PLAYER_MAX_TEMP;
		static double PLAYER_MIN_TEMP;

		public TemperatureState(Component component) {
			super(component, () -> {
				double temp = Temperature.convert(Overlays.WORLD_TEMP, ConfigSettings.CELSIUS.get() ? Temperature.Units.C : Temperature.Units.F, Temperature.Units.MC, true);
				return Overlays.getGaugeSeverity(temp, PLAYER_MIN_TEMP, PLAYER_MAX_TEMP);
			}, true);
		}

		@Override
		protected boolean doReveal(Integer newValue) {
			return super.doReveal(newValue) || newValue < -2 || newValue > 2;
		}
	}

	@Override
	public void tickState(ClientPlayerEntity player) {
		EntityTempManager.getTemperatureCap(player).ifPresent((icap) -> {
			if (icap instanceof PlayerTempCap cap) {
				TemperatureState.PLAYER_MAX_TEMP = cap.getTrait(Temperature.Trait.BURNING_POINT);
				TemperatureState.PLAYER_MIN_TEMP = cap.getTrait(Temperature.Trait.FREEZING_POINT);
			}
		});
	}

	public static ComponentRenderer BODY_TEMP_RENDERER = ComponentRenderer.of(BODY_TEMP_GAUGE);
	public static ComponentRenderer WORLD_TEMP_RENDERER = ComponentRenderer.of(WORLD_TEMP_GAUGE);
	public static ComponentRenderer VAGUE_TEMP_RENDERER = ComponentRenderer.of(VAGUE_TEMP_GAUGE);

	@Override
	public void init() {
		AutoHudGui.COMPONENT_RENDERERS.put(BODY_TEMP_GAUGE.identifier, BODY_TEMP_RENDERER);
		AutoHudGui.COMPONENT_RENDERERS.put(WORLD_TEMP_GAUGE.identifier, WORLD_TEMP_RENDERER);
		AutoHudGui.COMPONENT_RENDERERS.put(VAGUE_TEMP_GAUGE.identifier, VAGUE_TEMP_RENDERER);
	}
//?}
}
