package mod.crend.autohud.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Hud;
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

@Mixin(value = InGameHud.class, priority = 800)
public class InGameHudMixin {

    // Hotbar
    @Inject(method = "renderHotbar", at = @At(value = "HEAD"))
    private void preHotbar(float f, MatrixStack matrixStack, CallbackInfo ci) {
        Hud.preInject(matrixStack, Component.Hotbar);
    }

    @Inject(method = "renderHotbar", at = @At(value = "RETURN"))
    private void postHotbar(float f, MatrixStack matrixStack, CallbackInfo ci) {
        Hud.postInject(matrixStack);
    }

    // Tooltip
    @Inject(method = "renderHeldItemTooltip", at = @At(value = "HEAD"))
    private void preTooltip(MatrixStack matrixStack, CallbackInfo ci) {
        Hud.preInject(matrixStack, Component.Tooltip);
    }

    @Inject(method = "renderHeldItemTooltip", at = @At(value = "RETURN"))
    private void postTooltip(MatrixStack matrixStack, CallbackInfo ci) {
        Hud.postInject(matrixStack);
    }

    // Hotbar items
    @Inject(method = "renderHotbarItem", at = @At(value = "HEAD"))
    private void preHotbarItems(final int x, final int y, final float tickDelta, final PlayerEntity player, final ItemStack stack, final int seed, final CallbackInfo ci) {
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        Hud.preInject(matrixStack, Component.Hotbar);
        RenderSystem.applyModelViewMatrix();
    }

    @Inject(method = "renderHotbarItem", at = @At(value = "RETURN"))
    private void postHotbarItems(final int x, final int y, final float tickDelta, final PlayerEntity player, final ItemStack stack, final int seed, final CallbackInfo ci) {
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        Hud.postInject(matrixStack);
        RenderSystem.applyModelViewMatrix();
    }

    // Experience Bar
    @Inject(method = "renderExperienceBar", at = @At(value = "HEAD"))
    private void preExperienceBar(final MatrixStack matrixStack, final int x, final CallbackInfo ci) {
        Hud.preInject(matrixStack, Component.ExperienceBar);
    }

    @Inject(method = "renderExperienceBar", at = @At(value = "RETURN"))
    private void postExperienceBar(final MatrixStack matrixStack, final int x, final CallbackInfo ci) {
        Hud.postInject(matrixStack);
    }

    // Status Bars
    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 0))
    private void preArmorBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        Hud.preInject(matrixStack, Component.Armor);
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 1))
    private void postArmorBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        Hud.postInject(matrixStack);
        Hud.preInject(matrixStack, Component.Health);
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 2))
    private void postHealthBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        Hud.postInject(matrixStack);
        Hud.preInject(matrixStack, Component.Hunger);
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 3))
    private void postFoodBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        Hud.postInject(matrixStack);
        Hud.preInject(matrixStack, Component.Air);
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 4))
    private void postAirBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        Hud.postInject(matrixStack);
    }

    // Mount Health
    @Inject(method = "renderMountHealth", at = @At(value = "HEAD"))
    private void preMountHealth(final MatrixStack matrixStack, final CallbackInfo ci) {
        Hud.preInject(matrixStack, Component.MountHealth);
    }

    @Inject(method = "renderMountHealth", at = @At(value = "RETURN"))
    private void postMountHealth(final MatrixStack matrixStack, final CallbackInfo ci) {
        Hud.postInject(matrixStack);
    }

    // Mount Jump Bar
    @Inject(method = "renderMountJumpBar", at = @At(value = "HEAD"))
    private void preMountJumpBar(final MatrixStack matrixStack, final int x, final CallbackInfo ci) {
        Hud.preInject(matrixStack, Component.MountJumpBar);
    }

    @Inject(method = "renderMountJumpBar", at = @At(value = "RETURN"))
    private void postMountJumpBar(final MatrixStack matrixStack, final int x, final CallbackInfo ci) {
        Hud.postInject(matrixStack);
    }

    // Scoreboard
    @Inject(method = "renderScoreboardSidebar", at = @At(value = "HEAD"))
    private void preScoreboard(final MatrixStack matrixStack, final ScoreboardObjective objective, final CallbackInfo ci) {
        Hud.preInject(matrixStack, Component.Scoreboard);
    }

    @Inject(method = "renderScoreboardSidebar", at = @At(value = "RETURN"))
    private void postScoreboard(final MatrixStack matrixStack, final ScoreboardObjective objective, final CallbackInfo ci) {
        Hud.postInject(matrixStack);
    }

    // Status Effects
    @Inject(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;getEffectType()Lnet/minecraft/entity/effect/StatusEffect;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void preEffect(MatrixStack matrices, CallbackInfo ci, Collection<StatusEffectInstance> collection, int i, int j, StatusEffectSpriteManager statusEffectSpriteManager, List<Runnable> list, Iterator<StatusEffectInstance> var7, StatusEffectInstance statusEffectInstance) {
        if (Hud.shouldShowIcon(statusEffectInstance)) {
            Hud.preInject(matrices, Component.get(statusEffectInstance.getEffectType()));
        }
    }
    @Inject(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/StatusEffectSpriteManager;getSprite(Lnet/minecraft/entity/effect/StatusEffect;)Lnet/minecraft/client/texture/Sprite;"))
    private void postEffect(MatrixStack matrices, CallbackInfo ci) {
        Hud.postInject(matrices);
    }
    @Inject(method = "method_18620", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawSprite(Lnet/minecraft/client/util/math/MatrixStack;IIIIILnet/minecraft/client/texture/Sprite;)V"))
    private void preSprite(Sprite sprite, float g, MatrixStack matrices, int n, int o, CallbackInfo ci) {
        Component component = Component.findBySprite(sprite);
        if (component != null) {
            Hud.preInject(matrices, component);
        }
        else {
            matrices.push();
        }
    }
    @Inject(method = "method_18620", at = @At(value = "RETURN"))
    private void postSprite(Sprite sprite, float g, MatrixStack matrices, int n, int o, CallbackInfo ci) {
        Hud.postInject(matrices);
    }

    @Redirect(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target="Lnet/minecraft/entity/effect/StatusEffectInstance;shouldShowIcon()Z"))
    private boolean shouldShowIconProxy(StatusEffectInstance instance) {
        return Hud.shouldShowIcon(instance);
    }

    @Inject(method = "tick()V", at = @At(value = "TAIL"))
    private void tickAutoHud(CallbackInfo ci) {
        Hud.tick();
    }

}
