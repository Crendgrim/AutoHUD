package mod.crend.autohud.neoforge.mixin.gui;

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
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	// Hotbar items
	@WrapOperation(
			method = "renderHotbarVanilla",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V"
			)
	)
	private void autoHud$transparentHotbarItems(InGameHud instance, DrawContext context, int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed, Operation<Void> original) {
		if (!AutoHudRenderer.shouldRenderHotbarItems()) return;
		if (AutoHud.targetHotbar && AutoHud.config.animationFade()) {
			// We need to reset the renderer because otherwise the first item gets drawn with double alpha
			AutoHudRenderer.postInjectFade();
			// Setup custom framebuffer
			CustomFramebufferRenderer.init();
			// Have the original call draw onto the custom framebuffer
			original.call(instance, context, x, y, tickDelta, player, stack, seed);
			// Render the contents of the custom framebuffer as a texture with transparency onto the main framebuffer
			AutoHudRenderer.preInjectFadeWithReverseTranslation(context, Component.Hotbar, AutoHud.config.getHotbarItemsMaximumFade());
			CustomFramebufferRenderer.draw(context);
			AutoHudRenderer.postInjectFadeWithReverseTranslation(context);
		} else {
			original.call(instance, context, x, y, tickDelta, player, stack, seed);
		}
	}


	// Crosshair
	@WrapOperation(method = "renderCrosshair",
			slice = @Slice(
					from = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;blendFuncSeparate(Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;)V"),
					to = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getAttackIndicator()Lnet/minecraft/client/option/SimpleOption;")
			),
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
	private void autoHud$renderCrosshair(DrawContext context, Identifier texture, int x, int y, int width, int height, Operation<Void> original) {
		if (AutoHudRenderer.shouldRenderCrosshair()) {
			AutoHudRenderer.preInjectCrosshair();
			original.call(context, texture, x, y, width, height);
			AutoHudRenderer.postInjectCrosshair(context);
		}
	}


	// Status Effects
	@WrapOperation(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
	private void autoHud$statusEffectBackground(DrawContext context, Identifier texture, int x, int y, int width, int height, Operation<Void> original, @Local StatusEffectInstance statusEffectInstance) {
		if (AutoHud.targetStatusEffects) {
			AutoHudRenderer.preInject(context, Component.get(statusEffectInstance.getEffectType()));
		}
		original.call(context, texture, x, y, width, height);
		if (AutoHud.targetStatusEffects) {
			AutoHudRenderer.postInject(context);
		}
	}

	@WrapOperation(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/extensions/common/IClientMobEffectExtensions;renderGuiIcon(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/client/gui/hud/InGameHud;Lnet/minecraft/client/gui/DrawContext;IIFF)Z"))
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

	@WrapOperation(method = {"lambda$renderEffects$10", "method_18620"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawSprite(IIIIILnet/minecraft/client/texture/Sprite;)V"), require = 0)
	private static void autoHud$preSprite(DrawContext context, int x, int y, int z, int width, int height, Sprite sprite, Operation<Void> original) {
		if (AutoHud.targetStatusEffects) {
			Component component = Component.findBySprite(sprite);
			if (component != null) {
				AutoHudRenderer.preInject(context, component);
			} else {
				context.getMatrices().push();
			}
		}
		original.call(context, x, y, z, width, height, sprite);
		if (AutoHud.targetStatusEffects) {
			AutoHudRenderer.postInject(context);
		}
	}

	@WrapOperation(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target="Lnet/minecraft/entity/effect/StatusEffectInstance;shouldShowIcon()Z"))
	private boolean autoHud$shouldShowIconProxy(StatusEffectInstance instance, Operation<Boolean> original) {
		return original.call(instance) && Hud.shouldShowIcon(instance);
	}

}
