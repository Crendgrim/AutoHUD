package mod.crend.autohud.mixin.mod.dehydration;

import mod.crend.autohud.compat.DehydrationCompat;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Hud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class DehydrationMixin {
    @Mixin(value = InGameHud.class, priority = 900)
    public static class EarlyMixin {
        @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 1))
        private void preThirst(MatrixStack matrices, CallbackInfo ci) {
            Hud.postInject(matrices);
            Hud.preInject(matrices, DehydrationCompat.Thirst);
        }
    }
    @Mixin(value = InGameHud.class, priority = 1200)
    public static class LateMixin {
        @Inject(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 1))
        private void postThirst(MatrixStack matrices, CallbackInfo ci) {
            Hud.postInject(matrices);
            Hud.preInject(matrices, Component.Hunger);
        }
    }
}
