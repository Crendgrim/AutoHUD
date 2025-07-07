package mod.crend.autohud.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Hud;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
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

@Mixin(value = InGameHud.class, priority = 800)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
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
					from = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;blendFuncSeparate(Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;)V"),
					to = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getAttackIndicator()Lnet/minecraft/client/option/SimpleOption;")
			),
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"
			)
	)
	private void autoHud$renderCrosshair(
			DrawContext context,
			Identifier texture,
			int x, int y,
			int width, int height,
			Operation<Void> original
	) {
		ComponentRenderer.CROSSHAIR.wrap(context, () -> original.call(
				context,
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
					target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"
			)
	)
	private void autoHud$statusEffectBackground(
			DrawContext context,
			Identifier texture,
			int x, int y,
			int width, int height,
			Operation<Void> original,
			@Local StatusEffectInstance statusEffectInstance
	) {
		ComponentRenderer.getForStatusEffect(statusEffectInstance).wrap(context, () -> original.call(
				context,
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
			method = "lambda$renderEffects$10",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;drawSprite(IIIIILnet/minecraft/client/texture/Sprite;)V"
			),
			require = 0
	)
	private static void autoHud$preSprite(
			DrawContext context,
			int x, int y, int z,
			int width, int height,
			Sprite sprite,
			Operation<Void> original
	) {
		ComponentRenderer.getForStatusEffect(sprite).wrap(context,
				() -> original.call(context, x, y, z, width, height, sprite)
		);
	}

	@WrapOperation(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target="Lnet/minecraft/entity/effect/StatusEffectInstance;shouldShowIcon()Z"))
	private boolean autoHud$shouldShowIconProxy(StatusEffectInstance instance, Operation<Boolean> original) {
		return (original.call(instance) || AutoHud.config.showHiddenStatusEffects()) && Hud.shouldShowIcon(instance);
	}

}
