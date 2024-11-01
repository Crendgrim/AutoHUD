package mod.crend.autohud.neoforge.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Hud;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.autohud.render.RenderWrapper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
//? if >=1.21
/*import net.minecraft.client.render.RenderLayer;*/
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	// Hotbar items
	@WrapOperation(
			method = "renderHotbarVanilla",
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
			int x, int y,
			//? if <1.21 {
			float tickCounter,
			//?} else
			/*RenderTickCounter tickCounter,*/
			PlayerEntity player,
			ItemStack stack,
			int seed,
			Operation<Void> original
	) {
		RenderWrapper.HOTBAR_ITEMS.wrap(context, () -> original.call(instance, context, x, y, tickCounter, player, stack, seed));
	}


	// Crosshair
	@WrapOperation(method = "renderCrosshair",
			slice = @Slice(
					//? if <1.21.2
					from = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;blendFuncSeparate(Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;)V"),
					to = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getAttackIndicator()Lnet/minecraft/client/option/SimpleOption;")
			),
			at = @At(
					value = "INVOKE",
					//? if <1.21.2 {
					target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"
					//?} else
					/*target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V"*/
			)
	)
	private void autoHud$renderCrosshair(
			DrawContext context,
			//? if >=1.21.2
			/*Function<Identifier, RenderLayer> renderLayer,*/
			Identifier texture,
			int x, int y,
			int width, int height,
			Operation<Void> original
	) {
		RenderWrapper.CROSSHAIR.wrap(context, () -> original.call(
				context,
				//? if >=1.21.2
				/*renderLayer,*/
				texture,
				x, y,
				width, height
		));
	}


	// Status Effects
	@WrapOperation(
			method = "renderStatusEffectOverlay",
			at = @At(
					value = "INVOKE",
					//? if <1.21.2 {
					target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"
					//?} else
					/*target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V"*/
			)
	)
	private void autoHud$statusEffectBackground(
			DrawContext context,
			//? if >=1.21.2
			/*Function<Identifier, RenderLayer> renderLayer,*/
			Identifier texture,
			int x, int y,
			int width, int height,
			Operation<Void> original,
			@Local StatusEffectInstance statusEffectInstance
	) {
		RenderWrapper.STATUS_EFFECT.wrap(context, statusEffectInstance, () -> original.call(
				context,
				//? if >=1.21.2
				/*renderLayer,*/
				texture,
				x, y,
				width, height
		));
	}

	@WrapOperation(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/extensions/common/IClientMobEffectExtensions;renderGuiIcon(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/client/gui/hud/InGameHud;Lnet/minecraft/client/gui/DrawContext;IIFF)Z"))
	private boolean autoHud$postEffect(IClientMobEffectExtensions instance, StatusEffectInstance statusEffectInstance, InGameHud gui, DrawContext context, int x, int y, float z, float alpha, Operation<Boolean> original) {
		AtomicBoolean result = new AtomicBoolean(false);
		RenderWrapper.STATUS_EFFECT.wrap(context, statusEffectInstance, () ->
			result.set(original.call(instance, statusEffectInstance, gui, context, x, y, z, alpha))
		);
		return result.get();
	}

	@WrapOperation(
			//? if <1.21.2 {
			method = "lambda$renderEffects$10",
			//?} else
			/*method = "lambda$renderEffects$11",*/
			at = @At(
					value = "INVOKE",
					//? if <1.21.2 {
					target = "Lnet/minecraft/client/gui/DrawContext;drawSprite(IIIIILnet/minecraft/client/texture/Sprite;)V"
					//?} else
					/*target = "Lnet/minecraft/client/gui/DrawContext;drawSpriteStretched(Ljava/util/function/Function;Lnet/minecraft/client/texture/Sprite;IIIII)V"*/
			),
			require = 0
	)
	private static void autoHud$preSprite(
			DrawContext context,
			//? if >=1.21.2 {
			/*Function<Identifier, RenderLayer> renderLayer,
			Sprite sprite,
			*///?}
			int x, int y, int z,
			int width, int height,
			//? if <1.21.2
			Sprite sprite,
			Operation<Void> original
	) {
		RenderWrapper.STATUS_EFFECT.wrap(context, sprite,
				//? if <1.21.2 {
				() -> original.call(context, x, y, z, width, height, sprite)
				//?} else
				/*() -> original.call(context, renderLayer, sprite, x, y, z, width, height)*/
		);
	}

	@WrapOperation(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target="Lnet/minecraft/entity/effect/StatusEffectInstance;shouldShowIcon()Z"))
	private boolean autoHud$shouldShowIconProxy(StatusEffectInstance instance, Operation<Boolean> original) {
		return original.call(instance) && Hud.shouldShowIcon(instance);
	}

}
