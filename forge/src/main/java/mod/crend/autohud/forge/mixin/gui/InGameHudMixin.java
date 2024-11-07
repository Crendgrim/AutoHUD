package mod.crend.autohud.forge.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Components;
import mod.crend.autohud.component.Hud;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.autohud.render.RenderWrapper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.texture.Sprite;
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

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(InGameHud.class)
public class InGameHudMixin {
	@Inject(method="render", at=@At("HEAD"))
	private void autoHud$preRender(DrawContext context, /*? if <1.20.5 {*/float/*?} else {*//*RenderTickCounter*//*?}*/ tickDelta, CallbackInfo ci) {
		AutoHudRenderer.startRender(context, tickDelta);
	}
	@Inject(method="render", at=@At("RETURN"))
	private void autoHud$postRender(DrawContext context, /*? if <1.20.5 {*/float/*?} else {*//*RenderTickCounter*//*?}*/ tickDelta, CallbackInfo ci) {
		AutoHudRenderer.endRender();
	}

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
			AutoHudRenderer.preInjectFade(Components.Hotbar, context);
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
			AutoHudRenderer.preInjectFade(Components.Tooltip, context);
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
		RenderWrapper.HOTBAR_ITEMS.wrap(context, () -> original.call(instance, context, x, y, tickCounter, player, stack, seed));
	}

	// Scoreboard
	@Inject(method = "renderScoreboardSidebar", at=@At("HEAD"))
	private void autoHud$preScoreboardSidebar(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
		if (AutoHud.targetScoreboard) {
			AutoHudRenderer.preInjectFade(Components.Scoreboard, context);
		}
	}

	// Mount
	@Inject(method = "renderMountJumpBar", at=@At("HEAD"))
	private void autoHud$preMountJumpBar(JumpingMount mount, DrawContext context, int x, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.preInjectFade(Components.MountJumpBar, context);
		}
	}

	// Experience bar
	@Inject(method = "renderExperienceBar", at=@At("HEAD"))
	private void autoHud$preExperienceBar(DrawContext context, int x, CallbackInfo ci) {
		if (AutoHud.targetExperienceBar) {
			AutoHudRenderer.preInjectFade(Components.ExperienceBar, context);
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
		RenderWrapper.CROSSHAIR.wrap(context, () -> original.call(context, texture, x, y, /*? if <1.21 {*/u, v,/*?}*/ width, height));
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
		RenderWrapper.STATUS_EFFECT.wrap(context, statusEffectInstance, () ->
			original.call(context, texture, x, y, /*? if <1.21 {*/u, v,/*?}*/ width, height)
		);
	}

	@WrapOperation(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/extensions/common/IClientMobEffectExtensions;renderGuiIcon(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/client/gui/hud/InGameHud;Lnet/minecraft/client/gui/DrawContext;IIFF)Z"))
	private boolean autoHud$postEffect(IClientMobEffectExtensions instance, StatusEffectInstance statusEffectInstance, InGameHud gui, DrawContext context, int x, int y, float z, float alpha, Operation<Boolean> original) {
		AtomicBoolean result = new AtomicBoolean();
		RenderWrapper.STATUS_EFFECT.wrap(context, statusEffectInstance, () ->
			result.set(original.call(instance, statusEffectInstance, gui, context, x, y, z, alpha))
		);
		return result.get();
	}

	@WrapOperation(method = {"m_279741_", "method_18620"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawSprite(IIIIILnet/minecraft/client/texture/Sprite;)V"), require = 0)
	private static void autoHud$wrapSprite(DrawContext context, int x, int y, int z, int width, int height, Sprite sprite, Operation<Void> original) {
		RenderWrapper.STATUS_EFFECT.wrap(context, sprite, () -> original.call(context, x, y, z, width, height, sprite));
	}

	@Redirect(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target="Lnet/minecraft/entity/effect/StatusEffectInstance;shouldShowIcon()Z"))
	private boolean autoHud$shouldShowIconProxy(StatusEffectInstance instance) {
		return Hud.shouldShowIcon(instance);
	}
}
