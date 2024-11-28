package mod.crend.autohud.fabric.mixin.gui;

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
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//? if >1.20.1 {
/*import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Components;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import java.util.function.Function;
*///?}
//? if >=1.20.5
/*import net.minecraft.client.gui.LayeredDrawer;*/

@Mixin(value = InGameHud.class, priority = 800)
public abstract class InGameHudMixin {

    //? if <1.20.5 {
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
        AutoHudRenderer.endRender();
    }
    *///?}


    // Hotbar
    @WrapOperation(
            //? if <1.20.5 {
            method = "render",
            //?} else {
            /*method = "renderMainHud",
            *///?}
            at = @At(
                    value = "INVOKE",
                    //? if <1.20.5 {
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/gui/DrawContext;)V"
                    //?} else if <1.21 {
                    /*target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(Lnet/minecraft/client/gui/DrawContext;F)V"
                    *///?} else {
                    /*target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"
                    *///?}
            )
    )
    private void autoHud$wrapHotbar(
            InGameHud instance,
            //? if <1.20.5 {
            float tickDelta, DrawContext context,
            //?} else if <1.21 {
            /*DrawContext context, float tickDelta,
            *///?} else
            /*DrawContext context, RenderTickCounter tickDelta,*/
            Operation<Void> original
    ) {
        ComponentRenderer.HOTBAR.wrap(context, () ->
            //? if <1.20.6 {
            original.call(instance, tickDelta, context)
            //?} else {
            /*original.call(instance, context, tickDelta)
            *///?}
        );
    }

    // Tooltip
    @WrapOperation(
            //? if <1.20.5 {
            method = "render",
            //?} else
            /*method = "renderMainHud",*/
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

    // Experience Bar
    //? if <1.20.5 {
    // Inject at getNextLevelExperience instead of HEAD so OneBar canceling at HEAD does not affect us
    @Inject(method = "renderExperienceBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getNextLevelExperience()I"))
    private void autoHud$preExperienceBar(DrawContext context, int x, CallbackInfo ci) {
        ComponentRenderer.EXPERIENCE_BAR.beginRender(context);
    }
    @Inject(method = "renderExperienceBar", at = @At(value="INVOKE", target="Lnet/minecraft/util/profiler/Profiler;pop()V", ordinal = 0))
    private void autoHud$postExperienceBar(DrawContext context, int x, CallbackInfo ci) {
        ComponentRenderer.EXPERIENCE_BAR.endRender(context);
    }
    //?} else {
    /*@WrapOperation(
            method = "renderMainHud",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderExperienceBar(Lnet/minecraft/client/gui/DrawContext;I)V"
            )
    )
    private void autoHud$wrapExperienceBar(InGameHud instance, DrawContext context, int x, Operation<Void> original) {
        //? if <=1.20.1
        if (!autoHud$shouldRenderExperienceLevel(true)) return;
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
    *///?}

    @Inject(
            //? if <1.20.5 {
            method = "renderExperienceBar",
            //?} else
            /*method = "renderExperienceLevel",*/
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I",
                    ordinal = 0
            )
    )
    private void autoHud$experienceText(
            DrawContext context,
            //? if <1.20.5 {
            int x,
            //?} else if <1.21 {
            /*float x,
            *///?} else
            /*RenderTickCounter x,*/
            CallbackInfo ci
    ) {
        ComponentRenderer.EXPERIENCE_LEVEL.beginRender(context);
    }
    @Inject(
            //? if <1.20.5 {
            method = "renderExperienceBar",
            //?} else
            /*method = "renderExperienceLevel",*/
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiler/Profiler;pop()V"
                    //? if <1.20.5
                    , ordinal = 1
            )
    )
    private void autoHud$postExperienceText(
            DrawContext context,
            //? if <1.20.5 {
            int x,
            //?} else if <1.21 {
            /*float x,
            *///?} else
            /*RenderTickCounter x,*/
            CallbackInfo ci
    ) {
        ComponentRenderer.EXPERIENCE_LEVEL.endRender(context);
    }

    @ModifyArg(
            //? if <1.20.5 {
            method = "renderExperienceBar",
            //?} else
            /*method = "renderExperienceLevel",*/
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
    //? if <1.20.5 {
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

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getProfiler()Lnet/minecraft/util/profiler/Profiler;", ordinal = 2))
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
    *///?}

    // Mount Health
    @WrapOperation(
            //? if <1.20.5 {
            method = "render",
            //?} else
            /*method = "renderMainHud",*/
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
            //? if <1.20.5 {
            method = "render",
            //?} else
            /*method = "renderMainHud",*/
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
            //? if <1.20.5 {
            method = "render",
            //?} else if <1.21 {
            /*method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;F)V",
            *///?} else
            /*method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V",*/
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"
            )
    )
    private void autoHud$wrapScoreboardSidebar(InGameHud instance, DrawContext context, ScoreboardObjective objective, Operation<Void> original) {
        ComponentRenderer.SCOREBOARD.wrap(context, () -> original.call(instance, context, objective));
    }
    @ModifyArg(
            //? if <1.20.3 {
            method = "renderScoreboardSidebar",
            //?} else if <1.21.2 {
            /*method = "method_55440",
            *///?} else {
            /*method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
            *///?}
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
            //? if <1.20.3 {
            method = "renderScoreboardSidebar",
            //?} else if <1.21.2 {
            /*method = "method_55440",
            *///?} else {
            /*method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
            *///?}
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
                    //? if <1.20.3
                    u, v,
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
            method = "method_18620",
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

    // Chat
    @WrapOperation(
            //? if <1.20.5 {
            method = "render",
            //?} else
            /*method = "renderChat",*/
            at = @At(
                    value = "INVOKE",
                    //? if <1.20.5 {
                    target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/gui/DrawContext;III)V"
                    //?} else
                    /*target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/gui/DrawContext;IIIZ)V"*/
            )
    )
    private void autoHud$wrapChat(
            ChatHud instance,
            DrawContext context,
            int currentTick,
            int mouseX,
            int mouseY,
            //? if >=1.20.5
            /*boolean bl,*/
            Operation<Void> original) {
        ComponentRenderer.CHAT.wrap(context, () -> original.call(instance, context, currentTick, mouseX, mouseY /*? if >=1.20.5 {*//*,bl*//*?}*/));
    }

    // Boss Bar
    @WrapOperation(
            //? if <1.20.5 {
            method = "render",
            //?} else
            /*method = "method_55808",*/
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/BossBarHud;render(Lnet/minecraft/client/gui/DrawContext;)V")
    )
    private void autoHud$wrapBossBar(BossBarHud instance, DrawContext context, Operation<Void> original) {
        ComponentRenderer.BOSS_BAR.wrap(context, () -> original.call(instance, context));
    }

    // Action Bar
    @Inject(
            //? if <1.20.5 {
            method = "render",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/InGameHud;overlayMessage:Lnet/minecraft/text/Text;")
            ),
            //?} else
            /*method = "renderOverlayMessage",*/
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;push()V",
                    shift = At.Shift.AFTER,
                    ordinal = 0
            )
    )
    private void autoHud$preActionBar(DrawContext context, /*? if <1.21 {*/float/*?} else {*//*RenderTickCounter*//*?}*/ tickDelta, CallbackInfo ci) {
        ComponentRenderer.ACTION_BAR.beginRender(context);
    }
    @Inject(
            //? if <1.20.5 {
            method = "render",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/hud/InGameHud;overlayMessage:Lnet/minecraft/text/Text;")
            ),
            //?} else
            /*method = "renderOverlayMessage",*/
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V",
                    ordinal = 0
            )
    )
    private void autoHud$postActionBar(DrawContext context, /*? if <1.21 {*/float/*?} else {*//*RenderTickCounter*//*?}*/ tickDelta, CallbackInfo ci) {
        ComponentRenderer.ACTION_BAR.endRender(context);
    }
}
