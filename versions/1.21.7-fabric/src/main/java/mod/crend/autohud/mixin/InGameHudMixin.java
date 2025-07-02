package mod.crend.autohud.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Hud;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.RenderTickCounter;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.RenderPipelines;

@Mixin(value = InGameHud.class, priority = 800)
public abstract class InGameHudMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void autoHud$startRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        AutoHudRenderer.startRender(context, tickCounter);
    }
    @Inject(method = "render", at = @At("TAIL"))
    private void autoHud$endRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        AutoHudRenderer.endRender();
    }

    // Hotbar items
    @WrapOperation(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(Lnet/minecraft/client/gui/DrawContext;IILnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V"
            )
    )
    private void autoHud$transparentHotbarItems(
            InGameHud instance,
            DrawContext context,
            int x,
            int y,
            RenderTickCounter tickDelta,
            PlayerEntity player,
            ItemStack stack,
            int seed,
            Operation<Void> original
    ) {
        ComponentRenderer.HOTBAR_ITEMS.wrap(context, () -> original.call(instance, context, x, y, tickDelta, player, stack, seed));
    }

    @ModifyArg(
            method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)V"
            ),
            index = 4
    )
    private int autoHud$scoreboardSidebarText(int color) {
        if (AutoHudRenderer.inRender) {
            return AutoHudRenderer.getArgb() | 0xFFFFFF;
        }
        return color;
    }
    @ModifyArg(
            method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
            at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"),
            index=4
    )
    private int autoHud$scoreboardSidebarFill(int color) {
        if (AutoHudRenderer.inRender) {
            return AutoHudRenderer.modifyArgb(color);
        }
        return color;
    }

    // Crosshair
    @WrapOperation(method = "renderCrosshair",
            slice = @Slice(
                to = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/GameOptions;getAttackIndicator()Lnet/minecraft/client/option/SimpleOption;")
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V"
            )
    )
    private void autoHud$renderCrosshair(
            DrawContext context,
            RenderPipeline renderPipeline,
            Identifier texture,
            int x, int y,
            int width, int height,
            Operation<Void> original
    ) {
        ComponentRenderer.CROSSHAIR.wrap(context, () -> {
            RenderPipeline customPipeline = RenderPipelines.CROSSHAIR;
            original.call(context,
                    customPipeline,
                    texture, x, y,
                    width, height
            );
        }, () -> {
            original.call(context,
                    renderPipeline,
                    texture, x, y,
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
