package mod.crend.autohud.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import net.minecraft.client.gui.DrawContext;

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
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
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
	public static void preInject(DrawContext context, Component component) {
		if (AutoHud.config.animationFade()) {
			alpha = component.getAlpha(tickDelta);
			RenderSystem.enableBlend();
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
		}
		// Undo any translations we had before
		if (AutoHud.config.animationMove() || !AutoHud.config.animationFade()) {
			context.getMatrices().push();
			if (component.isHidden()) {
				context.getMatrices().translate(component.getOffsetX(tickDelta), component.getOffsetY(tickDelta), 0);
			}
		}
	}

	public static boolean shouldRenderCrosshair() {
		if (!Component.Crosshair.config.active()) return true;
		if (AutoHud.config.animationFade()) {
			return !Component.Crosshair.fullyHidden() || Component.Crosshair.config.maximumFade() > 0;
		}
		return !Component.Crosshair.isHidden();
	}
	public static void preInjectCrosshair() {
		if (Component.Crosshair.config.active()) {
			CustomFramebufferRenderer.init();
			RenderSystem.defaultBlendFunc();
			AutoHudRenderer.preInjectFade(Component.Crosshair, (float) Component.Crosshair.config.maximumFade());
		}
	}
	public static void postInjectCrosshair(DrawContext context) {
		if (Component.Crosshair.config.active()) {
			AutoHudRenderer.postInjectFade();
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
			CustomFramebufferRenderer.draw(context);
		}
	}

	public static void postInjectFade() {
		if (AutoHud.config.animationFade()) {
			alpha = 1.0f;
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
		}
	}
	public static void postInjectFadeWithReverseTranslation(DrawContext context) {
		postInjectFade();
		if (AutoHud.config.animationMove()) {
			context.getMatrices().pop();
		}
		if (globalOffsetX != 0 || globalOffsetY != 0) {
			context.getMatrices().pop();
		}
	}
	public static void postInject(DrawContext context) {
		if (AutoHud.config.animationFade()) {
			alpha = 1.0f;
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
		}
		if (AutoHud.config.animationMove() || !AutoHud.config.animationFade()) {
			context.getMatrices().pop();
		}
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

}
