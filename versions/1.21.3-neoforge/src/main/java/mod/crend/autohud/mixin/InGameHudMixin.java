package mod.crend.autohud.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Hud;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderLayer;
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

@Mixin(value = InGameHud.class, priority = 800)
public class InGameHudMixin {

	// Hotbar items
	@WrapOperation(
			method = "renderHotbarVanilla",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V"
			)
	)
	private void autoHud$transparentHotbarItems(
			InGameHud instance,
			DrawContext context,
			int x, int y,
			RenderTickCounter tickCounter,
			PlayerEntity player,
			ItemStack stack,
			int seed,
			Operation<Void> original
	) {
		ComponentRenderer.HOTBAR_ITEMS.wrap(context, () -> original.call(instance, context, x, y, tickCounter, player, stack, seed));
	}


	// Crosshair
	@WrapOperation(method = "renderCrosshair",
			slice = @Slice(
					to = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getAttackIndicator()Lnet/minecraft/client/option/SimpleOption;")
			),
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V"
			)
	)
	private void autoHud$renderCrosshair(
			DrawContext context,
			Function<Identifier, RenderLayer> renderLayer,
			Identifier texture,
			int x, int y,
			int width, int height,
			Operation<Void> original
	) {
		ComponentRenderer.CROSSHAIR.wrap(context, () -> original.call(
				context,
				renderLayer,
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
					target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V"
			)
	)
	private void autoHud$statusEffectBackground(
			DrawContext context,
			Function<Identifier, RenderLayer> renderLayer,
			Identifier texture,
			int x, int y,
			int width, int height,
			Operation<Void> original,
			@Local StatusEffectInstance statusEffectInstance
	) {
		ComponentRenderer.getForStatusEffect(statusEffectInstance).wrap(context, () -> original.call(
				context,
				renderLayer,
				texture,
				x, y,
				width, height
		));
	}

	@WrapOperation(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/extensions/common/IClientMobEffectExtensions;renderGuiIcon(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/client/gui/hud/InGameHud;Lnet/minecraft/client/gui/DrawContext;IIFF)Z"))
	private boolean autoHud$postEffect(IClientMobEffectExtensions instance, StatusEffectInstance statusEffectInstance, InGameHud gui, DrawContext context, int x, int y, float z, float alpha, Operation<Boolean> original) {
		AtomicBoolean result = new AtomicBoolean(false);
		ComponentRenderer.getForStatusEffect(statusEffectInstance).wrap(context, () ->
			result.set(original.call(instance, statusEffectInstance, gui, context, x, y, z, alpha))
		);
		return result.get();
	}

	@WrapOperation(
			method = "lambda$renderEffects$11",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;drawSpriteStretched(Ljava/util/function/Function;Lnet/minecraft/client/texture/Sprite;IIIII)V"
			),
			require = 0
	)
	private static void autoHud$preSprite(
			DrawContext context,
			Function<Identifier, RenderLayer> renderLayer,
			Sprite sprite,
			int x, int y, int z,
			int width, int height,
			Operation<Void> original
	) {
		ComponentRenderer.getForStatusEffect(sprite).wrap(context,
				() -> original.call(context, renderLayer, sprite, x, y, z, width, height)
		);
	}

	@WrapOperation(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target="Lnet/minecraft/entity/effect/StatusEffectInstance;shouldShowIcon()Z"))
	private boolean autoHud$shouldShowIconProxy(StatusEffectInstance instance, Operation<Boolean> original) {
		return (original.call(instance) || AutoHud.config.showHiddenStatusEffects()) && Hud.shouldShowIcon(instance);
	}

}
