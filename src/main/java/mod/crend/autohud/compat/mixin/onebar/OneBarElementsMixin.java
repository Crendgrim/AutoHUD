package mod.crend.autohud.compat.mixin.onebar;

import io.github.madis0.OneBarElements;
import mod.crend.autohud.compat.OneBarCompat;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Hud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = OneBarElements.class, remap = false)
public abstract class OneBarElementsMixin {
	@Shadow
	@Final
	private MatrixStack stack;

	@Inject(method = "renderOneBar", at=@At("HEAD"))
	private void autoHud$preRenderOneBar(CallbackInfo ci) {
		if (!stack.isEmpty()) {
			Hud.postInject(stack);
		}
		Hud.preInject(stack, OneBarCompat.OneBarComponent);
	}

	@Inject(method = "xpBar", at=@At("HEAD"))
	private void autoHud$preXpBar(CallbackInfo ci) {
		Hud.postInject(stack);
		Hud.preInject(stack, Component.ExperienceBar);
	}

	@Inject(method = "xpBar", at=@At("TAIL"))
	private void autoHud$postXpBar(CallbackInfo ci) {
		Hud.postInject(stack);
		Hud.preInject(stack, OneBarCompat.OneBarComponent);
	}


	@Inject(method = "renderOneBar", at=@At("TAIL"))
	private void autoHud$postRenderOneBar(CallbackInfo ci) {
		Hud.postInject(stack);
	}
}
