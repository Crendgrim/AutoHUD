package mod.crend.autohud.compat;

//? if raised {
import dev.yurisuika.raised.api.RaisedApi;
import dev.yurisuika.raised.util.properties.Element;
//?}
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.MinecraftClient;

public class RaisedCompat {
	public static void tick() {
		//? if raised
		AutoHudRenderer.globalOffsetY = RaisedApi.getY(Element.HOTBAR);
	}
	public static void tick(MinecraftClient client) {
		tick();
	}
}
