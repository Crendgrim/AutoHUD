package mod.crend.autohud.compat;

//? if onebar {
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Components;
import mod.crend.autohud.component.state.ComponentState;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.network.ClientPlayerEntity;

public class OneBarCompat implements AutoHudApi {
	public static final String ONEBAR_MOD_ID = "onebar";
	@Override
	public String modId() {
		return ONEBAR_MOD_ID;
	}

	public static Component OneBarComponent = Component.builder(ONEBAR_MOD_ID, "onebar")
			.config(AutoHud.config.health())
			.inMainHud()
			.state(player -> new ComponentState(OneBarCompat.OneBarComponent))
			.build();
	public static final ComponentRenderer ONE_BAR_WRAPPER = ComponentRenderer.of(OneBarComponent);

	static {
		// Fake this API being inserted via entry point
		AutoHud.addApi(new OneBarCompat());
	}

	@Override
	public void init() {
		// Disable vanilla elements that are handled by OneBar
		AutoHud.targetExperienceBar = false;
		AutoHud.targetStatusBars = false;

		Components.ExperienceBar.addStackComponent(OneBarComponent);
	}

	@Override
	public void initState(ClientPlayerEntity player) {
		OneBarComponent.hideNow();
	}

	@Override
	public void tickState(ClientPlayerEntity player) {
		OneBarComponent.synchronizeFromHidden(
				Components.Health,
				Components.Hunger,
				Components.Air,
				Components.Armor
		);
	}
}
//?} else {
/*public class OneBarCompat { }
*///?}
