package mod.crend.autohud.forge.compat;

import dev.yurisuika.raised.api.RaisedApi;
import dev.yurisuika.raised.util.properties.Element;
import mod.crend.autohud.render.AutoHudRenderer;

public class RaisedCompat {
	public static void tick() {
		AutoHudRenderer.globalOffsetY = RaisedApi.getY(Element.HOTBAR);
	}
}
