//? if raised {
package mod.crend.autohud.compat;

import dev.yurisuika.raised.api.RaisedApi;
import dev.yurisuika.raised.registry.LayerRegistry;
import mod.crend.autohud.api.AutoHudApi;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.network.ClientPlayerEntity;

public class RaisedCompat implements AutoHudApi {
	@Override
	public String modId() {
		return "raised";
	}

	@Override
	public void tickState(ClientPlayerEntity player) {
		AutoHudRenderer.globalOffsetY = -RaisedApi.getY(LayerRegistry.HOTBAR);
	}
}
//?}
