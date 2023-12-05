package mod.crend.autohud.fabric.mixin.gui;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Hud;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.autohud.render.CustomFramebufferRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Mixin(value = InGameHud.class, priority = 800)
public class InGameHudMixin {

    @Inject(method="render", at=@At("HEAD"))
    private void autoHud$preRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        AutoHudRenderer.startRender(context, tickDelta);
    }
    @Inject(method="render", at=@At("RETURN"))
    private void autoHud$postRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        AutoHudRenderer.endRender();
    }


    // Hotbar
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/gui/DrawContext;)V"
            )
    )
    private void autoHud$wrapHotbar(InGameHud instance, float tickDelta, DrawContext context, Operation<Void> original) {
        if (AutoHud.targetHotbar) {
            AutoHudRenderer.preInject(context, Component.Hotbar);
        }
        original.call(instance, tickDelta, context);
        if (AutoHud.targetHotbar) {
            AutoHudRenderer.postInject(context);
        }
    }
    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;getMatrices()Lnet/minecraft/client/util/math/MatrixStack;", ordinal = 0))
    private void autoHud$preHotbar(float tickDelta, DrawContext context, CallbackInfo ci) {
        AutoHudRenderer.injectTransparency();
    }

    // Tooltip
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHeldItemTooltip(Lnet/minecraft/client/gui/DrawContext;)V"
            )
    )
    private void autoHud$wrapTooltip(InGameHud instance, DrawContext context, Operation<Void> original) {
        if (AutoHud.targetHotbar) {
            AutoHudRenderer.preInject(context, Component.Tooltip);
        }
        original.call(instance, context);
        if (AutoHud.targetHotbar) {
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
                AutoHudRenderer.preInjectFadeWithReverseTranslation(context, Component.Hotbar, AutoHud.config.getHotbarItemsMaximumFade());
                CustomFramebufferRenderer.draw(context);
                AutoHudRenderer.postInjectFadeWithReverseTranslation(context);
            }
        } else {
            original.call(instance, context, x, y, tickDelta, player, stack, seed);
        }
    }

    // Experience Bar
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderExperienceBar(Lnet/minecraft/client/gui/DrawContext;I)V"
            )
    )
    private void autoHud$wrapExperienceBar(InGameHud instance, DrawContext context, int x, Operation<Void> original) {
        if (AutoHud.targetExperienceBar) {
            AutoHudRenderer.preInject(context, Component.ExperienceBar);
        }
        original.call(instance, context, x);
        if (AutoHud.targetExperienceBar) {
            AutoHudRenderer.postInject(context);
        }
    }

    @Inject(method = "renderExperienceBar", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I", ordinal = 0, shift = At.Shift.BEFORE))
    private void autoHud$experienceText(DrawContext context, int x, CallbackInfo ci) {
        if (AutoHud.config.revealExperienceTextWithHotbar() && Component.Hotbar.isMoreVisibleThan(Component.ExperienceBar)) {
            AutoHudRenderer.postInject(context);
            AutoHudRenderer.preInject(context, Component.Hotbar);
        }
    }

    @ModifyArg(method = "renderExperienceBar", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"), index = 4)
    private int autoHud$experienceText(int color) {
        if (AutoHudRenderer.inRender) {
            return AutoHudRenderer.modifyArgb(color);
        }
        return color;
    }


    // Status Bars
    @Inject(
            method = "renderStatusBars",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V"
            )
    )
    private void autoHud$preArmorBar(DrawContext context, CallbackInfo ci) {
        if (AutoHud.targetStatusBars) {
            AutoHudRenderer.preInject(context, Component.Armor);
        }
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 1))
    private void autoHud$postArmorBar(final DrawContext context, final CallbackInfo ci) {
        if (AutoHud.targetStatusBars) {
            AutoHudRenderer.postInject(context);
            AutoHudRenderer.preInject(context, Component.Health);
        }
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 2))
    private void autoHud$postHealthBar(final DrawContext context, final CallbackInfo ci) {
        if (AutoHud.targetStatusBars) {
            AutoHudRenderer.postInject(context);
            AutoHudRenderer.preInject(context, Component.Hunger);
        }
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 3))
    private void autoHud$postFoodBar(final DrawContext context, final CallbackInfo ci) {
        if (AutoHud.targetStatusBars) {
            AutoHudRenderer.postInject(context);
            AutoHudRenderer.preInject(context, Component.Air);
        }
    }

    @Inject(
            method = "renderStatusBars",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V")
    )
    private void autoHud$postAirBar(DrawContext context, CallbackInfo ci) {
        if (AutoHud.targetStatusBars) {
            AutoHudRenderer.postInject(context);
        }
    }

    // Mount Health
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountHealth(Lnet/minecraft/client/gui/DrawContext;)V"
            )
    )
    private void autoHud$wrapMountHealth(InGameHud instance, DrawContext context, Operation<Void> original) {
        if (AutoHud.targetStatusBars) {
            AutoHudRenderer.preInject(context, Component.MountHealth);
        }
        original.call(instance, context);
        if (AutoHud.targetStatusBars) {
            AutoHudRenderer.postInject(context);
        }
    }

    // Mount Jump Bar
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountJumpBar(Lnet/minecraft/entity/JumpingMount;Lnet/minecraft/client/gui/DrawContext;I)V"
            )
    )
    private void autoHud$wrapMountJumpBar(InGameHud instance, JumpingMount mount, DrawContext context, int x, Operation<Void> original) {
        if (AutoHud.targetStatusBars) {
            AutoHudRenderer.preInject(context, Component.MountJumpBar);
        }
        original.call(instance, mount, context, x);
        if (AutoHud.targetStatusBars) {
            AutoHudRenderer.postInject(context);
        }
    }

    // Scoreboard
    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"
            )
    )
    private void autoHud$wrapScoreboardSidebar(InGameHud instance, DrawContext context, ScoreboardObjective objective, Operation<Void> original) {
        if (AutoHud.targetScoreboard) {
            AutoHudRenderer.preInject(context, Component.Scoreboard);
        }
        original.call(instance, context, objective);
        if (AutoHud.targetScoreboard) {
            AutoHudRenderer.postInject(context);
        }
    }
    @ModifyArg(method = "method_55440", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I"), index = 4)
    private int autoHud$scoreboardSidebarString(int color) {
        if (AutoHudRenderer.inRender) {
            return AutoHudRenderer.getArgb() | 0xFFFFFF;
        }
        return color;
    }
    @ModifyArg(method = "method_55440", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I"), index = 4)
    private int autoHud$scoreboardSidebarText(int color) {
        if (AutoHudRenderer.inRender) {
            return AutoHudRenderer.getArgb() | 0xFFFFFF;
        }
        return color;
    }
    @ModifyArg(method = "method_55440", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"), index=4)
    private int autoHud$scoreboardSidebarFill(int color) {
        if (AutoHudRenderer.inRender) {
            return AutoHudRenderer.modifyArgb(color);
        }
        return color;
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
    @Inject(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;getEffectType()Lnet/minecraft/entity/effect/StatusEffect;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void autoHud$preEffect(DrawContext context, CallbackInfo ci, Collection<StatusEffectInstance> collection, int i, int j, StatusEffectSpriteManager statusEffectSpriteManager, List<Runnable> list, Iterator<StatusEffectInstance> var7, StatusEffectInstance statusEffectInstance) {
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
    @Inject(method = "method_18620", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawSprite(IIIIILnet/minecraft/client/texture/Sprite;)V"))
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
    @Inject(method = "method_18620", at = @At(value = "RETURN"))
    private static void autoHud$postSprite(DrawContext drawContext, float f, int i, int j, Sprite sprite, CallbackInfo ci) {
        if (AutoHud.targetStatusEffects) {
            AutoHudRenderer.postInject(drawContext);
        }
    }

    @Redirect(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target="Lnet/minecraft/entity/effect/StatusEffectInstance;shouldShowIcon()Z"))
    private boolean autoHud$shouldShowIconProxy(StatusEffectInstance instance) {
        return Hud.shouldShowIcon(instance);
    }

    // Chat
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/gui/DrawContext;III)V"))
    private void autoHud$wrapChat(ChatHud instance, DrawContext context, int currentTick, int mouseX, int mouseY, Operation<Void> original) {
        if (Component.Chat.config.active()) {
            AutoHudRenderer.preInject(context, Component.Chat);
        }
        original.call(instance, context, currentTick, mouseX, mouseY);
        if (Component.Chat.config.active()) {
            AutoHudRenderer.postInject(context);
        }
    }

    // Boss Bar
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/BossBarHud;render(Lnet/minecraft/client/gui/DrawContext;)V"))
    private void autoHud$wrapBossBar(BossBarHud instance, DrawContext context, Operation<Void> original) {
        if (Component.BossBar.config.active()) {
            AutoHudRenderer.preInject(context, Component.BossBar);
        }
        original.call(instance, context);
        if (Component.BossBar.config.active()) {
            AutoHudRenderer.postInject(context);
        }
    }

    // Action Bar
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", ordinal = 0, shift = At.Shift.AFTER))
    private void autoHud$preActionBar(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (Component.ActionBar.config.active()) {
            AutoHudRenderer.preInject(context, Component.ActionBar);
        }
    }
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V", ordinal = 0, shift = At.Shift.BEFORE))
    private void autoHud$postActionBar(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (Component.ActionBar.config.active()) {
            AutoHudRenderer.postInject(context);
        }
    }


    @Inject(method = "tick()V", at = @At(value = "TAIL"))
    private void autoHud$tickAutoHud(CallbackInfo ci) {
        Hud.tick();
    }

}
