package mod.crend.autohud.forge.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Debug;
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
@Debug(export = true)
public class InGameHudMixin {
	// Hotbar
	@Inject(method = "renderHotbar", at = @At("HEAD"))
	private void autoHud$preHotbar(float tickDelta, DrawContext context, CallbackInfo ci) {
		if (AutoHud.targetHotbar) {
			AutoHudRenderer.preInject(context, Component.Hotbar);
		}
	}

	@Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;getMatrices()Lnet/minecraft/client/util/math/MatrixStack;", ordinal = 0))
	private void autoHud$hotbarTransparency(float tickDelta, DrawContext context, CallbackInfo ci) {
		AutoHudRenderer.injectTransparency();
	}

	@Inject(method = "renderHotbar", at = @At("RETURN"))
	private void autoHud$postHotbar(float tickDelta, DrawContext context, CallbackInfo ci) {
		if (AutoHud.targetHotbar) {
			AutoHudRenderer.postInject(context);
		}
	}

	// Tooltip
	@WrapOperation(
			method = "renderHeldItemTooltip",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderSelectedItemName(Lnet/minecraft/client/gui/DrawContext;I)V"
			)
	)
	private static void autoHud$wrapTooltip(InGameHud instance, DrawContext context, int yShift, Operation<Void> original) {
		if (AutoHud.targetHotbar) {
			AutoHudRenderer.preInject(context, Component.Tooltip);
		}
		original.call(instance, context, yShift);
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.postInject(context);
		}
	}

	// Hotbar items
	@WrapOperation(
			method = "renderHotbar",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V"
			)
	)
	private void autoHud$transparentHotbarItems(InGameHud instance, DrawContext context, int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed, Operation<Void> original) {
		if (AutoHud.targetHotbar && AutoHud.config.animationFade()) {
			// Don't render items if they're fully invisible anyway
			if (!Component.Hotbar.fullyHidden() || AutoHud.config.getHotbarItemsMaximumFade() > 0.0f) {
				// We need to reset the renderer because otherwise the first item gets drawn with double alpha
				AutoHudRenderer.postInjectFade();
				// Setup custom framebuffer
				CustomFramebufferRenderer.init();
				// Have the original call draw onto the custom framebuffer
				original.call(instance, context, x, y, tickDelta, player, stack, seed);
				// Render the contents of the custom framebuffer as a texture with transparency onto the main framebuffer
				AutoHudRenderer.preInjectFade(context, Component.Hotbar, AutoHud.config.getHotbarItemsMaximumFade());
				CustomFramebufferRenderer.draw(context);
				AutoHudRenderer.postInjectFade(context);
			}
		} else {
			original.call(instance, context, x, y, tickDelta, player, stack, seed);
		}
	}

	// Scoreboard
	@Inject(method = "renderScoreboardSidebar", at=@At("HEAD"))
	private void autoHud$preScoreboardSidebar(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
		if (AutoHud.targetScoreboard) {
			AutoHudRenderer.preInject(context, Component.Scoreboard);
		}
	}
	@Inject(method = "renderScoreboardSidebar", at=@At("RETURN"))
	private void autoHud$postScoreboardSidebar(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
		if (AutoHud.targetScoreboard) {
			AutoHudRenderer.postInject(context);
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
			AutoHudRenderer.preInject(context, Component.MountJumpBar);
		}
	}
	@Inject(method = "renderMountJumpBar", at=@At("RETURN"))
	private void autoHud$postMountJumpBar(JumpingMount mount, DrawContext context, int x, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.postInject(context);
		}
	}

	// Experience bar
	@Inject(method = "renderExperienceBar", at=@At("HEAD"))
	private void autoHud$preExperienceBar(DrawContext context, int x, CallbackInfo ci) {
		if (AutoHud.targetExperienceBar) {
			AutoHudRenderer.preInject(context, Component.ExperienceBar);
		}
	}
	@Inject(method = "renderExperienceBar", at=@At("RETURN"))
	private void autoHud$postExperienceBar(DrawContext context, int x, CallbackInfo ci) {
		if (AutoHud.targetExperienceBar) {
			AutoHudRenderer.postInject(context);
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
