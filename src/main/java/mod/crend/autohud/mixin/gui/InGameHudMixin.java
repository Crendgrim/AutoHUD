package mod.crend.autohud.mixin.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.AutoHud;
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
    private void autoHud$preHotbar(float f, MatrixStack matrixStack, CallbackInfo ci) {
        if (AutoHud.targetHotbar) {
            Hud.preInject(matrixStack, Component.Hotbar);
        }
    }

    @Inject(method = "renderHotbar", at = @At(value = "RETURN"))
    private void autoHud$postHotbar(float f, MatrixStack matrixStack, CallbackInfo ci) {
        if (AutoHud.targetHotbar) {
            Hud.postInject(matrixStack);
        }
    }

    // Tooltip
    @Inject(method = "renderHeldItemTooltip", at = @At(value = "HEAD"))
    private void autoHud$preTooltip(MatrixStack matrixStack, CallbackInfo ci) {
        if (AutoHud.targetHotbar) {
            Hud.preInject(matrixStack, Component.Tooltip);
        }
    }

    @Inject(method = "renderHeldItemTooltip", at = @At(value = "RETURN"))
    private void autoHud$postTooltip(MatrixStack matrixStack, CallbackInfo ci) {
        if (AutoHud.targetHotbar) {
            Hud.postInject(matrixStack);
        }
    }

    // Hotbar items
    @Inject(method = "renderHotbarItem", at = @At(value = "HEAD"))
    private void autoHud$preHotbarItems(final int x, final int y, final float tickDelta, final PlayerEntity player, final ItemStack stack, final int seed, final CallbackInfo ci) {
        if (AutoHud.targetHotbar) {
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            Hud.preInject(matrixStack, Component.Hotbar);
            RenderSystem.applyModelViewMatrix();
        }
    }

    @Inject(method = "renderHotbarItem", at = @At(value = "RETURN"))
    private void autoHud$postHotbarItems(final int x, final int y, final float tickDelta, final PlayerEntity player, final ItemStack stack, final int seed, final CallbackInfo ci) {
        if (AutoHud.targetHotbar) {
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            Hud.postInject(matrixStack);
            RenderSystem.applyModelViewMatrix();
        }
    }

    // Experience Bar
    //@Inject(method = "renderExperienceBar", at = @At(value = "HEAD"))
    @Inject(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getNextLevelExperience()I"))
    private void autoHud$preExperienceBar(final MatrixStack matrixStack, final int x, final CallbackInfo ci) {
        if (AutoHud.targetExperienceBar) {
            Hud.preInject(matrixStack, Component.ExperienceBar);
        }
    }

    @Inject(method = "renderExperienceBar", at = @At(value = "RETURN"))
    private void autoHud$postExperienceBar(final MatrixStack matrixStack, final int x, final CallbackInfo ci) {
        if (AutoHud.targetExperienceBar) {
            Hud.postInject(matrixStack);
        }
    }

    // Status Bars
    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 0))
    private void autoHud$preArmorBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        if (AutoHud.targetStatusBars) {
            Hud.preInject(matrixStack, Component.Armor);
        }
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 1))
    private void autoHud$postArmorBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        if (AutoHud.targetStatusBars) {
            Hud.postInject(matrixStack);
            Hud.preInject(matrixStack, Component.Health);
        }
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 2))
    private void autoHud$postHealthBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        if (AutoHud.targetStatusBars) {
            Hud.postInject(matrixStack);
            Hud.preInject(matrixStack, Component.Hunger);
        }
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 3))
    private void autoHud$postFoodBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        if (AutoHud.targetStatusBars) {
            Hud.postInject(matrixStack);
            Hud.preInject(matrixStack, Component.Air);
        }
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 4))
    private void autoHud$postAirBar(final MatrixStack matrixStack, final CallbackInfo ci) {
        if (AutoHud.targetStatusBars) {
            Hud.postInject(matrixStack);
        }
    }

    // Mount Health
    @Inject(method = "renderMountHealth", at = @At(value = "HEAD"))
    private void autoHud$preMountHealth(final MatrixStack matrixStack, final CallbackInfo ci) {
        if (AutoHud.targetStatusBars) {
            Hud.preInject(matrixStack, Component.MountHealth);
        }
    }

    @Inject(method = "renderMountHealth", at = @At(value = "RETURN"))
    private void autoHud$postMountHealth(final MatrixStack matrixStack, final CallbackInfo ci) {
        if (AutoHud.targetStatusBars) {
            Hud.postInject(matrixStack);
        }
    }

    // Mount Jump Bar
    @Inject(method = "renderMountJumpBar", at = @At(value = "HEAD"))
    private void autoHud$preMountJumpBar(final MatrixStack matrixStack, final int x, final CallbackInfo ci) {
        if (AutoHud.targetStatusBars) {
            Hud.preInject(matrixStack, Component.MountJumpBar);
        }
    }

    @Inject(method = "renderMountJumpBar", at = @At(value = "RETURN"))
    private void autoHud$postMountJumpBar(final MatrixStack matrixStack, final int x, final CallbackInfo ci) {
        if (AutoHud.targetStatusBars) {
            Hud.postInject(matrixStack);
        }
    }

    // Scoreboard
    @Inject(method = "renderScoreboardSidebar", at = @At(value = "HEAD"))
    private void autoHud$preScoreboard(final MatrixStack matrixStack, final ScoreboardObjective objective, final CallbackInfo ci) {
        if (AutoHud.targetScoreboard) {
            Hud.preInject(matrixStack, Component.Scoreboard);
        }
    }

    @Inject(method = "renderScoreboardSidebar", at = @At(value = "RETURN"))
    private void autoHud$postScoreboard(final MatrixStack matrixStack, final ScoreboardObjective objective, final CallbackInfo ci) {
        if (AutoHud.targetScoreboard) {
            Hud.postInject(matrixStack);
        }
    }

    // Status Effects
    @Inject(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectInstance;getEffectType()Lnet/minecraft/entity/effect/StatusEffect;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void autoHud$preEffect(MatrixStack matrices, CallbackInfo ci, Collection<StatusEffectInstance> collection, int i, int j, StatusEffectSpriteManager statusEffectSpriteManager, List<Runnable> list, Iterator<StatusEffectInstance> var7, StatusEffectInstance statusEffectInstance) {
        if (AutoHud.targetStatusEffects && Hud.shouldShowIcon(statusEffectInstance)) {
            Hud.preInject(matrices, Component.get(statusEffectInstance.getEffectType()));
        }
    }
    @Inject(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/StatusEffectSpriteManager;getSprite(Lnet/minecraft/entity/effect/StatusEffect;)Lnet/minecraft/client/texture/Sprite;"))
    private void autoHud$postEffect(MatrixStack matrices, CallbackInfo ci) {
        if (AutoHud.targetStatusEffects) {
            Hud.postInject(matrices);
        }
    }
    @Inject(method = "method_18620", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawSprite(Lnet/minecraft/client/util/math/MatrixStack;IIIIILnet/minecraft/client/texture/Sprite;)V"))
    private void autoHud$preSprite(Sprite sprite, float g, MatrixStack matrices, int n, int o, CallbackInfo ci) {
        if (AutoHud.targetStatusEffects) {
            Component component = Component.findBySprite(sprite);
            if (component != null) {
                Hud.preInject(matrices, component);
            } else {
                matrices.push();
            }
        }
    }
    @Inject(method = "method_18620", at = @At(value = "RETURN"))
    private void autoHud$postSprite(Sprite sprite, float g, MatrixStack matrices, int n, int o, CallbackInfo ci) {
        if (AutoHud.targetStatusEffects) {
            Hud.postInject(matrices);
        }
    }

    @Redirect(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target="Lnet/minecraft/entity/effect/StatusEffectInstance;shouldShowIcon()Z"))
    private boolean autoHud$shouldShowIconProxy(StatusEffectInstance instance) {
        return Hud.shouldShowIcon(instance);
    }

    @Inject(method = "tick()V", at = @At(value = "TAIL"))
    private void autoHud$tickAutoHud(CallbackInfo ci) {
        Hud.tick();
    }

}
