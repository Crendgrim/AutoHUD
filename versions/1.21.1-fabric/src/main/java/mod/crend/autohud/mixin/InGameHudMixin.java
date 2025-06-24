package mod.crend.autohud.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Components;
import mod.crend.autohud.component.Hud;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LayeredDrawer;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InGameHud.class, priority = 800)
public abstract class InGameHudMixin {

    @Shadow @Final public MinecraftClient client;

    @WrapOperation(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/LayeredDrawer;render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"
            )
    )
    private void autoHud$render(
            LayeredDrawer instance,
            DrawContext context,
            RenderTickCounter tickCounter,
            Operation<Void> original
    ) {
        AutoHudRenderer.startRender(context, tickCounter);
        original.call(instance, context, tickCounter);
        AutoHudRenderer.endRender();
    }


    // Hotbar
    @WrapOperation(
            method = "renderMainHud",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"
            )
    )
    private void autoHud$wrapHotbar(
            InGameHud instance,
            DrawContext context, RenderTickCounter tickDelta,
            Operation<Void> original
    ) {
        ComponentRenderer.HOTBAR.wrap(context, () ->
            original.call(instance, context, tickDelta)
        );
    }

    // Tooltip
    @WrapOperation(
            method = "renderMainHud",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHeldItemTooltip(Lnet/minecraft/client/gui/DrawContext;)V"
            )
    )
    private void autoHud$wrapTooltip(InGameHud instance, DrawContext context, Operation<Void> original) {
        ComponentRenderer.TOOLTIP.wrap(context, () -> original.call(instance, context));
    }
    //?}

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

    // Experience Bar
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
        if (!client.interactionManager.hasExperienceBar()) {
            return false;
        }
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
            RenderTickCounter x,
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
            RenderTickCounter x,
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


    // Status Bars
    @WrapOperation(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderArmor(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIII)V"))
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

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getMaxAir()I", shift = At.Shift.AFTER))
    private void autoHud$preAirBar(DrawContext context, CallbackInfo ci) {
        ComponentRenderer.AIR.beginRender(context);
    }
    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V"))
    private void autoHud$postAirBar(DrawContext context, CallbackInfo ci) {
        ComponentRenderer.AIR.endRender(context);
    }

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

    // Scoreboard
    @WrapOperation(
            method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"
            )
    )
    private void autoHud$wrapScoreboardSidebar(InGameHud instance, DrawContext context, ScoreboardObjective objective, Operation<Void> original) {
        ComponentRenderer.SCOREBOARD.wrap(context, () -> original.call(instance, context, objective));
    }
    @ModifyArg(
            method = "method_55440",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I"
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
            method = "method_55440",
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
        ComponentRenderer.CROSSHAIR.wrap(context,
                () -> original.call(context, texture, x, y, width, height),
                () -> original.call(context, texture, x, y, width, height)
        );
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
                    target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"
            )
    )
    private void autoHud$wrapStatusEffect(
            DrawContext context,
            Identifier texture,
            int x, int y,
            int width, int height,
            Operation<Void> original,
            @Local StatusEffectInstance statusEffectInstance
    ) {
        ComponentRenderer.getForStatusEffect(statusEffectInstance).wrap(context,
                () -> original.call(
                        context,
                        texture,
                        x, y,
                        width, height
                )
        );
    }
    @WrapOperation(
            method = "method_18620",
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

    // Chat
    @WrapOperation(
            method = "renderChat",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/gui/DrawContext;IIIZ)V"
            )
    )
    private void autoHud$wrapChat(
            ChatHud instance,
            DrawContext context,
            int currentTick,
            int mouseX,
            int mouseY,
            boolean bl,
            Operation<Void> original) {
        ComponentRenderer.CHAT.wrap(context, () -> original.call(instance, context, currentTick, mouseX, mouseY, bl));
    }

    // Boss Bar
    @WrapOperation(
            method = "method_55808",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/BossBarHud;render(Lnet/minecraft/client/gui/DrawContext;)V")
    )
    private void autoHud$wrapBossBar(BossBarHud instance, DrawContext context, Operation<Void> original) {
        ComponentRenderer.BOSS_BAR.wrap(context, () -> original.call(instance, context));
    }

    // Action Bar
    @Inject(
            method = "renderOverlayMessage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
                    shift = At.Shift.AFTER,
                    ordinal = 0
            )
    )
    private void autoHud$preActionBar(DrawContext context, RenderTickCounter tickDelta, CallbackInfo ci) {
        ComponentRenderer.ACTION_BAR.beginRender(context);
    }
    @Inject(
            method = "renderOverlayMessage",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V",
                    ordinal = 0
            )
    )
    private void autoHud$postActionBar(DrawContext context, RenderTickCounter tickDelta, CallbackInfo ci) {
        ComponentRenderer.ACTION_BAR.endRender(context);
    }
    //?}
}
//?}
