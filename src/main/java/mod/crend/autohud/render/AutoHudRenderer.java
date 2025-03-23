package mod.crend.autohud.render;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Components;
import net.minecraft.client.gui.DrawContext;
//? if >1.20.1
/*import net.minecraft.client.render.RenderTickCounter;*/

import java.util.ArrayList;
import java.util.List;

public class AutoHudRenderer {
	public static boolean inRender;
	public static float tickDelta = 0.0f;
	public static float alpha = 1.0f;
	public static float globalOffsetX = 0;
	public static float globalOffsetY = 0;


	public static boolean shouldRenderHotbarItems() {
		// Render items when we're not doing anything with the hotbar
		return !AutoHud.targetHotbar
				// Render items when they're not fully hidden (in other words, visible in some way)
				|| !Components.Hotbar.fullyHidden()
				// If we are in fade mode, only render items if they're not fully transparent.
				|| (AutoHud.config.animationFade() && AutoHud.config.getHotbarItemsMaximumFade() > 0.0f)
				// If we are neither in fade nor move mode, skip rendering if it's hidden.
				// If we are in move mode, the items may still be visible in the "hidden" state!
				|| (!AutoHud.config.animationFade() && AutoHud.config.animationMove());
	}

	/**
	 * Modifies the given color to set its alpha value
	 */
	public static int modifyArgb(int argb) {
		int oldAlpha = argb >> 24;
		if ((oldAlpha & 0xFC) == 0) {
			oldAlpha = 0xFF;
		}
		return Math.round(alpha * oldAlpha) << 24 | (argb & 0xFFFFFF);
	}
	public static int getArgb() {
		return Math.round(alpha * 0xFF) << 24;
	}

	public static void injectTransparency() {
		if (inRender) {
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
		}
	}

	public static List<Component> active = new ArrayList<>();

	public static void startRender(DrawContext context, /*? if <1.21 {*/float/*?} else {*//*RenderTickCounter*//*?}*/ renderTickCounter) {
		inRender = true;
		tickDelta = renderTickCounter
				/*? if >1.21.4 {*/
						/*.getTickProgress(true)
				*//*?} else if >=1.21 {*/
						/*.getTickDelta(true)*/
				/*?}*/
		;
		active.clear();
	}

	public static void renderChatMessageIndicator(DrawContext context, /*? if <1.21 {*/float/*?} else {*//*RenderTickCounter*//*?}*/ ignored) {
		if (Components.ChatIndicator.config.active()) {
			ComponentRenderer.CHAT_MESSAGE_INDICATOR.wrap(context, ChatMessageIndicator::render);
		}
	}

	public static void endRender() {
		for (Component component : active) {
			System.err.println("Not cleaned up: " + component.identifier);
		}
		inRender = false;
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		//? if <=1.21.4
		RenderSystem.defaultBlendFunc();
	}
}
