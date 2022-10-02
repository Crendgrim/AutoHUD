package mod.crend.autohud.compat.mixin.essentialgui;

import mod.crend.autohud.component.Hud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class EssentialGuiMixin {
	@Inject(method="render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHeldItemTooltip(Lnet/minecraft/client/util/math/MatrixStack;)V",
					shift = At.Shift.AFTER
			))
	private void autoHud$essentialGuiCompatRenderHeldItemTooltip(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
		Hud.postInject(matrices);
	}
}
