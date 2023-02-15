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
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = OneBarElements.class, remap = false)
public abstract class OneBarElementsMixin {
	@Shadow
	@Final
	private MatrixStack stack;

	@Inject(method = "renderOneBar", at=@At("HEAD"))
	private void autoHud$preRenderOneBar(CallbackInfo ci) {
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

	@ModifyVariable(method = "renderBar", at = @At("HEAD"), ordinal = 4, argsOnly = true)
	private int autoHud$alpha(int color) {
		return Hud.modifyArgb(color);
	}

	@ModifyArg(
			method = "barText",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I")
	)
	private int autoHud$barTextAlpha(int color) {
		return Hud.modifyArgb(color);
	}
	@ModifyArg(
			method = "xpBar",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I")
	)
	private int autoHud$xpBarTextAlpha(int color) {
		return Hud.modifyArgb(color);
	}
	@ModifyArg(
			method = "xpBar",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawableHelper;drawCenteredText(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"),
			index = 5
	)
	private int autoHud$xpBarTextCenteredAlpha(int color) {
		return Hud.modifyArgb(color);
	}
}
