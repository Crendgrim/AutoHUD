package mod.crend.autohud.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.libbamboo.render.CustomFramebufferRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface RenderWrapper {

	RenderWrapper HOTBAR = new ComponentRenderer(Component.Hotbar);
	RenderWrapper TOOLTIP = new ComponentRenderer(Component.Tooltip);
	RenderWrapper HOTBAR_ITEMS = new CustomRenderer(
			() -> Component.Hotbar.isActive() && AutoHud.config.animationFade(),
			AutoHudRenderer::shouldRenderHotbarItems,
			context -> {
				// We need to reset the renderer because otherwise the first item gets drawn with double alpha
				AutoHudRenderer.postInjectFade(context);
				// Setup custom framebuffer
				CustomFramebufferRenderer.init();
				// Have the original call draw onto the custom framebuffer
			},
			context -> {
				// Render the contents of the custom framebuffer as a texture with transparency onto the main framebuffer
				AutoHudRenderer.preInjectFadeWithReverseTranslation(Component.Hotbar, context, AutoHud.config.getHotbarItemsMaximumFade());
				CustomFramebufferRenderer.draw(context);
				AutoHudRenderer.postInjectFadeWithReverseTranslation(context);
			}
	);
	RenderWrapper EXPERIENCE_BAR = new ComponentRenderer(Component.ExperienceBar);
	RenderWrapper EXPERIENCE_LEVEL = new ComponentRenderer(Component.ExperienceLevel);
	RenderWrapper ARMOR = new ComponentRenderer(Component.Armor);
	RenderWrapper HEALTH = new ComponentRenderer(Component.Health);
	RenderWrapper HUNGER = new ComponentRenderer(Component.Hunger);
	RenderWrapper AIR = new ComponentRenderer(Component.Air);
	RenderWrapper MOUNT_HEALTH = new ComponentRenderer(Component.MountHealth);
	RenderWrapper MOUNT_JUMP_BAR = new ComponentRenderer(Component.MountJumpBar);
	RenderWrapper SCOREBOARD = new ComponentRenderer(Component.Scoreboard);
	RenderWrapper CROSSHAIR = new CustomRenderer(
			() -> Component.Crosshair.isActive(),
			AutoHudRenderer::shouldRenderCrosshair,
			context -> {
				RenderSystem.defaultBlendFunc();
				AutoHudRenderer.preInjectFade(Component.Crosshair, (float) Component.Crosshair.config.maximumFade());
				CustomFramebufferRenderer.init();
			},
			context -> {
				AutoHudRenderer.postInjectFade(context);
				RenderSystem.enableBlend();
				RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
				CustomFramebufferRenderer.draw(context);
				RenderSystem.defaultBlendFunc();
			}
	);
	RenderWrapper CHAT = new ComponentRenderer(Component.Chat);
	RenderWrapper BOSS_BAR = new ComponentRenderer(Component.BossBar);
	RenderWrapper ACTION_BAR = new ComponentRenderer(Component.ActionBar);
	StatusEffectRenderWrapper STATUS_EFFECT = new StatusEffectRenderWrapper();
	RenderWrapper CHAT_MESSAGE_INDICATOR = new ComponentRenderer(Component.ChatIndicator);

	RenderWrapper withCustomFramebuffer();
	void beginRender(DrawContext context);
	void endRender(DrawContext context);
	void wrap(DrawContext context, Runnable customRenderCall, Runnable originalRenderCall);
	default void wrap(DrawContext context, Consumer<DrawContext> customRenderCall, Consumer<DrawContext> originalRenderCall) {
		wrap(context, () -> customRenderCall.accept(context), () -> originalRenderCall.accept(context));
	}
	default void wrap(DrawContext context, Runnable renderCall) {
		wrap(context, renderCall, renderCall);
	}
	default void wrap(DrawContext context, Consumer<DrawContext> renderCall) {
		wrap(context, renderCall, renderCall);
	}
	boolean isActive();
	boolean doRender();

	class ComponentRenderer implements RenderWrapper {
		private final Component component;
		private final BiConsumer<Component, DrawContext> beginRender;
		private final BiConsumer<Component, DrawContext> endRender;
		private final FramebufferRenderer framebufferRenderer;

		public ComponentRenderer(Component component) {
			this(component, AutoHudRenderer::preInject, AutoHudRenderer::postInject);
		}
		public ComponentRenderer(Component component, BiConsumer<Component, DrawContext> beginRender, BiConsumer<Component, DrawContext> endRender) {
			this.component = component;
			this.beginRender = beginRender;
			this.endRender = endRender;
			this.framebufferRenderer = new FramebufferRenderer(
					this,
					context -> {
						AutoHudRenderer.preInjectFade(component, (float) component.config.maximumFade());
					},
					AutoHudRenderer::postInjectFade
			);
		}

		@Override
		public RenderWrapper withCustomFramebuffer() {
			return framebufferRenderer;
		}

		@Override
		public boolean isActive() {
			return this.component.isActive();
		}

		@Override
		public boolean doRender() {
			return !this.component.fullyHidden();
		}

		@Override
		public void beginRender(DrawContext context) {
			if (isActive()) {
				this.beginRender.accept(this.component, context);
			}
		}

		@Override
		public void endRender(DrawContext context) {
			if (isActive()) {
				this.endRender.accept(this.component, context);
			}
		}

		@Override
		public void wrap(DrawContext context, Runnable customRenderCall, Runnable originalRenderCall) {
			if (isActive()) {
				if (doRender()) {
					this.beginRender.accept(this.component, context);
					customRenderCall.run();
					this.endRender.accept(this.component, context);
				}
			} else {
				originalRenderCall.run();
			}
		}
	}

	class FramebufferRenderer implements RenderWrapper {
		RenderWrapper wrappedRenderWrapper;
		private final Consumer<DrawContext> framebufferSetup;
		private final Consumer<DrawContext> framebufferTeardown;

		public FramebufferRenderer(RenderWrapper wrappedRenderWrapper, Consumer<DrawContext> framebufferSetup, Consumer<DrawContext> framebufferTeardown) {
			this.wrappedRenderWrapper = wrappedRenderWrapper;
			this.framebufferSetup = framebufferSetup;
			this.framebufferTeardown = framebufferTeardown;
		}

		@Override
		public RenderWrapper withCustomFramebuffer() {
			return this;
		}

		@Override
		public void beginRender(DrawContext context) {
			if (isActive()) {
				CustomFramebufferRenderer.init();
				wrappedRenderWrapper.beginRender(context);
			}
		}

		@Override
		public void endRender(DrawContext context) {
			if (isActive()) {
				wrappedRenderWrapper.endRender(context);
				framebufferSetup.accept(context);
				CustomFramebufferRenderer.draw(context);
				framebufferTeardown.accept(context);
			}
		}

		@Override
		public void wrap(DrawContext context, Runnable customRenderCall, Runnable originalRenderCall) {
			if (isActive()) {
				if (doRender()) {
					CustomFramebufferRenderer.init();
					wrappedRenderWrapper.wrap(context, customRenderCall, originalRenderCall);
					framebufferSetup.accept(context);
					CustomFramebufferRenderer.draw(context);
					framebufferTeardown.accept(context);
				}
			} else {
				originalRenderCall.run();
			}
		}

		@Override
		public boolean isActive() {
			return wrappedRenderWrapper.isActive();
		}

		@Override
		public boolean doRender() {
			return wrappedRenderWrapper.doRender();
		}
	}

	class CustomRenderer implements RenderWrapper {
		private final Consumer<DrawContext> beginRender;
		private final Consumer<DrawContext> endRender;
		private final Supplier<Boolean> doRender;
		private final Supplier<Boolean> isActive;
		private final FramebufferRenderer framebufferRenderer;

		public CustomRenderer(
				Supplier<Boolean> isActive,
				Supplier<Boolean> doRender,
				Consumer<DrawContext> beginRender,
				Consumer<DrawContext> endRender
		) {
			this.isActive = isActive;
			this.doRender = doRender;
			this.beginRender = beginRender;
			this.endRender = endRender;
			this.framebufferRenderer = new FramebufferRenderer(this, context -> {}, context -> {});
		}

		@Override
		public RenderWrapper withCustomFramebuffer() {
			return framebufferRenderer;
		}

		@Override
		public boolean isActive() {
			return isActive.get();
		}

		@Override
		public boolean doRender() {
			return doRender.get();
		}

		@Override
		public void beginRender(DrawContext context) {
			if (isActive() && doRender()) {
				this.beginRender.accept(context);
			}
		}

		@Override
		public void endRender(DrawContext context) {
			if (isActive() && doRender()) {
				this.endRender.accept(context);
			}
		}

		@Override
		public void wrap(DrawContext context, Runnable customRenderCall, Runnable originalRenderCall) {
			if (isActive()) {
				if (doRender.get()) {
					this.beginRender.accept(context);
					customRenderCall.run();
					this.endRender.accept(context);
				}
			} else {
				originalRenderCall.run();
			}
		}
	}

	class StatusEffectRenderWrapper {
		public StatusEffectRenderWrapper() {
		}

		public boolean isActive() {
			return AutoHud.targetStatusEffects;
		}

		public boolean doRender(StatusEffectInstance instance) {
			Component component = Component.get(instance.getEffectType());
			return component != null && !component.fullyHidden();
		}
		public boolean doRender(Sprite sprite) {
			Component component = Component.findBySprite(sprite);
			return component != null && !component.fullyHidden();
		}

		public void preInject(DrawContext context, StatusEffectInstance instance) {
			if (isActive() && doRender(instance)) {
				AutoHudRenderer.preInject(Component.get(instance.getEffectType()), context);
			}
		}

		public void preInject(DrawContext context, Sprite sprite) {
			if (isActive()) {
				Component component = Component.findBySprite(sprite);
				if (component != null && !component.fullyHidden()) {
					AutoHudRenderer.preInject(component, context);
				}
			}
		}

		public void postInject(DrawContext context, Sprite sprite) {
			if (isActive()) {
				Component component = Component.findBySprite(sprite);
				if (component != null && !component.fullyHidden()) {
					AutoHudRenderer.postInject(component, context);
				}
			}
		}

		public void wrap(DrawContext context, Sprite sprite, Runnable renderCall) {
			if (isActive()) {
				Component component = Component.findBySprite(sprite);
				if (component != null) {
					AutoHudRenderer.preInject(component, context);
					renderCall.run();
					AutoHudRenderer.postInject(component, context);
				}
			} else {
				renderCall.run();
			}
		}
		public void wrap(DrawContext context, StatusEffectInstance instance, Runnable renderCall) {
			if (isActive()) {
				if (doRender(instance)) {
					Component component = Component.get(instance.getEffectType());
					AutoHudRenderer.preInject(component, context);
					renderCall.run();
					AutoHudRenderer.postInject(component, context);
				}
			} else {
				renderCall.run();
			}
		}
	}
}
