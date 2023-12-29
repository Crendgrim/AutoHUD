package mod.crend.autohud.forge.compat;

import dev.yurisuika.raised.client.option.RaisedConfig;
import mod.crend.autohud.render.AutoHudRenderer;

public class RaisedCompat {
	public static void tick() {
		AutoHudRenderer.globalOffsetY = RaisedConfig.getHud();
	}
}
