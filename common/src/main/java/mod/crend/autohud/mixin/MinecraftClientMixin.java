package mod.crend.autohud.mixin;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow
	public HitResult crosshairTarget;

	@Inject(
			method = "doAttack",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;", shift = At.Shift.BEFORE)
	)
	private void autoHud$doAttack(CallbackInfoReturnable<Boolean> cir) {
		if (AutoHud.config.onSwinging()
				|| (AutoHud.config.onAttacking() && crosshairTarget.getType() == HitResult.Type.ENTITY)
		) {
			Component.revealAll();
		}
	}
}
