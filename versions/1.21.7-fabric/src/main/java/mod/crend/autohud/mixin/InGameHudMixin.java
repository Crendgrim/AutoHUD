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
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.RenderTickCounter;
import com.mojang.blaze3d.pipeline.RenderPipeline;

@Mixin(value = InGameHud.class, priority = 800)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public abstract class InGameHudMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void autoHud$startRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        AutoHudRenderer.startRender(context, tickCounter);
    }
    @Inject(method = "render", at = @At("TAIL"))
    private void autoHud$endRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        AutoHudRenderer.endRender();
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
                    target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"
            )
    )
    private void autoHud$wrapStatusEffect(
            DrawContext context,
            RenderPipeline renderPipeline,
            Identifier texture,
            int x, int y,
            int width, int height,
            Operation<Void> original,
            @Local StatusEffectInstance statusEffectInstance
    ) {
        ComponentRenderer.getForStatusEffect(statusEffectInstance).wrap(context,
                () -> original.call(
                        context,
                        renderPipeline,
                        texture,
                        x, y,
                        width, height
                )
        );
    }

    @WrapOperation(
            method = "renderStatusEffectOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIIII)V"
            )
    )
    private void autoHud$wrapSprite(
            DrawContext context,
            RenderPipeline renderPipeline,
            Identifier texture,
            int x, int y,
            int width, int height,
            int color,
            Operation<Void> original,
            @Local StatusEffectInstance statusEffectInstance
    ) {
        ComponentRenderer.getForStatusEffect(statusEffectInstance).wrap(context,
                () -> original.call(
                        context,
                        renderPipeline,
                        texture,
                        x, y,
                        width, height,
                        color
                )
        );
    }

}
