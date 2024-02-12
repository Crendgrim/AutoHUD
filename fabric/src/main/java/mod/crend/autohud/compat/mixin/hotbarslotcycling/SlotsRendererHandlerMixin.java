package mod.crend.autohud.compat.mixin.hotbarslotcycling;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import fuzs.hotbarslotcycling.impl.client.handler.SlotsRendererHandler;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.compat.HotbarSlotCyclingCompat;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.autohud.render.CustomFramebufferRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SlotsRendererHandler.class)
public class SlotsRendererHandlerMixin {

	@ModifyExpressionValue(
			method = "onRenderGui",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
					ordinal = 0
			)
	)
	private static boolean autoHud$renderEmptySlots(boolean original, @Local(ordinal = 0) ItemStack forwardStack) {
		if (!AutoHud.targetHotbar) return original;
		HotbarSlotCyclingCompat.forwardStack = forwardStack;
		if (original) {
			HotbarSlotCyclingCompat.HotbarSlotCyclerComponent.forceHide();
		}
		return HotbarSlotCyclingCompat.HotbarSlotCyclerComponent.fullyHidden() && AutoHud.config.getHotbarItemsMaximumFade() == 0.0f;
	}
	@ModifyExpressionValue(
			method = "onRenderGui",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
					ordinal = 1
			)
	)
	private static boolean autoHud$renderEmptySlots2(boolean original) {
		return !AutoHud.targetHotbar && original;
	}

	@ModifyExpressionValue(
			method = "renderAdditionalSlots",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
					ordinal = 0
			)
	)
	private static boolean autoHud$renderEmptySlots3(boolean original) {
		return !AutoHud.targetHotbar && original;
	}

	@ModifyExpressionValue(
			method = "renderAdditionalSlots",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
					ordinal = 1
			)
	)
	private static boolean autoHud$renderEmptySlots4(boolean original) {
		return !AutoHud.targetHotbar && original;
	}

	@WrapOperation(
			method = "onRenderGui",
			at = @At(value = "INVOKE", target = "Lfuzs/hotbarslotcycling/impl/client/handler/SlotsRendererHandler;renderAdditionalSlots(Lnet/minecraft/client/gui/DrawContext;FIILnet/minecraft/client/font/TextRenderer;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V")
	)
	private static void autoHud$wrapSlotRender(DrawContext context, float partialTicks, int screenWidth, int screenHeight, TextRenderer font, PlayerEntity player, ItemStack backwardStack, ItemStack selectedStack, ItemStack forwardStack, Operation<Void> original) {
		if (AutoHud.targetHotbar) {
			AutoHudRenderer.preInject(context, HotbarSlotCyclingCompat.HotbarSlotCyclerComponent);
		}
		original.call(context, partialTicks, screenWidth, screenHeight, font, player, backwardStack, selectedStack, forwardStack);
		if (AutoHud.targetHotbar) {
			AutoHudRenderer.postInject(context);
		}
	}

	@WrapOperation(
			method = "renderAdditionalSlots",
			at = @At(value = "INVOKE", target = "Lfuzs/hotbarslotcycling/impl/client/handler/SlotsRendererHandler;renderSlotBackgrounds(Lnet/minecraft/client/gui/DrawContext;IIZZZ)V")
	)
	private static void autoHud$transparentBackground(DrawContext context, int posX, int posY, boolean renderForwardStack, boolean renderBackwardStack, boolean renderToRight, Operation<Void> original) {
		// For some reason, rendering the background texture as transparent does not work. Render them to a texture first instead.
		if (AutoHud.targetHotbar && AutoHud.config.animationFade()) {
			if (!HotbarSlotCyclingCompat.HotbarSlotCyclerComponent.fullyHidden()) {
				AutoHudRenderer.postInjectFade();
				CustomFramebufferRenderer.init();
				original.call(context, posX, posY, renderForwardStack, renderBackwardStack, renderToRight);
				AutoHudRenderer.preInjectFadeWithReverseTranslation(context, HotbarSlotCyclingCompat.HotbarSlotCyclerComponent, 0);
				CustomFramebufferRenderer.draw(context);
				AutoHudRenderer.postInjectFadeWithReverseTranslation(context);
			}
		} else {
			original.call(context, posX, posY, renderForwardStack, renderBackwardStack, renderToRight);
		}
	}

	@WrapOperation(
			method = "renderAdditionalSlots",
			at = @At(value = "INVOKE", target = "Lfuzs/hotbarslotcycling/impl/client/handler/SlotsRendererHandler;renderSlotItems(FIILnet/minecraft/client/font/TextRenderer;Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Z)V")
	)
	private static void autoHud$transparentItems(float partialTicks, int posX, int posY, TextRenderer font, DrawContext context, PlayerEntity player, ItemStack selectedStack, ItemStack forwardStack, ItemStack backwardStack, boolean renderToRight, Operation<Void> original) {
		if (AutoHud.targetHotbar && AutoHud.config.animationFade()) {
			// Don't render items if they're fully invisible anyway
			if (!HotbarSlotCyclingCompat.HotbarSlotCyclerComponent.fullyHidden() || AutoHud.config.getHotbarItemsMaximumFade() > 0.0f) {
				// We need to reset the renderer because otherwise the first item gets drawn with double alpha
				AutoHudRenderer.postInjectFade();
				// Setup custom framebuffer
				CustomFramebufferRenderer.init();
				// Have the original call draw onto the custom framebuffer
				original.call(partialTicks, posX, posY, font, context, player, selectedStack, forwardStack, backwardStack, renderToRight);
				// Render the contents of the custom framebuffer as a texture with transparency onto the main framebuffer
				AutoHudRenderer.preInjectFadeWithReverseTranslation(context, HotbarSlotCyclingCompat.HotbarSlotCyclerComponent, AutoHud.config.getHotbarItemsMaximumFade());
				CustomFramebufferRenderer.draw(context);
				AutoHudRenderer.postInjectFadeWithReverseTranslation(context);
			}
		} else {
			original.call(partialTicks, posX, posY, font, context, player, selectedStack, forwardStack, backwardStack, renderToRight);
		}
	}


}
