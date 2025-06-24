package mod.crend.autohud;

import mod.crend.autohud.component.Hud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ModKeyBindings {
	public static final String CATEGORY = "key.category.autohud";

	public static final KeyBinding TOGGLE_HUD = new KeyBinding(
			"key.autohud.toggle-hud",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_H,
			CATEGORY
	);

	public static KeyBinding PEEK_HUD = new KeyBinding(
			"key.autohud.peek-hud",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_R,
			CATEGORY
	);

	public static final List<KeyBinding> ALL = List.of(
			TOGGLE_HUD,
			PEEK_HUD
	);

	public static void clientTick(MinecraftClient client) {
		while (TOGGLE_HUD.wasPressed()) {
			Hud.toggleHud();
		}
		Hud.peekHud(PEEK_HUD.isPressed());
	}
}
