//? if overflowing_bars {
package mod.crend.autohud.compat;

import fuzs.overflowingbars.OverflowingBars;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Components;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.network.ClientPlayerEntity;

public class OverflowingBarsCompat implements AutoHudApi {
	@Override
	public String modId() {
		return OverflowingBars.MOD_ID;
	}

	public static final Component TOUGHNESS = Component.builder("overflowingbars", "toughness")
			.isTargeted(() -> AutoHud.targetStatusBars)
			.config(AutoHud.config.armor())
			.inMainHud()
			.state(player -> Components.Armor.state)
			.build();
	public static final ComponentRenderer TOUGHNESS_RENDERER = ComponentRenderer.of(TOUGHNESS);

	@Override
	public void tickState(ClientPlayerEntity player) {
		TOUGHNESS.synchronizeFrom(Components.Armor);
	}

	@Override
	public void init() {
		Components.Hunger.addStackComponent(TOUGHNESS);
	}
}
//?}
