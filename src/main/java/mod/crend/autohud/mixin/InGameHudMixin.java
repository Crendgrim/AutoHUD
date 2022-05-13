package mod.crend.autohud.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.*;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    private void preInject(MatrixStack matrixStack, Component component) {
        matrixStack.push();
        if (component.isHidden()) {
            matrixStack.translate(component.getDeltaX(), component.getDeltaY(), 0);
        }
    }

    private void postInject(MatrixStack matrixStack) {
        matrixStack.pop();
    }

    // Hotbar
    @Inject(method = "renderHotbar", at = @At(value = "HEAD"))
    private void preHotbar(float f, MatrixStack matrixStack, CallbackInfo ci) {
        preInject(matrixStack, Component.Hotbar);
    }

    @Inject(method = "renderHotbar", at = @At(value = "RETURN"))
    private void postHotbar(float f, MatrixStack matrixStack, CallbackInfo ci) {
        postInject(matrixStack);
    }

    // Tooltip
    @Inject(method = "renderHeldItemTooltip", at = @At(value = "HEAD"))
    private void preTooltip(MatrixStack matrixStack, CallbackInfo ci) {
        preInject(matrixStack, Component.Tooltip);
    }

    @Inject(method = "renderHeldItemTooltip", at = @At(value = "RETURN"))
    private void postTooltip(MatrixStack matrixStack, CallbackInfo ci) {
        postInject(matrixStack);
    }

    // Hotbar items
    @Inject(method = "renderHotbarItem", at = @At(value = "HEAD"))
    private void preHotbarItems(final int x, final int y, final float tickDelta, final PlayerEntity player, final ItemStack stack, final int seed, final CallbackInfo ci) {
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        preInject(matrixStack, Component.Hotbar);
        RenderSystem.applyModelViewMatrix();
    }

    @Inject(method = "renderHotbarItem", at = @At(value = "RETURN"))
    private void postHotbarItems(final int x, final int y, final float tickDelta, final PlayerEntity player, final ItemStack stack, final int seed, final CallbackInfo ci) {
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        postInject(matrixStack);
        RenderSystem.applyModelViewMatrix();
    }

    // Experience Bar
    @Inject(method = "renderExperienceBar", at = @At(value = "HEAD"))
    private void preExperienceBar(final MatrixStack matrixStack, final int x, final CallbackInfo ci) {
        preInject(matrixStack, Component.ExperienceBar);
    }

    @Inject(method = "renderExperienceBar", at = @At(value = "RETURN"))
    private void postExperienceBar(final MatrixStack matrixStack, final int x, final CallbackInfo ci) {
        postInject(matrixStack);
    }

    // Status Bars
    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 0))
    private void preArmorBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        preInject(matrixStack, Component.Armor);
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 1))
    private void postArmorBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        postInject(matrixStack);
        preInject(matrixStack, Component.Health);
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 2))
    private void postHealthBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        postInject(matrixStack);
        preInject(matrixStack, Component.Hunger);
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 3))
    private void postFoodBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        postInject(matrixStack);
        preInject(matrixStack, Component.Air);
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 4))
    private void postAirBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        postInject(matrixStack);
    }

    // Mount Health
    @Inject(method = "renderMountHealth", at = @At(value = "HEAD"))
    private void preMountHealth(final MatrixStack matrixStack, final CallbackInfo ci) {
        preInject(matrixStack, Component.MountHealth);
    }

    @Inject(method = "renderMountHealth", at = @At(value = "RETURN"))
    private void postMountHealth(final MatrixStack matrixStack, final CallbackInfo ci) {
        postInject(matrixStack);
    }

    // Mount Jump Bar
    @Inject(method = "renderMountJumpBar", at = @At(value = "HEAD"))
    private void preMountJumpBar(final MatrixStack matrixStack, final int x, final CallbackInfo ci) {
        preInject(matrixStack, Component.MountJumpBar);
    }

    @Inject(method = "renderMountJumpBar", at = @At(value = "RETURN"))
    private void postMountJumpBar(final MatrixStack matrixStack, final int x, final CallbackInfo ci) {
        postInject(matrixStack);
    }

    // Crosshair
    @Inject(method = "renderCrosshair", at = @At(value = "HEAD"), cancellable = true)
    private void preCrosshair(final MatrixStack matrixStack, final CallbackInfo ci) {
        if (!CrosshairHandler.shouldShowCrosshair()) ci.cancel();
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 0))
    private void drawCrosshair(InGameHud instance, MatrixStack matrixStack, int x, int y, int u, int v, int width, int height) {
        if (AutoHud.config.dynamicCrosshairStyle()) {
            Crosshair crosshair = CrosshairHandler.getActiveCrosshair();
            RenderSystem.setShaderTexture(0, CrosshairHandler.crosshairTexture);
            instance.drawTexture(matrixStack, x, y, crosshair.getX(), crosshair.getY(), 15, 15);
            for (CrosshairModifier modifier : CrosshairHandler.getActiveCrosshairModifiers()) {
                instance.drawTexture(matrixStack, x, y, modifier.getX(), modifier.getY(), 15, 15);
            }
            RenderSystem.setShaderTexture(0, InGameHud.GUI_ICONS_TEXTURE);
        } else {
            instance.drawTexture(matrixStack, x, y, u, v, width, height);
        }
    }

    // Scoreboard
    @Inject(method = "renderScoreboardSidebar", at = @At(value = "HEAD"))
    private void preScoreboard(final MatrixStack matrixStack, final ScoreboardObjective objective, final CallbackInfo ci) {
        preInject(matrixStack, Component.Scoreboard);
    }

    @Inject(method = "renderScoreboardSidebar", at = @At(value = "RETURN"))
    private void postScoreboard(final MatrixStack matrixStack, final ScoreboardObjective objective, final CallbackInfo ci) {
        postInject(matrixStack);
    }

    // Status Effects
    @Inject(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;getEffectType()Lnet/minecraft/entity/effect/StatusEffect;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void preEffect(MatrixStack matrices, CallbackInfo ci, Collection<StatusEffectInstance> collection, int i, int j, StatusEffectSpriteManager statusEffectSpriteManager, List<Runnable> list, Iterator<StatusEffectInstance> var7, StatusEffectInstance statusEffectInstance) {
        preInject(matrices, Component.get(statusEffectInstance.getEffectType()));
    }
    @Inject(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/StatusEffectSpriteManager;getSprite(Lnet/minecraft/entity/effect/StatusEffect;)Lnet/minecraft/client/texture/Sprite;"))
    private void postEffect(MatrixStack matrices, CallbackInfo ci) {
        postInject(matrices);
    }
    @Inject(method = "method_18620", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawSprite(Lnet/minecraft/client/util/math/MatrixStack;IIIIILnet/minecraft/client/texture/Sprite;)V"))
    private void preSprite(Sprite sprite, float g, MatrixStack matrices, int n, int o, CallbackInfo ci) {
        Component component = Component.findBySprite(sprite);
        if (component != null) {
            preInject(matrices, component);
        }
        else {
            matrices.push();
        }
    }
    @Inject(method = "method_18620", at = @At(value = "RETURN"))
    private void postSprite(Sprite sprite, float g, MatrixStack matrices, int n, int o, CallbackInfo ci) {
        postInject(matrices);
    }

    @Redirect(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target="Lnet/minecraft/entity/effect/StatusEffectInstance;shouldShowIcon()Z"))
    private boolean shouldShowIconProxy(StatusEffectInstance instance) {
        return Hud.shouldShowIcon(instance);
    }

    @Inject(method = "tick()V", at = @At(value = "TAIL"))
    private void tickAutoHud(CallbackInfo ci) {
        Hud.tick();
        CrosshairHandler.tick();
    }

}
