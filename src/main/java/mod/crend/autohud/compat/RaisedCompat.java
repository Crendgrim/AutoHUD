//? if raised {
package mod.crend.autohud.compat;

import dev.yurisuika.raised.api.RaisedApi;
import dev.yurisuika.raised.registry.LayerRegistry;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.MinecraftClient;

public class RaisedCompat {
	public static void tick() {
		AutoHudRenderer.globalOffsetY = -RaisedApi.getY(LayerRegistry.HOTBAR);
	}
	public static void tick(MinecraftClient client) {
		tick();
	}
}
//?}
