package mod.crend.autohud.forge.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
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
//? if >1.20.1 {
/*import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Components;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import java.util.function.Function;
*///?}
//? if >=1.20.5
/*import net.minecraft.client.gui.LayeredDrawer;*/

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(value = InGameHud.class, priority = 800)
public abstract class InGameHudMixin {

	//? if <1.20.5 {
    @Inject(method="render", at=@At("HEAD"))
    private void autoHud$preRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        AutoHudRenderer.startRender(context, tickDelta);
    }
    @Inject(method="render", at=@At("RETURN"))
    private void autoHud$postRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        AutoHudRenderer.endRender();
    }
    //?} else {
	/*@WrapOperation(
			method = "render",
			at = @At(
					value = "INVOKE",
					//? if <1.21 {
					target = "Lnet/minecraft/client/gui/LayeredDrawer;render(Lnet/minecraft/client/gui/DrawContext;F)V"
					//?} else
					/^target = "Lnet/minecraft/client/gui/LayeredDrawer;render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"^/
			)
	)
	private void autoHud$render(
			LayeredDrawer instance,
			DrawContext context,
			//? if <1.21 {
			float tickCounter,
			 //?} else
			/^RenderTickCounter tickCounter,^/
			Operation<Void> original
	) {
		AutoHudRenderer.startRender(context, tickCounter);
		original.call(instance, context, tickCounter);
		AutoHudRenderer.renderChatMessageIndicator(context, tickCounter);
		AutoHudRenderer.endRender();
	}
	*///?}

	// Hotbar
	
	//? if <1.20.5 {
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
	}//?} else {
	/*@WrapOperation(
			method = "renderMainHud",
			at = @At(
					value = "INVOKE",
					//? if <1.21 {
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(Lnet/minecraft/client/gui/DrawContext;F)V"
					//?} else
					/^target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"^/
			)
	)
	private void autoHud$wrapHotbar(
			InGameHud instance,
			//? if <1.21 {
			DrawContext context, float tickDelta,
			 //?} else
			/^DrawContext context, RenderTickCounter tickDelta,^/
			Operation<Void> original
	) {
		ComponentRenderer.HOTBAR.wrap(context, () ->
						//? if <1.20.6 {
						original.call(instance, tickDelta, context)
						 //?} else
						/^original.call(instance, context, tickDelta)^/
		);
	}
	*///?}

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
			//? if <1.21 {
			float tickDelta,
			 //?} else
			/*RenderTickCounter tickDelta,*/
			PlayerEntity player,
			ItemStack stack,
			int seed,
			Operation<Void> original
	) {
		ComponentRenderer.HOTBAR_ITEMS.wrap(context, () -> original.call(instance, context, x, y, tickDelta, player, stack, seed));
	}

	// Tooltip
	
	//? if <1.20.5 {
	@Inject(method = "renderHeldItemTooltip", at = @At("HEAD"))
	private void autoHud$preTooltip(DrawContext context, CallbackInfo ci) {
		ComponentRenderer.TOOLTIP.beginFade(context);
	}
	//?} else {
	/*@WrapOperation(
			method = "renderMainHud",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHeldItemTooltip(Lnet/minecraft/client/gui/DrawContext;)V"
			)
	)
	private void autoHud$wrapTooltip(InGameHud instance, DrawContext context, Operation<Void> original) {
		ComponentRenderer.TOOLTIP.wrap(context, () -> original.call(instance, context));
	}
	*///?}

	// Experience
	//? if <1.20.5 {
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
	//?} else {

	/*// Experience Bar
	@WrapOperation(
			method = "renderMainHud",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderExperienceBar(Lnet/minecraft/client/gui/DrawContext;I)V"
			)
	)
	private void autoHud$wrapExperienceBar(InGameHud instance, DrawContext context, int x, Operation<Void> original) {
		ComponentRenderer.EXPERIENCE_BAR.wrap(context, () -> original.call(instance, context, x));
	}

	@ModifyReturnValue(
			method = "shouldRenderExperience",
			at = @At("RETURN")
	)
	private boolean autoHud$shouldRenderExperienceLevel(boolean original) {
		if (AutoHud.targetExperienceBar) {
			if (AutoHud.config.revealExperienceTextWithHotbar() && Components.Hotbar.shouldRender()) {
				return true;
			}
			return Components.ExperienceBar.shouldRender();
		}
		return original;
	}

	@Inject(
			method = "renderExperienceLevel",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I",
					ordinal = 0
			)
	)
	private void autoHud$experienceText(
			DrawContext context,
			//? if <1.21 {
			float x,
			 //?} else
			/^RenderTickCounter x,^/
			CallbackInfo ci
	) {
		ComponentRenderer.EXPERIENCE_LEVEL.beginRender(context);
	}
	@Inject(
			method = "renderExperienceLevel",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/util/profiler/Profiler;pop()V"
			)
	)
	private void autoHud$postExperienceText(
			DrawContext context,
			//? if <1.21 {
			float x,
			 //?} else
			/^RenderTickCounter x,^/
			CallbackInfo ci
	) {
		ComponentRenderer.EXPERIENCE_LEVEL.endRender(context);
	}

	@ModifyArg(
			method = "renderExperienceLevel",
			at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"),
			index = 4
	)
	private int autoHud$experienceText(int color) {
		if (AutoHudRenderer.inRender) {
			return AutoHudRenderer.modifyArgb(color);
		}
		return color;
	}

	*///?}

	// Status Bars

	//? if <1.20.5 {
	@Inject(method = "renderMountJumpBar", at=@At("HEAD"))
	private void autoHud$preMountJumpBar(JumpingMount mount, DrawContext context, int x, CallbackInfo ci) {
		ComponentRenderer.MOUNT_JUMP_BAR.beginFade(context);
	}
	//?} else {
	/*@WrapOperation(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderArmor(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIII)V"))
	private void autoHud$armorBar(DrawContext context, PlayerEntity player, int i, int j, int k, int x, Operation<Void> original) {
		ComponentRenderer.ARMOR.wrap(context, () -> original.call(context, player, i, j, k, x));
	}

	@WrapOperation(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHealthBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V"))
	private void autoHud$healthBar(InGameHud instance, DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, Operation<Void> original) {
		ComponentRenderer.HEALTH.wrap(context, () -> original.call(instance, context, player, x, y, lines, regeneratingHeartIndex, maxHealth, lastHealth, health, absorption, blinking));
	}

	@WrapOperation(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderFood(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;II)V"))
	private void autoHud$foodBar(InGameHud instance, DrawContext context, PlayerEntity player, int top, int right, Operation<Void> original) {
		ComponentRenderer.HUNGER.wrap(context, () -> original.call(instance, context, player, top, right));
	}

	//? if <1.21.2 {
	@Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getMaxAir()I", shift = At.Shift.AFTER))
	private void autoHud$preAirBar(DrawContext context, CallbackInfo ci) {
		ComponentRenderer.AIR.beginRender(context);
	}
	@Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V"))
	private void autoHud$postAirBar(DrawContext context, CallbackInfo ci) {
		ComponentRenderer.AIR.endRender(context);
	}
	//?} else {
    /^@WrapOperation(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderAirBubbles(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;III)V"))
    private void autoHud$airBar(InGameHud instance, DrawContext context, PlayerEntity player, int heartCount, int top, int left, Operation<Void> original) {
        ComponentRenderer.AIR.wrap(context, () -> original.call(instance, context, player, heartCount, top, left));
    }^///?}

	// Mount Health
	@WrapOperation(
			method = "renderMainHud",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountHealth(Lnet/minecraft/client/gui/DrawContext;)V"
			)
	)
	private void autoHud$wrapMountHealth(InGameHud instance, DrawContext context, Operation<Void> original) {
		ComponentRenderer.MOUNT_HEALTH.wrap(context, () -> original.call(instance, context));
	}

	// Mount Jump Bar
	@WrapOperation(
			method = "renderMainHud",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountJumpBar(Lnet/minecraft/entity/JumpingMount;Lnet/minecraft/client/gui/DrawContext;I)V"
			)
	)
	private void autoHud$wrapMountJumpBar(InGameHud instance, JumpingMount mount, DrawContext context, int x, Operation<Void> original) {
		ComponentRenderer.MOUNT_JUMP_BAR.wrap(context, () -> original.call(instance, mount, context, x));
	}
	*///?}

	// Scoreboard

	//? if <1.20.5 {
	
	@Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at=@At("HEAD"))
	private void autoHud$preScoreboardSidebar(DrawContext context, ScoreboardObjective objective, CallbackInfo ci) {
		ComponentRenderer.SCOREBOARD.beginFade(context);
	}
	//?} else {
	/*@WrapOperation(
			//? if <1.21 {
			method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;F)V",
			//?} else
			/^method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V",^/
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"
			)
	)
	private void autoHud$wrapScoreboardSidebar(InGameHud instance, DrawContext context, ScoreboardObjective objective, Operation<Void> original) {
		ComponentRenderer.SCOREBOARD.wrap(context, () -> original.call(instance, context, objective));
	}
	@ModifyArg(
			//? if <1.21.2 {
			method = "method_55440",
			//?} else {
			/^method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
			^///?}
			at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I"),
			index = 4
	)
	private int autoHud$scoreboardSidebarText(int color) {
		if (AutoHudRenderer.inRender) {
			return AutoHudRenderer.getArgb() | 0xFFFFFF;
		}
		return color;
	}
	@ModifyArg(
			//? if <1.21.2 {
			method = "method_55440",
			//?} else {
			/^method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
			^///?}
			at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"),
			index=4
	)
	private int autoHud$scoreboardSidebarFill(int color) {
		if (AutoHudRenderer.inRender) {
			return AutoHudRenderer.modifyArgb(color);
		}
		return color;
	}
	*///?}

	// Crosshair

	@WrapOperation(method = "renderCrosshair",
			slice = @Slice(
					//? if <1.21.2
					from = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;blendFuncSeparate(Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;)V"),
					to = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getAttackIndicator()Lnet/minecraft/client/option/SimpleOption;")
			),
			at = @At(
					value = "INVOKE",
					//? if <1.20.3 {
					target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"
					//?} else if <1.21.2 {
					/*target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"
					*///?} else
					/*target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V"*/
			)
	)
	private void autoHud$renderCrosshair(
			DrawContext context,
			//? if >=1.21.2
			/*Function<Identifier, RenderLayer> renderLayers,*/
			Identifier texture,
			int x, int y,
			//? if <1.20.3
			int u, int v,
			int width, int height,
			Operation<Void> original
	) {
		ComponentRenderer.CROSSHAIR.wrap(context, () -> {
			//? if >=1.21.2
			/*Function<Identifier, RenderLayer> renderLayerFunction = RenderLayer::getGuiTextured;*/
			original.call(context,
					//? if >=1.21.2
					/*renderLayerFunction,*/
					texture, x, y,
					//? if <1.20.3
					u, v,
					width, height
			);
		}, () -> {
			original.call(context,
					//? if >=1.21.2
					/*renderLayers,*/
					texture, x, y,
					/*u, v,*/
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
		return original.call(instance) && Hud.shouldShowIcon(instance);
	}
	@WrapOperation(
			method = "renderStatusEffectOverlay",
			at = @At(
					value = "INVOKE",
					//? if <1.20.3 {
					target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"
					//?} else if <1.21.2 {
					/*target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"
					*///?} else
					/*target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V"*/
			)
	)
	private void autoHud$wrapStatusEffect(
			DrawContext context,
			//? if >=1.21.2
			/*Function<Identifier, RenderLayer> renderLayer,*/
			Identifier texture,
			int x, int y,
			//? if <1.20.3
			int u, int v,
			int width, int height,
			Operation<Void> original,
			@Local StatusEffectInstance statusEffectInstance
	) {
		ComponentRenderer.getForStatusEffect(statusEffectInstance).wrap(context,
				() -> original.call(
						context,
						//? if >=1.21.2
						/*renderLayer,*/
						texture,
						x, y,
						//? if <1.20.3
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
					//? if <1.21.2 {
					target = "Lnet/minecraft/client/gui/DrawContext;drawSprite(IIIIILnet/minecraft/client/texture/Sprite;)V"
					//?} else
					/*target = "Lnet/minecraft/client/gui/DrawContext;drawSpriteStretched(Ljava/util/function/Function;Lnet/minecraft/client/texture/Sprite;IIIII)V"*/
			)
	)
	private static void autoHud$wrapSprite(
			DrawContext context,
			//? if >=1.21.2 {
            /*Function<Identifier, RenderLayer> renderLayer,
            Sprite sprite,
            *///?}
			int x, int y, int z,
			int width, int height,
			//? if <1.21.2
			Sprite sprite,
			Operation<Void> original) {
		ComponentRenderer.getForStatusEffect(sprite).wrap(context,
				//? if <1.21.2 {
				() -> original.call(context, x, y, z, width, height, sprite)
				//?} else
				/*() -> original.call(context, renderLayer, sprite, x, y, z, width, height)*/
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

	// Chat
	//? if >=1.20.5 {
	/*@WrapOperation(
			method = "renderChat",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraftforge/client/ForgeHooksClient;onCustomizeChatEvent(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/gui/hud/ChatHud;Lnet/minecraft/client/util/Window;III)V"
			)
	)
	private void autoHud$wrapChat(DrawContext context, ChatHud chatHud, Window window, int i, int j, int ticks, Operation<Void> original) {
		ComponentRenderer.CHAT.wrap(context, () -> original.call(context, chatHud, window, i, j, ticks));
	}
	*///?}

	// Boss Bar
	//? if >=1.20.5 {
	/*@WrapOperation(
			method = "method_55808",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/BossBarHud;render(Lnet/minecraft/client/gui/DrawContext;)V")
	)
	private void autoHud$wrapBossBar(BossBarHud instance, DrawContext context, Operation<Void> original) {
		ComponentRenderer.BOSS_BAR.wrap(context, () -> original.call(instance, context));
	}
	*///?}

	// Action Bar
	//? if >=1.20.5 {
	/*@Inject(
			method = "renderOverlayMessage",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
					shift = At.Shift.AFTER,
					ordinal = 0
			)
	)
	private void autoHud$preActionBar(DrawContext context, /^? if <1.21 {^/float/^?} else {^//^RenderTickCounter^//^?}^/ tickDelta, CallbackInfo ci) {
		ComponentRenderer.ACTION_BAR.beginRender(context);
	}
	@Inject(
			method = "renderOverlayMessage",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V",
					ordinal = 0
			)
	)
	private void autoHud$postActionBar(DrawContext context, /^? if <1.21 {^/float/^?} else {^//^RenderTickCounter^//^?}^/ tickDelta, CallbackInfo ci) {
		ComponentRenderer.ACTION_BAR.endRender(context);
	}
	*///?}

}
