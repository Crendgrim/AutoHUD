package mod.crend.autohud.forge.mixin.gui;

import com.llamalad7.mixinextras.sugar.Local;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.render.ComponentRenderer;
import mod.crend.libbamboo.PlatformUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/*
 * This file is originally part of the StatusEffectTimer mod:
 *   https://github.com/magicus/statuseffecttimer
 *
 * It is included here in order to be modified properly.
 * Changes:
 *  - Skip timers for hidden effects by redirecting StatusEffectInstance.shouldShowIcon()
 *  - Insert matrix push/pop for moving each timer text with its icon
 */

// Set priority to 500, to load before default at 1000. This is to better cooperate with HUDTweaks.
@Mixin(value = InGameHud.class, priority = 500)
public abstract class StatusEffectTimerMixin {
    @Shadow @Final
	protected MinecraftClient client;

    @Inject(method = "renderStatusEffectOverlay",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER))
    private void appendOverlayDrawing(
            DrawContext context,
            //? if >1.20.6 {
            /*RenderTickCounter tickDelta,
            *///?} else if >1.20.5
            /*float tickDelta,*/
            CallbackInfo c,
            @Local List<Runnable> list,
            @Local StatusEffectInstance statusEffectInstance,
            @Local(ordinal = 2) int i2,
            @Local(ordinal = 3) int i3,
            @Local(ordinal = 4) int i4,
            @Local(ordinal = 5) int i5
    ) {
        list.add(() -> {
            if (AutoHud.config.statusEffectTimer()) {
                int x = switch (PlatformUtils.getCurrentPlatform()) {
                    case FABRIC -> i4;
                    case FORGE -> i5;
                    case NEOFORGE -> i2;
                };
                int y = switch (PlatformUtils.getCurrentPlatform()) {
                    case FABRIC -> i5;
                    case FORGE -> i3;
                    case NEOFORGE -> i3;
                };
                ComponentRenderer.getForStatusEffect(statusEffectInstance).wrap(context, () -> drawStatusEffectOverlay(context, statusEffectInstance, x, y));
            }
        });
    }

    @Unique
    private void drawStatusEffectOverlay(DrawContext context, StatusEffectInstance statusEffectInstance, int x, int y) {
        String duration = getDurationAsString(statusEffectInstance);
        int durationLength = client.textRenderer.getWidth(duration);
        context.drawTextWithShadow(client.textRenderer, duration, x + 13 - (durationLength / 2), y + 14, 0x99FFFFFF);

        int amplifier = statusEffectInstance.getAmplifier();
        if (amplifier > 0) {
            // Convert to roman numerals if possible
            String amplifierString = (amplifier < 10) ? I18n.translate("enchantment.level." + (amplifier + 1)) : "**";
            int amplifierLength = client.textRenderer.getWidth(amplifierString);
            context.drawTextWithShadow(client.textRenderer, amplifierString, x + 22 - amplifierLength, y + 3, 0x99FFFFFF);
        }
    }

    @Unique
    private String getDurationAsString(StatusEffectInstance statusEffectInstance) {
        if (statusEffectInstance.isInfinite()) {
            return I18n.translate("effect.duration.infinite");
        }

        int ticks = MathHelper.floor((float) statusEffectInstance.getDuration());
        int seconds = ticks / 20;

        if (seconds >= 3600) {
            return seconds / 3600 + "h";
        } else if (seconds >= 60) {
            return seconds / 60 + "m";
        } else {
            return String.valueOf(seconds);
        }
    }
}
