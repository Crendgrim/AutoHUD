package mod.crend.autohud.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Hud;
import net.minecraft.client.gui.hud.InGameHud;
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
    @Inject (method = "renderHotbar", at = @At(value = "HEAD"))
    private void preHotbar(float f, MatrixStack matrixStack, CallbackInfo ci) {
        preInject(matrixStack, Component.Hotbar);
    }
    @Inject (method = "renderHotbar", at = @At(value = "RETURN"))
    private void postHotbar(float f, MatrixStack matrixStack, CallbackInfo ci) {
        postInject(matrixStack);
    }

    // Tooltip
    @Inject (method = "renderHeldItemTooltip", at = @At(value = "HEAD"))
    private void preTooltip(MatrixStack matrixStack, CallbackInfo ci) {
        preInject(matrixStack, Component.Tooltip);
    }
    @Inject (method = "renderHeldItemTooltip", at = @At(value = "RETURN"))
    private void postTooltip(MatrixStack matrixStack, CallbackInfo ci){
        postInject(matrixStack);
    }

    // Hotbar items
    @Inject (method = "renderHotbarItem", at = @At(value = "HEAD"))
    private void preHotbarItems(final int x, final int y, final float tickDelta, final PlayerEntity player, final ItemStack stack, final int seed, final CallbackInfo ci) {
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        preInject(matrixStack, Component.Hotbar);
        RenderSystem.applyModelViewMatrix();
    }
    @Inject (method = "renderHotbarItem", at = @At(value = "RETURN"))
    private void postHotbarItems(final int x, final int y, final float tickDelta, final PlayerEntity player, final ItemStack stack, final int seed, final CallbackInfo ci) {
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        postInject(matrixStack);
        RenderSystem.applyModelViewMatrix();
    }

    // Experience Bar
    @Inject (method = "renderExperienceBar", at = @At(value = "HEAD"))
    private void preExperienceBar(final MatrixStack matrixStack, final int x, final CallbackInfo ci) {
        preInject(matrixStack, Component.ExperienceBar);
    }
    @Inject (method = "renderExperienceBar", at = @At(value = "RETURN"))
    private void postExperienceBar(final MatrixStack matrixStack, final int x, final CallbackInfo ci) {
        postInject(matrixStack);
    }

    // Status Bars
    @Inject (method = "renderStatusBars", at = @At(value = "INVOKE", target="Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 0))
    private void preArmorBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        preInject(matrixStack, Component.Armor);
    }
    @Inject (method = "renderStatusBars", at = @At(value = "INVOKE", target="Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 1))
    private void postArmorBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        postInject(matrixStack);
        preInject(matrixStack, Component.Health);
    }
    @Inject (method = "renderStatusBars", at = @At(value = "INVOKE", target="Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 2))
    private void postHealthBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        postInject(matrixStack);
        preInject(matrixStack, Component.Hunger);
    }
    @Inject (method = "renderStatusBars", at = @At(value = "INVOKE", target="Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 3))
    private void postFoodBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        postInject(matrixStack);
        preInject(matrixStack, Component.Air);
    }
    @Inject (method = "renderStatusBars", at = @At(value = "INVOKE", target="Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 4))
    private void postAirBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        postInject(matrixStack);
    }

    // Mount Health
    @Inject (method = "renderMountHealth", at = @At(value = "HEAD"))
    private void preMountHealth(final MatrixStack matrixStack, final CallbackInfo ci) {
        preInject(matrixStack, Component.MountHealth);
    }
    @Inject (method = "renderMountHealth", at = @At(value = "RETURN"))
    private void postMountHealth(final MatrixStack matrixStack, final CallbackInfo ci) {
        postInject(matrixStack);
    }

    // Mount Jump Bar
    @Inject (method = "renderMountJumpBar", at = @At(value = "HEAD"))
    private void preMountJumpBar(final MatrixStack matrixStack, final int x, final CallbackInfo ci) {
        preInject(matrixStack, Component.MountJumpBar);
    }
    @Inject (method = "renderMountJumpBar", at = @At(value = "RETURN"))
    private void postMountJumpBar(final MatrixStack matrixStack, final int x, final CallbackInfo ci) {
        postInject(matrixStack);
    }

    // Crosshair
    @Inject (method = "renderCrosshair", at = @At(value = "HEAD"), cancellable = true)
    private void preCrosshair(final MatrixStack matrixStack, final CallbackInfo ci) {
        if (!Hud.shouldShowCrosshair()) ci.cancel();
    }

    // Scoreboard
    @Inject (method = "renderScoreboardSidebar", at = @At(value = "HEAD"))
    private void preScoreboard(final MatrixStack matrixStack, final ScoreboardObjective objective, final CallbackInfo ci) {
        preInject(matrixStack, Component.Scoreboard);
    }
    @Inject (method = "renderScoreboardSidebar", at = @At(value = "RETURN"))
    private void postScoreboard(final MatrixStack matrixStack, final ScoreboardObjective objective, final CallbackInfo ci) {
        postInject(matrixStack);
    }

    // Status Effects
    @Inject (method = "renderStatusEffectOverlay", at = @At(value = "HEAD"))
    private void preStatusEffects(final MatrixStack matrixStack, final CallbackInfo ci) {
        preInject(matrixStack, Component.StatusEffects);
    }
    @Inject (method = "renderStatusEffectOverlay", at = @At(value = "RETURN"))
    private void postStatusEffects(final MatrixStack matrixStack, final CallbackInfo ci) {
        postInject(matrixStack);
    }
    @Redirect(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target="Lnet/minecraft/entity/effect/StatusEffectInstance;shouldShowIcon()Z"))
    private boolean shouldShowIconProxy(StatusEffectInstance instance) {
        if (AutoHud.config.hideTurtleHelmetWaterBreathing() && instance.getTranslationKey().equals("effect.minecraft.water_breathing") && instance.getDuration() == 200) {
            return false;
        }
        return instance.shouldShowIcon();
    }

}
