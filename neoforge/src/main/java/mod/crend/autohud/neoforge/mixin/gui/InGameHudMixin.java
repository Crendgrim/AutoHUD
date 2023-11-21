package mod.crend.autohud.neoforge.mixin.gui;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Hud;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.autohud.render.CustomFramebufferRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	// Hotbar
	@Inject(method = "renderHotbar", at = @At("HEAD"))
	private void autoHud$preHotbar(float tickDelta, DrawContext context, CallbackInfo ci) {
		if (AutoHud.targetHotbar) {
			AutoHudRenderer.preInjectFade(Component.Hotbar);
		}
	}

	@Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;getMatrices()Lnet/minecraft/client/util/math/MatrixStack;", ordinal = 0))
	private void autoHud$hotbarTransparency(float tickDelta, DrawContext context, CallbackInfo ci) {
		AutoHudRenderer.injectTransparency();
	}

	// Tooltip
	@Inject(method = "renderHeldItemTooltip", at = @At("HEAD"))
	private void autoHud$preTooltip(DrawContext context, CallbackInfo ci) {
		if (AutoHud.targetHotbar) {
			AutoHudRenderer.preInjectFade(Component.Tooltip);
		}
	}

	// Hotbar items
	@Inject(
			method = "renderHotbar",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V"
			)
	)
	private void autoHud$preTransparentHotbarItems(float tickDelta, DrawContext context, CallbackInfo ci) {
		if (AutoHud.targetHotbar && AutoHud.config.animationFade()) {
			// Don't render items if they're fully invisible anyway
			if (!Component.Hotbar.fullyHidden() || AutoHud.config.getHotbarItemsMaximumFade() > 0.0f) {
				// We need to reset the renderer because otherwise the first item gets drawn with double alpha
				AutoHudRenderer.postInjectFade();
				// Setup custom framebuffer
				CustomFramebufferRenderer.init();
				// Have the original call draw onto the custom framebuffer
			}
		}
	}
	@Inject(
			method = "renderHotbar",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V",
					shift = At.Shift.AFTER
			)
	)
	private void autoHud$postTransparentHotbarItems(float tickDelta, DrawContext context, CallbackInfo ci) {
		if (AutoHud.targetHotbar && AutoHud.config.animationFade()) {
			// Don't render items if they're fully invisible anyway
			if (!Component.Hotbar.fullyHidden() || AutoHud.config.getHotbarItemsMaximumFade() > 0.0f) {
				// We have rendered onto the custom framebuffer now
				// Render the contents of the custom framebuffer as a texture with transparency onto the main framebuffer
				AutoHudRenderer.preInjectFade(context, Component.Hotbar, AutoHud.config.getHotbarItemsMaximumFade());
				CustomFramebufferRenderer.draw(context);
				AutoHudRenderer.postInjectFade(context);
			}
		}
	}

	// Scoreboard
	@Inject(method = "renderScoreboardSidebar", at=@At("HEAD"))
	private void autoHud$preScoreboardSidebar(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
		if (AutoHud.targetScoreboard) {
			AutoHudRenderer.preInjectFade(Component.Scoreboard);
		}
	}
	@ModifyArg(method = "renderScoreboardSidebar", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"), index = 4)
	private int autoHud$scoreboardSidebarString(int color) {
		if (AutoHudRenderer.inRender) {
			return AutoHudRenderer.getArgb() | 0xFFFFFF;
		}
		return color;
	}
	@ModifyArg(method = "renderScoreboardSidebar", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I"), index = 4)
	private int autoHud$scoreboardSidebarText(int color) {
		if (AutoHudRenderer.inRender) {
			return AutoHudRenderer.getArgb() | 0xFFFFFF;
		}
		return color;
	}
	@ModifyArg(method = "renderScoreboardSidebar", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"), index=4)
	private int autoHud$scoreboardSidebarFill(int color) {
		if (AutoHudRenderer.inRender) {
			return AutoHudRenderer.modifyArgb(color);
		}
		return color;
	}

	// Mount
	@Inject(method = "renderMountJumpBar", at=@At("HEAD"))
	private void autoHud$preMountJumpBar(JumpingMount mount, DrawContext context, int x, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.preInjectFade(Component.MountJumpBar);
		}
	}

	// Experience bar
	@Inject(method = "renderExperienceBar", at=@At("HEAD"))
	private void autoHud$preExperienceBar(DrawContext context, int x, CallbackInfo ci) {
		if (AutoHud.targetExperienceBar) {
			AutoHudRenderer.preInjectFade(Component.ExperienceBar);
		}
	}

	// Status Effects
	@Inject(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;getEffectType()Lnet/minecraft/entity/effect/StatusEffect;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void autoHud$preEffect(DrawContext context, CallbackInfo ci, Collection<StatusEffectInstance> collection, Screen screen, int i, int j, StatusEffectSpriteManager statusEffectSpriteManager, List<Runnable> list, Iterator<StatusEffectInstance> var7, StatusEffectInstance statusEffectInstance) {
		if (AutoHud.targetStatusEffects && Hud.shouldShowIcon(statusEffectInstance)) {
			AutoHudRenderer.preInject(context, Component.get(statusEffectInstance.getEffectType()));
		}
	}
	@Inject(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/StatusEffectSpriteManager;getSprite(Lnet/minecraft/entity/effect/StatusEffect;)Lnet/minecraft/client/texture/Sprite;"))
	private void autoHud$postEffect(DrawContext context, CallbackInfo ci) {
		if (AutoHud.targetStatusEffects) {
			AutoHudRenderer.postInject(context);
		}
	}
	@Inject(method = {"m_279741_", "method_18620"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawSprite(IIIIILnet/minecraft/client/texture/Sprite;)V"), require = 0)
	private static void autoHud$preSprite(DrawContext context, float f, int i, int j, Sprite sprite, CallbackInfo ci) {
		if (AutoHud.targetStatusEffects) {
			Component component = Component.findBySprite(sprite);
			if (component != null) {
				AutoHudRenderer.preInject(context, component);
			} else {
				context.getMatrices().push();
			}
		}
	}
	@Inject(method = {"m_279741_", "method_18620"}, at = @At(value = "RETURN"), require = 0)
	private static void autoHud$postSprite(DrawContext drawContext, float f, int i, int j, Sprite sprite, CallbackInfo ci) {
		if (AutoHud.targetStatusEffects) {
			AutoHudRenderer.postInject(drawContext);
		}
	}

	@Redirect(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target="Lnet/minecraft/entity/effect/StatusEffectInstance;shouldShowIcon()Z"))
	private boolean autoHud$shouldShowIconProxy(StatusEffectInstance instance) {
		return Hud.shouldShowIcon(instance);
	}
}
