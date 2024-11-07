package mod.crend.autohud.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Components;
import mod.crend.libbamboo.render.CustomFramebufferRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RenderWrapper {

	public static RenderWrapper HOTBAR = of(Components.Hotbar);
	public static RenderWrapper TOOLTIP = of(Components.Tooltip);
	public static RenderWrapper HOTBAR_ITEMS = builder(Components.Hotbar)
			.fade()
			.isActive(() -> Components.Hotbar.isActive() && AutoHud.config.animationFade())
			.doRender(AutoHudRenderer::shouldRenderHotbarItems)
			.withCustomFramebuffer(true)
			.maximumFade(AutoHud.config::getHotbarItemsMaximumFade)
			// We need to reset the renderer because otherwise the first item gets drawn with double alpha
			.beginRender(AutoHudRenderer::postInjectFade)
			.build();
	public static RenderWrapper EXPERIENCE_BAR = of(Components.ExperienceBar);
	public static RenderWrapper EXPERIENCE_LEVEL = of(Components.ExperienceLevel);
	public static RenderWrapper ARMOR = of(Components.Armor);
	public static RenderWrapper ARMOR_FADE = builder(Components.Armor)
			.fade()
			.withCustomFramebuffer(true)
			.build();
	public static RenderWrapper HEALTH = of(Components.Health);
	public static RenderWrapper HUNGER = of(Components.Hunger);
	public static RenderWrapper AIR = of(Components.Air);
	public static RenderWrapper MOUNT_HEALTH = of(Components.MountHealth);
	public static RenderWrapper MOUNT_JUMP_BAR = of(Components.MountJumpBar);
	public static RenderWrapper SCOREBOARD = of(Components.Scoreboard);
	public static RenderWrapper CROSSHAIR = builder(Components.Crosshair)
			.withCustomFramebuffer(false)
			.beginRender(context -> {
				RenderSystem.defaultBlendFunc();
				AutoHudRenderer.preInjectFade(Components.Crosshair, context, (float) Components.Crosshair.config.maximumFade());
			})
			.endRender(context -> {
				AutoHudRenderer.postInjectFade(context);
				RenderSystem.enableBlend();
				RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
			})
			.build();
	public static RenderWrapper CHAT = of(Components.Chat);
	public static RenderWrapper BOSS_BAR = of(Components.BossBar);
	public static RenderWrapper ACTION_BAR = of(Components.ActionBar);
	public static StatusEffectRenderWrapper STATUS_EFFECT = new StatusEffectRenderWrapper();
	public static RenderWrapper CHAT_MESSAGE_INDICATOR = of(Components.ChatIndicator);


	public static Builder builder(Component component) {
		return new Builder(component);
	}

	public static RenderWrapper of(Component component) {
		return builder(component).move().fade().build();
	}


	private final Consumer<DrawContext> beginRender;
	private final Consumer<DrawContext> endRender;
	private final Supplier<Boolean> doRender;
	private final Supplier<Boolean> isActive;

	private RenderWrapper(
			Supplier<Boolean> isActive,
			Supplier<Boolean> doRender,
			Consumer<DrawContext> beginRender,
			Consumer<DrawContext> endRender
	) {
		this.isActive = isActive;
		this.doRender = doRender;
		this.beginRender = beginRender;
		this.endRender = endRender;
	}

	public boolean isActive() {
		return isActive.get();
	}

	public boolean doRender() {
		return doRender.get();
	}

	public void beginRender(DrawContext context) {
		if (isActive() && doRender()) {
			this.beginRender.accept(context);
		}
	}

	public void endRender(DrawContext context) {
		if (isActive() && doRender()) {
			this.endRender.accept(context);
		}
	}

	public void wrap(DrawContext context, Runnable originalRenderCall) {
		wrap(context, originalRenderCall, originalRenderCall);
	}
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
	public void wrap(DrawContext context, Consumer<DrawContext> originalRenderCall) {
		wrap(context, originalRenderCall, originalRenderCall);
	}
	public void wrap(DrawContext context, Consumer<DrawContext> customRenderCall, Consumer<DrawContext> originalRenderCall) {
		wrap(context, () -> customRenderCall.accept(context), () -> originalRenderCall.accept(context));
	}

	public static class Builder {
		private final Component component;
		private boolean fade = false;
		private boolean move = false;
		private boolean customFramebuffer = false;
		private boolean containedInMovedComponent = false;
		private Supplier<Float> maximumFade = null;
		private Supplier<Boolean> isActive = null;
		private Supplier<Boolean> doRender = null;
		private Consumer<DrawContext> beginRender = null;
		private Consumer<DrawContext> endRender = null;

		Builder(Component component) {
			this.component = component;
		}

		public Builder fade() {
			this.fade = true;
			return this;
		}

		public Builder move() {
			this.move = true;
			return this;
		}

		public Builder withCustomFramebuffer(boolean containedInMovedComponent) {
			this.customFramebuffer = true;
			this.containedInMovedComponent = containedInMovedComponent;
			return this;
		}

		public Builder maximumFade(Supplier<Float> maximumFade) {
			this.maximumFade = maximumFade;
			return this;
		}

		public Builder isActive(Supplier<Boolean> isActive) {
			this.isActive = isActive;
			return this;
		}

		public Builder doRender(Supplier<Boolean> doRender) {
			this.doRender = doRender;
			return this;
		}

		public Builder beginRender(Consumer<DrawContext> beginRender) {
			this.beginRender = beginRender;
			return this;
		}

		public Builder endRender(Consumer<DrawContext> endRender) {
			this.endRender = endRender;
			return this;
		}

		public RenderWrapper build() {
			if (isActive == null) {
				isActive = component::isActive;
			}
			if (doRender == null) {
				doRender = component::shouldRender;
			}
			if (maximumFade == null) {
				maximumFade = () -> (float) component.config.maximumFade();
			}
			if (beginRender == null) {
				if (!fade && !move) {
					beginRender = context -> {};
				} else {
					beginRender = move
							? context -> AutoHudRenderer.preInject(component, context, maximumFade.get())
							: context -> AutoHudRenderer.preInjectFade(component, context, maximumFade.get());
				}
			}
			if (endRender == null) {
				if (!fade && !move) {
					endRender = context -> {};
				} else {
					endRender = move
							? context -> AutoHudRenderer.postInject(component, context)
							: context -> AutoHudRenderer.postInjectFade(component, context);
				}
			}
			if (customFramebuffer) {
				return new RenderWrapper(
						isActive,
						doRender,
						context -> {
							beginRender.accept(context);
							CustomFramebufferRenderer.init();
						},
						getEndRender()
				);
			} else {
				return new RenderWrapper(isActive, doRender, beginRender, endRender);
			}
		}

		private Consumer<DrawContext> getEndRender() {
			if (fade) {
				if (containedInMovedComponent) {
					return context -> {
						endRender.accept(context);
						AutoHudRenderer.preInjectFadeWithReverseTranslation(component, context, maximumFade.get());
						CustomFramebufferRenderer.draw(context);
						AutoHudRenderer.postInjectFadeWithReverseTranslation(component, context);
					};
				}
				return context -> {
					endRender.accept(context);
					AutoHudRenderer.preInjectFade(component, context, maximumFade.get());
					CustomFramebufferRenderer.draw(context);
					AutoHudRenderer.postInjectFade(component, context);
				};
			}
			return context -> {
				endRender.accept(context);
				CustomFramebufferRenderer.draw(context);
				RenderSystem.defaultBlendFunc();
			};
		}
	}


	public static class StatusEffectRenderWrapper {
		public StatusEffectRenderWrapper() {
		}

		public boolean isActive() {
			return AutoHud.targetStatusEffects;
		}

		public boolean doRender(StatusEffectInstance instance) {
			Component component = Component.get(instance.getEffectType());
			return component != null && component.shouldRender();
		}

		public boolean doRender(Sprite sprite) {
			Component component = Component.findBySprite(sprite);
			return component != null && component.shouldRender();
		}

		public void preInject(DrawContext context, StatusEffectInstance instance) {
			if (isActive() && doRender(instance)) {
				AutoHudRenderer.preInject(Component.get(instance.getEffectType()), context);
			}
		}

		public void preInject(DrawContext context, Sprite sprite) {
			if (isActive()) {
				Component component = Component.findBySprite(sprite);
				if (component != null && component.shouldRender()) {
					AutoHudRenderer.preInject(component, context);
				}
			}
		}

		public void postInject(DrawContext context, Sprite sprite) {
			if (isActive()) {
				Component component = Component.findBySprite(sprite);
				if (component != null && component.shouldRender()) {
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
