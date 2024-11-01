package mod.crend.autohud.compat.mixin.dehydration;

import mod.crend.autohud.compat.DehydrationCompat;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class DehydrationMixin {
    @Mixin(value = InGameHud.class, priority = 900)
    public static class EarlyMixin {
        //? if dehydration {
        @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 1))
        private void autoHud$preThirst(DrawContext context, CallbackInfo ci) {
            AutoHudRenderer.postInject(context);
            AutoHudRenderer.preInject(context, DehydrationCompat.Thirst);
        }
        //?}
    }
    @Mixin(value = InGameHud.class, priority = 1200)
    public static class LateMixin {
        //? if dehydration {
        @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 1))
        private void autoHud$postThirst(DrawContext context, CallbackInfo ci) {
            AutoHudRenderer.postInject(context);
            AutoHudRenderer.preInject(context, Component.Hunger);
        }
        //?}
    }
}
