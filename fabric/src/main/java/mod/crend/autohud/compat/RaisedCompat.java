package mod.crend.autohud.compat;

import dev.yurisuika.raised.api.RaisedApi;
import dev.yurisuika.raised.util.properties.Element;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.MinecraftClient;

public class RaisedCompat {
	public static void tick(MinecraftClient client) {
		AutoHudRenderer.globalOffsetY = RaisedApi.getY(Element.HOTBAR);
	}

}
