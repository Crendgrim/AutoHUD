package mod.crend.autohud.render;

import mod.crend.autohud.component.Component;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.TutorialToast;

public class ChatMessageIndicator {
	public static void render(DrawContext context) {
		if (!Component.ChatIndicator.fullyHidden()) {
			AutoHudRenderer.preInject(context, Component.ChatIndicator);
			int x = 10;
			int y = MinecraftClient.getInstance().getWindow().getScaledHeight() - 30;
			TutorialToast.Type.SOCIAL_INTERACTIONS.drawIcon(context, x, y);
			AutoHudRenderer.postInject(context);
		}
	}
}
