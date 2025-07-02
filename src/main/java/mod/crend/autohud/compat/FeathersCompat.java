//? if feathers {
/*package mod.crend.autohud.compat;

import com.elenai.feathers.Feathers;
import com.elenai.feathers.client.ClientFeathersData;
import mod.crend.autohud.AutoHudGui;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.state.PolicyComponentState;
import mod.crend.autohud.config.ConfigHandler;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.util.Identifier;

public class FeathersCompat implements AutoHudApi {
	@Override
	public String modId() {
		return Feathers.MODID;
	}

	public static Component FEATHERS = Component.builder(Identifier.of(Feathers.MODID, "feathers"))
			.inMainHud()
			.config(ConfigHandler.DummyPolicyComponent)
			.state(player -> new PolicyComponentState(FeathersCompat.FEATHERS, ClientFeathersData::getFeathers, ClientFeathersData::getMaxFeathers, true))
			.build();
	public static ComponentRenderer FEATHERS_RENDERER = ComponentRenderer.of(FEATHERS);

	@Override
	public void init() {
		AutoHudGui.COMPONENT_RENDERERS.put(FEATHERS.identifier, FEATHERS_RENDERER);
	}
}
*///?}
