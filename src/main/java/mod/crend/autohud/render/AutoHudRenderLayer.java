package mod.crend.autohud.render;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import net.minecraft.client.gui.DrawContext;

public interface AutoHudRenderLayer {
	AutoHudRenderLayer DO_MOVE = new DoMove();
	AutoHudRenderLayer DO_FADE = new DoFade();
	AutoHudRenderLayer DO_REVERSE_TRANSLATION = new DoReverseTranslation();
	AutoHudRenderLayer DO_GLOBAL_TRANSLATION = new DoGlobalTranslation();

	AutoHudRenderLayer MOVE_MODE = new MoveMode();
	AutoHudRenderLayer FADE_MODE = new FadeMode();
	AutoHudRenderLayer FADE_MODE_WITH_REVERSE_TRANSLATION = new FadeModeWithReverseTranslation();

	default boolean shouldApply() { return true; }

	void doPreRender(Component component, DrawContext context, float minAlpha);
	default void preRender(Component component, DrawContext context, float minAlpha) {
		if (shouldApply()) doPreRender(component, context, minAlpha);
	}
	default void preRender(Component component, DrawContext context) {
		preRender(component, context, (float) component.config.maximumFade());
	}

	void doPostRender(Component component, DrawContext context);
	default void postRender(Component component, DrawContext context) {
		if (shouldApply()) doPostRender(component, context);
	}

	default void wrap(Component component, DrawContext context, Runnable original) {
		wrap(component, context, (float) component.config.maximumFade(), original);
	}
	default void wrap(Component component, DrawContext context, float minAlpha, Runnable original) {
		preRender(component, context, minAlpha);
		original.run();
		postRender(component, context);
	}

	class DoMove implements AutoHudRenderLayer {
		@Override
		public boolean shouldApply() {
			return (AutoHud.config.animationMove() || !AutoHud.config.animationFade());
		}

		@Override
		public void doPreRender(Component component, DrawContext context, float ignored) {
			AutoHudRenderer.active.add(component);
			context.getMatrices().push();
			if (component.isHidden()) {
				context.getMatrices().translate(component.getOffsetX(AutoHudRenderer.tickDelta), component.getOffsetY(AutoHudRenderer.tickDelta), 0);
			}
		}

		@Override
		public void doPostRender(Component component, DrawContext context) {
			AutoHudRenderer.active.remove(component);
			context.getMatrices().pop();
		}
	}

	class DoFade implements AutoHudRenderLayer {
		@Override
		public boolean shouldApply() {
			return AutoHud.config.animationFade();
		}

		@Override
		public void doPreRender(Component component, DrawContext context, float minAlpha) {
			AutoHudRenderer.alpha = Math.max(component.getAlpha(AutoHudRenderer.tickDelta), minAlpha);
			//? if <=1.21.4
			RenderSystem.enableBlend();
			float[] color = RenderSystem.getShaderColor();
			RenderSystem.setShaderColor(color[0], color[1], color[2], color[3] * AutoHudRenderer.alpha);
		}

		@Override
		public void doPostRender(Component component, DrawContext context) {
			//? if >=1.21.2
			/*context.draw();*/
			AutoHudRenderer.alpha = 1.0f;
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, AutoHudRenderer.alpha);
		}
	}

	class DoReverseTranslation implements AutoHudRenderLayer {
		@Override
		public boolean shouldApply() {
			return AutoHud.config.animationMove();
		}

		@Override
		public void doPreRender(Component component, DrawContext context, float minAlpha) {
			context.getMatrices().push();
			if (component.isHidden()) {
				context.getMatrices().translate(-component.getOffsetX(AutoHudRenderer.tickDelta), -component.getOffsetY(AutoHudRenderer.tickDelta), 0);
			}
		}

		@Override
		public void doPostRender(Component component, DrawContext context) {
			context.getMatrices().pop();
		}
	}

	class DoGlobalTranslation implements AutoHudRenderLayer {
		@Override
		public boolean shouldApply() {
			return (AutoHudRenderer.globalOffsetX != 0 || AutoHudRenderer.globalOffsetY != 0);
		}

		@Override
		public void doPreRender(Component component, DrawContext context, float minAlpha) {
			context.getMatrices().push();
			context.getMatrices().translate(AutoHudRenderer.globalOffsetX, AutoHudRenderer.globalOffsetY, 0);
		}

		@Override
		public void doPostRender(Component component, DrawContext context) {
			context.getMatrices().pop();
		}
	}


	class MoveMode implements AutoHudRenderLayer {
		@Override
		public void doPreRender(Component component, DrawContext context, float minAlpha) {
			DO_FADE.preRender(component, context, minAlpha);
			DO_MOVE.preRender(component, context, minAlpha);
		}

		@Override
		public void doPostRender(Component component, DrawContext context) {
			DO_MOVE.postRender(component, context);
			DO_FADE.postRender(component, context);
		}
	}

	class FadeMode implements AutoHudRenderLayer {
		@Override
		public void doPreRender(Component component, DrawContext context, float minAlpha) {
			DO_FADE.preRender(component, context, minAlpha);
		}

		@Override
		public void doPostRender(Component component, DrawContext context) {
			DO_FADE.postRender(component, context);
		}
	}

	class FadeModeWithReverseTranslation implements AutoHudRenderLayer {
		@Override
		public void doPreRender(Component component, DrawContext context, float minAlpha) {
			DO_FADE.preRender(component, context, minAlpha);
			DO_REVERSE_TRANSLATION.preRender(component, context, minAlpha);
			DO_GLOBAL_TRANSLATION.preRender(component, context, minAlpha);
		}

		@Override
		public void doPostRender(Component component, DrawContext context) {
			DO_GLOBAL_TRANSLATION.postRender(component, context);
			DO_REVERSE_TRANSLATION.postRender(component, context);
			DO_FADE.postRender(component, context);
		}
	}
}
