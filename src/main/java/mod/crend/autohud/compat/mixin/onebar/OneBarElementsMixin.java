//? if onebar {
package mod.crend.autohud.compat.mixin.onebar;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.madis0.OneBarElements;
import mod.crend.autohud.compat.OneBarCompat;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = OneBarElements.class, remap = false)
public abstract class OneBarElementsMixin {
	@Shadow
	@Final
	private DrawContext drawContext;

	@WrapMethod(method = "renderOneBar")
	private void autoHud$wrapOneBar(Operation<Void> original) {
		OneBarCompat.ONE_BAR_WRAPPER.wrap(drawContext, () -> original.call());
	}

	@WrapOperation(method = "renderOneBar", at = @At(value = "INVOKE", target = "Lio/github/madis0/OneBarElements;xpBar()V"))
	private void autoHud$wrapXpBar(OneBarElements instance, Operation<Void> original) {
		OneBarCompat.ONE_BAR_WRAPPER.endRender(drawContext);
		ComponentRenderer.EXPERIENCE_BAR.wrap(drawContext, () -> original.call(instance));
		OneBarCompat.ONE_BAR_WRAPPER.beginRender(drawContext);
	}

	@ModifyVariable(method = "renderBar", at = @At("HEAD"), ordinal = 4, argsOnly = true)
	private int autoHud$alpha(int color) {
		return AutoHudRenderer.modifyArgb(color);
	}

	@ModifyArg(
			method = "barText",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;IIIZ)I"),
			index = 4
	)
	private int autoHud$barTextAlpha(int color) {
		return AutoHudRenderer.modifyArgb(color);
	}
	@ModifyArg(
			method = "xpBar",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)I"),
			index = 4
	)
	private int autoHud$xpBarTextAlpha(int color) {
		return AutoHudRenderer.modifyArgb(color);
	}
	@ModifyArg(
			method = "xpBar",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawCenteredTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"),
			index = 4
	)
	private int autoHud$xpBarTextCenteredAlpha(int color) {
		return AutoHudRenderer.modifyArgb(color);
	}
}
//?}
