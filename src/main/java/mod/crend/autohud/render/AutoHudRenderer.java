package mod.crend.autohud.render;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Components;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.util.ArrayList;
import java.util.List;

public class AutoHudRenderer {
	public static boolean inRender;
	public static float tickDelta = 0.0f;
	public static float alpha = 1.0f;
	public static float globalOffsetX = 0;
	public static float globalOffsetY = 0;


	public static void preInjectFade(Component component, DrawContext context) {
		preInjectFade(component, context, (float) component.config.maximumFade());
	}
	public static void preInjectFade(Component component, DrawContext context, float minAlpha) {
		if (AutoHud.config.animationFade()) {
			alpha = Math.max(component.getAlpha(tickDelta), minAlpha);
			RenderSystem.enableBlend();
			float[] color = RenderSystem.getShaderColor();
			RenderSystem.setShaderColor(color[0], color[1], color[2], color[3] * alpha);
		}
	}
	public static void preInjectFadeWithReverseTranslation(Component component, DrawContext context) {
		preInjectFadeWithReverseTranslation(component, context, (float) component.config.maximumFade());
	}
	public static void preInjectFadeWithReverseTranslation(Component component, DrawContext context, float minAlpha) {
		preInjectFade(component, context, minAlpha);
		if (AutoHud.config.animationMove()) {
			context.getMatrices().push();
			if (component.isHidden()) {
				context.getMatrices().translate(-component.getOffsetX(tickDelta), -component.getOffsetY(tickDelta), 0);
			}
		}
		if (globalOffsetX != 0 || globalOffsetY != 0) {
			context.getMatrices().push();
			context.getMatrices().translate(globalOffsetX, globalOffsetY, 0);
		}
	}
	public static void preInject(Component component, DrawContext context) {
		preInject(component, context, (float) component.config.maximumFade());
	}
	public static void preInject(Component component, DrawContext context, float minAlpha) {
		preInjectFade(component, context, minAlpha);
		active.add(component);
		if (AutoHud.config.animationMove() || !AutoHud.config.animationFade()) {
			context.getMatrices().push();
			if (component.isHidden()) {
				context.getMatrices().translate(component.getOffsetX(tickDelta), component.getOffsetY(tickDelta), 0);
			}
		}
	}

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

	public static void postInjectFade(Component ignored, DrawContext context) {
		postInjectFade(context);
	}
	public static void postInjectFade(DrawContext context) {
		if (AutoHud.config.animationFade()) {
			//? if >=1.21.2
			/*context.draw();*/
			alpha = 1.0f;
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
		}
	}
	public static void postInjectFadeWithReverseTranslation(Component ignored, DrawContext context) {
		postInjectFadeWithReverseTranslation(context);
	}
	public static void postInjectFadeWithReverseTranslation(DrawContext context) {
		postInjectFade(context);
		if (AutoHud.config.animationMove()) {
			context.getMatrices().pop();
		}
		if (globalOffsetX != 0 || globalOffsetY != 0) {
			context.getMatrices().pop();
		}
	}
	public static void postInject(Component component, DrawContext context) {
		active.remove(component);
		postInjectFade(context);
		if (AutoHud.config.animationMove() || !AutoHud.config.animationFade()) {
			context.getMatrices().pop();
		}
	}

	public static boolean experienceLevelOverridesBar() {
		return (AutoHud.config.revealExperienceTextWithHotbar() && Components.Hotbar.shouldRender())
				|| (AutoHud.config.experience().onChange() && !AutoHud.config.experienceBar().onChange());
	}
	public static void moveExperienceText(DrawContext context) {
		Component experienceTextComponent = Components.ExperienceLevel;
		if (AutoHud.config.revealExperienceTextWithHotbar() && Components.Hotbar.isMoreVisibleThan(Components.ExperienceLevel)) {
			experienceTextComponent = Components.Hotbar;
		}
		context.getMatrices().translate(
				experienceTextComponent.getOffsetX(tickDelta) - Components.ExperienceBar.getOffsetX(tickDelta),
				experienceTextComponent.getOffsetY(tickDelta) - Components.ExperienceBar.getOffsetY(tickDelta),
				0);
		AutoHudRenderer.preInjectFade(experienceTextComponent, context);
	}
	public static void moveBackExperienceText(DrawContext context) {
		Component experienceTextComponent = Components.ExperienceLevel;
		if (AutoHud.config.revealExperienceTextWithHotbar() && Components.Hotbar.isMoreVisibleThan(Components.ExperienceLevel)) {
			experienceTextComponent = Components.Hotbar;
		}
		context.getMatrices().translate(
				Components.ExperienceBar.getOffsetX(tickDelta) - experienceTextComponent.getOffsetX(tickDelta),
				Components.ExperienceBar.getOffsetY(tickDelta) - experienceTextComponent.getOffsetY(tickDelta),
				0);
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

	static List<Component> active = new ArrayList<>();

	public static void startRender(DrawContext context, /*? if <1.21 {*/float/*?} else {*//*RenderTickCounter*//*?}*/ renderTickCounter) {
		inRender = true;
		tickDelta = renderTickCounter/*? if >=1.21 {*//*.getTickDelta(true)*//*?}*/;
		active.clear();
	}

	public static void renderChatMessageIndicator(DrawContext context, /*? if <1.21 {*/float/*?} else {*//*RenderTickCounter*//*?}*/ ignored) {
		RenderWrapper.CHAT_MESSAGE_INDICATOR.wrap(context, ChatMessageIndicator::render);
	}

	public static void endRender() {
		for (Component component : active) {
			System.err.println("Not cleaned up: " + component.name);
		}
		inRender = false;
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.defaultBlendFunc();
	}
}
