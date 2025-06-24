package mod.crend.autohud.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Hud;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InGameHud.class, priority = 800)
public abstract class InGameHudMixin {

    @Shadow protected abstract int getHeartCount(LivingEntity entity);
    @Shadow protected abstract LivingEntity getRiddenEntity();

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
    private void autoHud$wrapHotbar(
            InGameHud instance,
            float tickDelta, DrawContext context,
            Operation<Void> original
    ) {
        ComponentRenderer.HOTBAR.wrap(context, () ->
            original.call(instance, tickDelta, context)
        );
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
        ComponentRenderer.TOOLTIP.wrap(context, () -> original.call(instance, context));
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

    // Experience Bar
    // Inject at getNextLevelExperience instead of HEAD so OneBar canceling at HEAD does not affect us
    @Inject(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getNextLevelExperience()I"))
    private void autoHud$preExperienceBar(DrawContext context, int x, CallbackInfo ci) {
        ComponentRenderer.EXPERIENCE_BAR.beginRender(context);
    }
    @Inject(method = "renderExperienceBar", at = @At(value="INVOKE", target="Lnet/minecraft/util/profiler/Profiler;pop()V", ordinal = 0))
    private void autoHud$postExperienceBar(DrawContext context, int x, CallbackInfo ci) {
        ComponentRenderer.EXPERIENCE_BAR.endRender(context);
    }

    @Inject(
            method = "renderExperienceBar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I",
                    ordinal = 0
            )
    )
    private void autoHud$experienceText(
            DrawContext context,
            int x,
            CallbackInfo ci
    ) {
        ComponentRenderer.EXPERIENCE_LEVEL.beginRender(context);
    }
    @Inject(
            method = "renderExperienceBar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiler/Profiler;pop()V"
                    , ordinal = 1
            )
    )
    private void autoHud$postExperienceText(
            DrawContext context,
            int x,
            CallbackInfo ci
    ) {
        ComponentRenderer.EXPERIENCE_LEVEL.endRender(context);
    }

    @ModifyArg(
            method = "renderExperienceBar",
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
    @Inject(
            method = "renderStatusBars",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V"
            )
    )
    private void autoHud$preArmorBar(DrawContext context, CallbackInfo ci) {
        ComponentRenderer.ARMOR.beginRender(context);
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 1))
    private void autoHud$postArmorBar(final DrawContext context, final CallbackInfo ci) {
        ComponentRenderer.ARMOR.endRender(context);
    }

    @WrapOperation(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHealthBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V"))
    private void autoHud$wrapHealthBar(InGameHud instance, DrawContext context, PlayerEntity player, int flag4, int x, int y, int lines, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, Operation<Void> original) {
        ComponentRenderer.HEALTH.wrap(context, () ->
                original.call(instance, context, player, flag4, x, y, lines, maxHealth, lastHealth, health, absorption, blinking)
        );
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 1, shift = At.Shift.AFTER))
    private void autoHud$preHungerBar(final DrawContext context, final CallbackInfo ci) {
        ComponentRenderer.HUNGER.beginRender(context);
    }

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 3))
    private void autoHud$preAirBar(final DrawContext context, final CallbackInfo ci) {
        if (getHeartCount(getRiddenEntity()) == 0) {
            ComponentRenderer.HUNGER.endRender(context);
        }
        ComponentRenderer.AIR.beginRender(context);
    }

    @Inject(
            method = "renderStatusBars",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;pop()V")
    )
    private void autoHud$postAirBar(DrawContext context, CallbackInfo ci) {
        ComponentRenderer.AIR.endRender(context);
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
        ComponentRenderer.MOUNT_HEALTH.wrap(context, () -> original.call(instance, context));
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
        ComponentRenderer.MOUNT_JUMP_BAR.wrap(context, () -> original.call(instance, mount, context, x));
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
        ComponentRenderer.SCOREBOARD.wrap(context, () -> original.call(instance, context, objective));
    }
    @ModifyArg(
            method = "renderScoreboardSidebar",
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
            method = "renderScoreboardSidebar",
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
        ComponentRenderer.CROSSHAIR.wrap(context,
                () -> original.call(context, texture, x, y, u, v, width, height),
                () -> original.call(context, texture, x, y, u, v, width, height)
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
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/gui/DrawContext;III)V"
            )
    )
    private void autoHud$wrapChat(
            ChatHud instance,
            DrawContext context,
            int currentTick,
            int mouseX,
            int mouseY,
            Operation<Void> original) {
        ComponentRenderer.CHAT.wrap(context, () -> original.call(instance, context, currentTick, mouseX, mouseY));
    }

    // Boss Bar
    @WrapOperation(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/BossBarHud;render(Lnet/minecraft/client/gui/DrawContext;)V")
    )
    private void autoHud$wrapBossBar(BossBarHud instance, DrawContext context, Operation<Void> original) {
        ComponentRenderer.BOSS_BAR.wrap(context, () -> original.call(instance, context));
    }

    // Action Bar
    @Inject(
            method = "render",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/InGameHud;overlayMessage:Lnet/minecraft/text/Text;")
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
                    shift = At.Shift.AFTER,
                    ordinal = 0
            )
    )
    private void autoHud$preActionBar(DrawContext context, float tickDelta, CallbackInfo ci) {
        ComponentRenderer.ACTION_BAR.beginRender(context);
    }
    @Inject(
            method = "render",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/InGameHud;overlayMessage:Lnet/minecraft/text/Text;")
            ),
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V",
                    ordinal = 0
            )
    )
    private void autoHud$postActionBar(DrawContext context, float tickDelta, CallbackInfo ci) {
        ComponentRenderer.ACTION_BAR.endRender(context);
    }
}
