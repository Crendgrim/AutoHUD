package mod.crend.autohud.render;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import net.minecraft.client.gui.DrawContext;

public class AutoHudRenderer {
	public static boolean inRender;
	public static float tickDelta = 0.0f;
	public static float alpha = 1.0f;

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
	public static void preInjectFade(DrawContext context, Component component, float minAlpha) {
		preInjectFade(component, minAlpha);
		if (AutoHud.config.animationFade() && AutoHud.config.animationMove()) {
			context.getMatrices().push();
			if (component.isHidden()) {
				context.getMatrices().translate(-component.getOffsetX(tickDelta), -component.getOffsetY(tickDelta), 0);
			}
		}
	}
	public static void preInject(DrawContext context, Component component) {
		if (AutoHud.config.animationFade()) {
			alpha = component.getAlpha(tickDelta);
			RenderSystem.enableBlend();
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
		}
		if (AutoHud.config.animationMove() || !AutoHud.config.animationFade()) {
			context.getMatrices().push();
			if (component.isHidden()) {
				context.getMatrices().translate(component.getOffsetX(tickDelta), component.getOffsetY(tickDelta), 0);
			}
		}
	}

	public static void postInjectFade() {
		if (AutoHud.config.animationFade()) {
			alpha = 1.0f;
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
		}
	}
	public static void postInjectFade(DrawContext context) {
		postInjectFade();
		if (AutoHud.config.animationMove() || !AutoHud.config.animationFade()) {
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
