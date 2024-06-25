package mod.crend.autohud.compat;

import mod.crend.autohud.render.AutoHudRenderer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

public class RaisedCompat {
	public static void tick(MinecraftClient client) {
		var shared = FabricLoader.getInstance().getObjectShare().get("raised:hud");
		AutoHudRenderer.globalOffsetY = (shared == null ? 0 : (int) shared);
	}

}
