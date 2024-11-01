package mod.crend.autohud.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.libbamboo.render.CustomFramebufferRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class AutoHudRenderer {
	public static boolean inRender;
	public static float tickDelta = 0.0f;
	public static float alpha = 1.0f;
	public static float globalOffsetX = 0;
	public static float globalOffsetY = 0;

	public static void preInjectFade(Component component) {
		preInjectFade(component, 0.0f);
	}
	public static void preInjectFade(Component component, float minAlpha) {
		if (AutoHud.config.animationFade()) {
			alpha = Math.max(component.getAlpha(tickDelta), minAlpha);
			RenderSystem.enableBlend();
			float[] color = RenderSystem.getShaderColor();
			RenderSystem.setShaderColor(color[0], color[1], color[2], color[3] * alpha);
		}
	}
	public static void preInjectFadeWithReverseTranslation(DrawContext context, Component component, float minAlpha) {
		preInjectFade(component, minAlpha);
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
		preInject(context, component);
	}
	public static void preInject(DrawContext context, Component component) {
		preInjectFade(component);
		// Undo any translations we had before
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
				|| !Component.Hotbar.fullyHidden()
				// If we are in fade mode, only render items if they're not fully transparent.
				|| (AutoHud.config.animationFade() && AutoHud.config.getHotbarItemsMaximumFade() > 0.0f)
				// If we are neither in fade nor move mode, skip rendering if it's hidden.
				// If we are in move mode, the items may still be visible in the "hidden" state!
				|| (!AutoHud.config.animationFade() && AutoHud.config.animationMove());
	}
	public static boolean shouldRenderCrosshair() {
		if (!Component.Crosshair.config.active()) return true;
		if (AutoHud.config.animationFade()) {
			return !Component.Crosshair.fullyHidden() || Component.Crosshair.config.maximumFade() > 0;
		}
		return !Component.Crosshair.isHidden();
	}

	public static void postInjectFade(DrawContext context) {
		if (AutoHud.config.animationFade()) {
			//? if >=1.21.2
			//context.draw();
			alpha = 1.0f;
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
		}
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
	public static void postInject(Component ignored, DrawContext context) {
		postInject(context);
	}
	public static void postInject(DrawContext context) {
		postInjectFade(context);
		if (AutoHud.config.animationMove() || !AutoHud.config.animationFade()) {
			context.getMatrices().pop();
		}
	}

	public static boolean experienceLevelOverridesBar() {
		return (AutoHud.config.revealExperienceTextWithHotbar() && !Component.Hotbar.fullyHidden())
				|| (AutoHud.config.experience().onChange() && !AutoHud.config.experienceBar().onChange());
	}
	public static void moveExperienceText(DrawContext context) {
		Component experienceTextComponent = Component.ExperienceLevel;
		if (AutoHud.config.revealExperienceTextWithHotbar() && Component.Hotbar.isMoreVisibleThan(Component.ExperienceLevel)) {
			experienceTextComponent = Component.Hotbar;
		}
		context.getMatrices().translate(
				experienceTextComponent.getOffsetX(tickDelta) - Component.ExperienceBar.getOffsetX(tickDelta),
				experienceTextComponent.getOffsetY(tickDelta) - Component.ExperienceBar.getOffsetY(tickDelta),
				0);
		AutoHudRenderer.preInjectFade(experienceTextComponent);
	}
	public static void moveBackExperienceText(DrawContext context) {
		Component experienceTextComponent = Component.ExperienceLevel;
		if (AutoHud.config.revealExperienceTextWithHotbar() && Component.Hotbar.isMoreVisibleThan(Component.ExperienceLevel)) {
			experienceTextComponent = Component.Hotbar;
		}
		context.getMatrices().translate(
				Component.ExperienceBar.getOffsetX(tickDelta) - experienceTextComponent.getOffsetX(tickDelta),
				Component.ExperienceBar.getOffsetY(tickDelta) - experienceTextComponent.getOffsetY(tickDelta),
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

	public static void startRender(DrawContext context, /*? if <1.21 {*/float/*?} else {*//*RenderTickCounter*//*?}*/ renderTickCounter) {
		inRender = true;
		tickDelta = renderTickCounter/*? if >=1.21 {*//*.getTickDelta(true)*//*?}*/;
		ChatMessageIndicator.render(context);
	}

	public static void endRender() {
		inRender = false;
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.defaultBlendFunc();
	}

}
