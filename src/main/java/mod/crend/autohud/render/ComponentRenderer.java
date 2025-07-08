package mod.crend.autohud.render;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Components;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

//? if <=1.21.4 {
import com.mojang.blaze3d.platform.GlStateManager;
//?}
//? if <=1.21.5
import mod.crend.libbamboo.render.CustomFramebufferRenderer;

public class ComponentRenderer {

	public static ComponentRenderer HOTBAR = builder(Components.Hotbar)
			.fade()
			.move()
			.doRender(() -> Components.Hotbar.shouldRender() || AutoHudRenderer.shouldRenderHotbarItems())
			.build();
	public static ComponentRenderer TOOLTIP = of(Components.Tooltip);
	//? if <1.21.6 {
	public static ComponentRenderer HOTBAR_ITEMS = builder(Components.Hotbar)
			.fade()
			.isActive(() -> Components.Hotbar.isActive() && AutoHud.config.animationFade())
			.doRender(AutoHudRenderer::shouldRenderHotbarItems)
			.withCustomFramebuffer(true)
			.maximumFade(AutoHud.config::getHotbarItemsMaximumFade)
			// We need to reset the renderer because otherwise the first item gets drawn with double alpha
			.beginRender(context -> AutoHudRenderLayer.FADE_MODE.postRender(Components.Hotbar, context))
			.build();
	//?}
	public static ComponentRenderer EXPERIENCE_BAR = of(Components.ExperienceBar);
	public static ComponentRenderer EXPERIENCE_LEVEL = of(Components.ExperienceLevel);
	//? if >=1.21.6
	/*public static ComponentRenderer INFO_BAR = of(Components.InfoBar);*/
	// For Forge, don't cancel the render event for the bar if the level has to be rendered.
	//? if forge {
	/*public static ComponentRenderer EXPERIENCE_BAR_FORGE = builder(Components.ExperienceBar)
			.move()
			.fade()
			.doRender(() -> Components.ExperienceBar.shouldRender() || Components.ExperienceLevel.shouldRender())
			.build();
	public static ComponentRenderer EXPERIENCE_LEVEL_FORGE = builder(Components.ExperienceLevel)
			.move()
			.fade()
			.beginRender(context -> {
				AutoHudRenderLayer.MOVE_MODE.postRender(Components.ExperienceBar, context);
				AutoHudRenderLayer.MOVE_MODE.preRender(Components.ExperienceLevel, context);
			})
			.endRender(context -> {
				AutoHudRenderLayer.MOVE_MODE.postRender(Components.ExperienceLevel, context);
				AutoHudRenderLayer.MOVE_MODE.preRender(Components.ExperienceBar, context);
			})
			.build();
	*///?}

	public static ComponentRenderer ARMOR = of(Components.Armor);
	public static ComponentRenderer ARMOR_FADE = builder(Components.Armor)
			.fade()
			.withCustomFramebuffer(true)
			.build();
	public static ComponentRenderer HEALTH = of(Components.Health);
	public static ComponentRenderer HUNGER = of(Components.Hunger);
	public static ComponentRenderer AIR = of(Components.Air);
	public static ComponentRenderer MOUNT_HEALTH = of(Components.MountHealth);
	public static ComponentRenderer MOUNT_JUMP_BAR = of(Components.MountJumpBar);
	public static ComponentRenderer SCOREBOARD = of(Components.Scoreboard);
	public static ComponentRenderer CROSSHAIR = builder(Components.Crosshair)
			//? if <=1.21.4 {
			.withCustomFramebuffer(false)
			.beginRender(context -> {
				RenderSystem.defaultBlendFunc();
				AutoHudRenderLayer.FADE_MODE.preRender(Components.Crosshair, context);
			})
			.endRender(context -> {
				AutoHudRenderLayer.FADE_MODE.postRender(Components.Crosshair, context);
				RenderSystem.enableBlend();
				RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
			})
			//?}
			//? if >=1.21.6 {
			/*.beginRender(context -> AutoHudRenderLayer.FADE_MODE.preRender(Components.Crosshair, context))
			.endRender(context -> AutoHudRenderLayer.FADE_MODE.postRender(Components.Crosshair, context))
			*///?}
			.build();
	public static ComponentRenderer CHAT = of(Components.Chat);
	public static ComponentRenderer BOSS_BAR = of(Components.BossBar);
	public static ComponentRenderer ACTION_BAR = of(Components.ActionBar);
	public static ComponentRenderer CHAT_MESSAGE_INDICATOR = of(Components.ChatIndicator);

	static Map<Identifier, ComponentRenderer> statusEffectComponents = new HashMap<>();

	public static void registerStatusEffectComponent(Component component) {
		statusEffectComponents.put(component.identifier, of(component));
	}
	public static ComponentRenderer getForStatusEffect(StatusEffectInstance instance) {
		return statusEffectComponents.get(Component.get(instance.getEffectType()).identifier);
	}
	//? if <=1.21.5 {
	public static ComponentRenderer getForStatusEffect(Sprite sprite) {
		return statusEffectComponents.get(Objects.requireNonNull(Component.findBySprite(sprite)).identifier);
	}
	//?}

	public static Builder builder(Component component) {
		return new Builder(component);
	}

	public static ComponentRenderer of(Component component) {
		return builder(component).move().fade().build();
	}


	private final Component component;
	private final Supplier<Boolean> isActive;
	private final Supplier<Boolean> doRender;
	private final Consumer<DrawContext> beginRender;
	private final Consumer<DrawContext> endRender;

	private ComponentRenderer(
			Component component,
			Supplier<Boolean> isActive,
			Supplier<Boolean> doRender,
			Consumer<DrawContext> beginRender,
			Consumer<DrawContext> endRender
	) {
		this.component = component;
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
		if (isActive()) {
			this.beginRender.accept(context);
		}
	}

	public void endRender(DrawContext context) {
		if (isActive()) {
			this.endRender.accept(context);
		}
	}

	public void wrap(DrawContext context, Runnable originalRenderCall) {
		wrap(context, originalRenderCall, originalRenderCall);
	}
	public void wrap(DrawContext context, Runnable customRenderCall, Runnable originalRenderCall) {
		if (isActive()) {
			if (doRender()) {
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

	public void beginFade(DrawContext context) {
		if (isActive()) {
			AutoHudRenderLayer.FADE_MODE.preRender(component, context);
		}
	}
	public void endFade(DrawContext context) {
		if (isActive()) {
			AutoHudRenderLayer.FADE_MODE.postRender(component, context);
		}
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

		public ComponentRenderer build() {
			if (isActive == null) {
				isActive = component::isActive;
			}
			if (doRender == null) {
				doRender = component::shouldRender;
			}
			if (maximumFade == null) {
				maximumFade = () -> (float) component.config.maximumFade();
			}
			AutoHudRenderLayer wrapper;
			if (move || !fade) {
				wrapper = AutoHudRenderLayer.MOVE_MODE;
			} else {
				wrapper = AutoHudRenderLayer.FADE_MODE;
			}
			if (beginRender == null) {
				beginRender = context -> wrapper.preRender(component, context, maximumFade.get());
			}
			if (endRender == null) {
				endRender = context -> wrapper.postRender(component, context);
			}
			return new ComponentRenderer(
					component,
					isActive,
					doRender,
					customFramebuffer ? context -> {
						beginRender.accept(context);
						//RenderWrapper.create(context);
						//? if <=1.21.5
						CustomFramebufferRenderer.init();
					} : beginRender,
					customFramebuffer ? getCustomFramebufferEndRender() : endRender
			);
		}

		private Consumer<DrawContext> getCustomFramebufferEndRender() {
			if (fade) {
				if (containedInMovedComponent) {
					return context -> {
						endRender.accept(context);
						//? if <=1.21.5 {
						AutoHudRenderLayer.FADE_MODE_WITH_REVERSE_TRANSLATION.wrap(component, context, maximumFade.get(), () ->
							CustomFramebufferRenderer.draw(context)
						);
						//?}
						/*AutoHudRenderLayer.FADE_MODE_WITH_REVERSE_TRANSLATION.wrap(component, context, maximumFade.get(), () ->
								RenderWrapper.cleanup()
						);
						 */
					};
				}
				return context -> {
					endRender.accept(context);
					//? if <=1.21.5 {
					AutoHudRenderLayer.FADE_MODE.wrap(component, context, maximumFade.get(), () ->
							CustomFramebufferRenderer.draw(context)
					);
					//?}
					//AutoHudRenderLayer.FADE_MODE.wrap(component, context, maximumFade.get(), () -> RenderWrapper.cleanup());
				};
			}
			return context -> {
				endRender.accept(context);
				//RenderWrapper.cleanup();
				//? if <=1.21.5
				CustomFramebufferRenderer.draw(context);
			};
		}
	}
}
