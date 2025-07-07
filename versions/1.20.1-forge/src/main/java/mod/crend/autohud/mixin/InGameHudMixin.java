package mod.crend.autohud.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Hud;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.autohud.render.ComponentRenderer;
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

@Mixin(value = InGameHud.class, priority = 800)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public abstract class InGameHudMixin {

    @Inject(method="render", at=@At("HEAD"))
    private void autoHud$preRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        AutoHudRenderer.startRender(context, tickDelta);
    }
    @Inject(method="render", at=@At("RETURN"))
    private void autoHud$postRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        AutoHudRenderer.endRender();
    }

	// Hotbar
	@Inject(method = "renderHotbar", at = @At("HEAD"))
	private void autoHud$preHotbar(
			float tickDelta,
			DrawContext context,
			CallbackInfo ci
	) {
		ComponentRenderer.HOTBAR.beginFade(context);
	}

	@Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;getMatrices()Lnet/minecraft/client/util/math/MatrixStack;", ordinal = 0))
	private void autoHud$hotbarTransparency(
			float tickDelta,
			DrawContext context,
			CallbackInfo ci
	) {
		AutoHudRenderer.injectTransparency();
	}

	// Hotbar items
	@WrapOperation(
			method = "renderHotbar",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V"
			)
	)
	private void autoHud$transparentHotbarItems(
			InGameHud instance,
			DrawContext context,
			int x,
			int y,
			float tickDelta,
			PlayerEntity player,
			ItemStack stack,
			int seed,
			Operation<Void> original
	) {
		ComponentRenderer.HOTBAR_ITEMS.wrap(context, () -> original.call(instance, context, x, y, tickDelta, player, stack, seed));
	}

	// Tooltip
	@Inject(method = "renderHeldItemTooltip", at = @At("HEAD"))
	private void autoHud$preTooltip(DrawContext context, CallbackInfo ci) {
		ComponentRenderer.TOOLTIP.beginFade(context);
	}


	// Experience
	@Inject(method = "renderExperienceBar", at=@At("HEAD"))
	private void autoHud$preExperienceBar(DrawContext context, int x, CallbackInfo ci) {
		ComponentRenderer.EXPERIENCE_BAR_FORGE.beginFade(context);
	}
	@Inject(
			method = "renderExperienceBar",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", ordinal = 1)
	)
	private void autoHud$experienceText(DrawContext context, int x, CallbackInfo ci) {
		ComponentRenderer.EXPERIENCE_LEVEL_FORGE.beginRender(context);
	}
	@Inject(
			method = "renderExperienceBar",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V", ordinal = 1)
	)
	private void autoHud$postExperienceText(DrawContext context, int x, CallbackInfo ci) {
		ComponentRenderer.EXPERIENCE_LEVEL_FORGE.endRender(context);
	}


	// Status Bars
	@Inject(method = "renderMountJumpBar", at=@At("HEAD"))
	private void autoHud$preMountJumpBar(JumpingMount mount, DrawContext context, int x, CallbackInfo ci) {
		ComponentRenderer.MOUNT_JUMP_BAR.beginFade(context);
	}

	// Scoreboard
	@Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at=@At("HEAD"))
	private void autoHud$preScoreboardSidebar(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
		ComponentRenderer.SCOREBOARD.beginFade(context);
	}

	// Crosshair
	@WrapOperation(method = "renderCrosshair",
			slice = @Slice(
					from = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;blendFuncSeparate(Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;)V"),
					to = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getAttackIndicator()Lnet/minecraft/client/option/SimpleOption;")
			),
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"
			)
	)
	private void autoHud$renderCrosshair(
			DrawContext context,
			Identifier texture,
			int x, int y,
			int u, int v,
			int width, int height,
			Operation<Void> original
	) {
		ComponentRenderer.CROSSHAIR.wrap(context, () -> {
			original.call(context,
					texture, x, y,
					u, v,
					width, height
			);
		}, () -> {
			original.call(context,
					texture, x, y,
					u, v,
					width, height
			);
		});
	}

	// Status Effects
	@WrapOperation(
			method = "renderStatusEffectOverlay",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;shouldShowIcon()Z")
	)
	private boolean autoHud$shouldShowIcon(StatusEffectInstance instance, Operation<Boolean> original, DrawContext context) {
		return (original.call(instance) || AutoHud.config.showHiddenStatusEffects()) && Hud.shouldShowIcon(instance);
	}
	@WrapOperation(
			method = "renderStatusEffectOverlay",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"
			)
	)
	private void autoHud$wrapStatusEffect(
			DrawContext context,
			Identifier texture,
			int x, int y,
			int u, int v,
			int width, int height,
			Operation<Void> original,
			@Local StatusEffectInstance statusEffectInstance
	) {
		ComponentRenderer.getForStatusEffect(statusEffectInstance).wrap(context,
				() -> original.call(
						context,
						texture,
						x, y,
						u, v,
						width, height
				)
		);
	}
	@WrapOperation(
			require = 0,
			// Use
			method = {"m_279741_", "method_18620"},
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;drawSprite(IIIIILnet/minecraft/client/texture/Sprite;)V"
			)
	)
	private static void autoHud$wrapSprite(
			DrawContext context,
			int x, int y, int z,
			int width, int height,
			Sprite sprite,
			Operation<Void> original) {
		ComponentRenderer.getForStatusEffect(sprite).wrap(context,
				() -> original.call(context, x, y, z, width, height, sprite)
		);
	}
	@WrapOperation(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/extensions/common/IClientMobEffectExtensions;renderGuiIcon(Lnet/minecraft/entity/effect/StatusEffectInstance;Lnet/minecraft/client/gui/hud/InGameHud;Lnet/minecraft/client/gui/DrawContext;IIFF)Z"))
	private boolean autoHud$postEffect(IClientMobEffectExtensions instance, StatusEffectInstance statusEffectInstance, InGameHud gui, DrawContext context, int x, int y, float z, float alpha, Operation<Boolean> original) {
		AtomicBoolean result = new AtomicBoolean();
		ComponentRenderer.getForStatusEffect(statusEffectInstance).wrap(context, () ->
			result.set(original.call(instance, statusEffectInstance, gui, context, x, y, z, alpha))
		);
		return result.get();
	}

}