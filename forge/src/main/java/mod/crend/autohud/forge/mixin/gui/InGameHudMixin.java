package mod.crend.autohud.forge.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Hud;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.libbamboo.render.CustomFramebufferRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.Identifier;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	//? if >=1.21 {
	/*@Inject(method="render", at=@At("HEAD"))
	private void autoHud$preRender(DrawContext context, RenderTickCounter tickDelta, CallbackInfo ci) {
		AutoHudRenderer.startRender(context, tickDelta);
	}
	@Inject(method="render", at=@At("RETURN"))
	private void autoHud$postRender(DrawContext context, RenderTickCounter tickDelta, CallbackInfo ci) {
		AutoHudRenderer.endRender();
	}
	*///?}

	// Hotbar
	@Inject(method = "renderHotbar", at = @At("HEAD"))
	private void autoHud$preHotbar(
			//? if <1.21
			float tickDelta,
			DrawContext context,
			//? if >=1.21
			/*RenderTickCounter tickCounter,*/
			CallbackInfo ci
	) {
		if (AutoHud.targetHotbar) {
			AutoHudRenderer.preInjectFade(Component.Hotbar);
		}
	}

	@Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;getMatrices()Lnet/minecraft/client/util/math/MatrixStack;", ordinal = 0))
	private void autoHud$hotbarTransparency(
			//? if <1.21
			float tickDelta,
			DrawContext context,
			//? if >=1.21
			/*RenderTickCounter tickCounter,*/
			CallbackInfo ci
	) {
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
	@WrapOperation(
			method = "renderHotbar",
			at = @At(
					value = "INVOKE",
					//? if <1.21 {
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V"
					//?} else
					/*target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V"*/
			)
	)
	private void autoHud$transparentHotbarItems(
			InGameHud instance,
			DrawContext context,
			int x,
			int y,
			/*? if <1.21 {*/float/*?} else {*//*RenderTickCounter*//*?}*/ tickCounter,
			PlayerEntity player,
			ItemStack stack,
			int seed,
			Operation<Void> original
	) {
		if (!AutoHudRenderer.shouldRenderHotbarItems()) return;
		if (AutoHud.targetHotbar && AutoHud.config.animationFade()) {
			// We need to reset the renderer because otherwise the first item gets drawn with double alpha
			AutoHudRenderer.postInjectFade();
			// Setup custom framebuffer
			CustomFramebufferRenderer.init();
			// Have the original call draw onto the custom framebuffer
			original.call(instance, context, x, y, tickCounter, player, stack, seed);
			// Render the contents of the custom framebuffer as a texture with transparency onto the main framebuffer
			AutoHudRenderer.preInjectFadeWithReverseTranslation(context, Component.Hotbar, AutoHud.config.getHotbarItemsMaximumFade());
			CustomFramebufferRenderer.draw(context);
			AutoHudRenderer.postInjectFadeWithReverseTranslation(context);
		} else {
			original.call(instance, context, x, y, tickCounter, player, stack, seed);
		}
	}

	// Scoreboard
	@Inject(method = "renderScoreboardSidebar", at=@At("HEAD"))
	private void autoHud$preScoreboardSidebar(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
		if (AutoHud.targetScoreboard) {
			AutoHudRenderer.preInjectFade(Component.Scoreboard);
		}
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
	@Inject(
			//? if <1.21 {
			method = "render",
			//?} else
			/*method = "renderExperienceLevel",*/
			at=@At("HEAD")
	)
	private void autoHud$experienceText(DrawContext context, /*? if <1.21 {*/float/*?} else {*//*RenderTickCounter*//*?}*/ tickCounter, CallbackInfo ci) {
		if (AutoHud.targetExperienceBar) {
			AutoHudRenderer.moveExperienceText(context);
		}
	}
	@Inject(method = "renderExperienceBar", at=@At("RETURN"))
	private void autoHud$postExperienceText(DrawContext context, int x, CallbackInfo ci) {
		if (AutoHud.targetExperienceBar) {
			AutoHudRenderer.moveBackExperienceText(context);
		}
	}

	// Crosshair
	@WrapOperation(method = "renderCrosshair",
			slice = @Slice(
					from = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;blendFuncSeparate(Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;)V"),
					to = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getAttackIndicator()Lnet/minecraft/client/option/SimpleOption;")
			),
			at = @At(
					value = "INVOKE",
					//? if <1.21 {
					target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"
					//?} else
					/*target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"*/
			)
	)
	private void autoHud$renderCrosshair(DrawContext context, Identifier texture, int x, int y, /*? if <1.21 {*/int u, int v,/*?}*/ int width, int height, Operation<Void> original) {
		if (AutoHudRenderer.shouldRenderCrosshair()) {
			AutoHudRenderer.preInjectCrosshair();
			original.call(context, texture, x, y, /*? if <1.21 {*/u, v,/*?}*/ width, height);
			AutoHudRenderer.postInjectCrosshair(context);
		}
	}

	// Status Effects
	@WrapOperation(
			method = "renderStatusEffectOverlay",
			at = @At(
					value = "INVOKE",
					//? if <1.21 {
					target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"
					//?} else
					/*target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"*/
			)
	)
	private void autoHud$statusEffectBackground(DrawContext context, Identifier texture, int x, int y, /*? if <1.21 {*/int u, int v,/*?}*/ int width, int height, Operation<Void> original, @Local StatusEffectInstance statusEffectInstance) {
		if (AutoHud.targetStatusEffects) {
			AutoHudRenderer.preInject(context, Component.get(statusEffectInstance.getEffectType()));
		}
		original.call(context, texture, x, y, /*? if <1.21 {*/u, v,/*?}*/ width, height);
		if (AutoHud.targetStatusEffects) {
			AutoHudRenderer.postInject(context);
		}
	}

	@WrapOperation(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/extensions/common/IClientMobEffectExtensions;renderGuiIcon(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/client/gui/hud/InGameHud;Lnet/minecraft/client/gui/DrawContext;IIFF)Z"))
	private boolean autoHud$postEffect(IClientMobEffectExtensions instance, StatusEffectInstance statusEffectInstance, InGameHud gui, DrawContext context, int x, int y, float z, float alpha, Operation<Boolean> original) {
		if (AutoHud.targetStatusEffects) {
			AutoHudRenderer.preInject(context, Component.get(statusEffectInstance.getEffectType()));
		}
		boolean result = original.call(instance, statusEffectInstance, gui, context, x, y, z, alpha);
		if (AutoHud.targetStatusEffects) {
			AutoHudRenderer.postInject(context);
		}
		return result;
	}

	@Inject(method = "method_18620", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawSprite(IIIIILnet/minecraft/client/texture/Sprite;)V"), require = 0)
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
	@Inject(method = "method_18620", at = @At(value = "RETURN"), require = 0)
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
