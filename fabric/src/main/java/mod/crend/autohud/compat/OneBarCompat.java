package mod.crend.autohud.compat;

//? if onebar {
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Components;
import mod.crend.autohud.component.state.ComponentState;
import mod.crend.autohud.render.RenderWrapper;
import net.minecraft.client.network.ClientPlayerEntity;

public class OneBarCompat implements AutoHudApi {
	@Override
	public String modId() {
		return "onebar";
	}

	public static Component OneBarComponent = Component.builder("OneBar")
			.config(AutoHud.config.health())
			.inMainHud()
			.state(player -> new ComponentState(OneBarCompat.OneBarComponent))
			.build();
	public static final RenderWrapper ONE_BAR_WRAPPER = RenderWrapper.of(OneBarComponent);

	static {
		// Fake this API being inserted via entry point
		AutoHud.addApi(new OneBarCompat());
	}

	@Override
	public void initState(ClientPlayerEntity player) {
		// Disable vanilla elements that are handled by OneBar
		AutoHud.targetExperienceBar = false;
		AutoHud.targetStatusBars = false;

		Components.ExperienceBar.addStackComponent(OneBarComponent);
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
