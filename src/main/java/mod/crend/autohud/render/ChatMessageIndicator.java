package mod.crend.autohud.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.TutorialToast;

public class ChatMessageIndicator {
	public static void render(DrawContext context) {
		int x = 10;
		int y = MinecraftClient.getInstance().getWindow().getScaledHeight() - 30;
		TutorialToast.Type.SOCIAL_INTERACTIONS.drawIcon(context, x, y);
	}
}
