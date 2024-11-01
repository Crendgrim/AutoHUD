package mod.crend.autohud.compat.mixin.onebar;

//? if onebar {
import io.github.madis0.OneBarElements;
import mod.crend.autohud.compat.OneBarCompat;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.gui.DrawContext;
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
	private DrawContext drawContext;

	@Inject(method = "renderOneBar", at=@At("HEAD"))
	private void autoHud$preRenderOneBar(CallbackInfo ci) {
		AutoHudRenderer.preInject(drawContext, OneBarCompat.OneBarComponent);
	}

	@Inject(method = "xpBar", at=@At("HEAD"))
	private void autoHud$preXpBar(CallbackInfo ci) {
		AutoHudRenderer.postInject(drawContext);
		AutoHudRenderer.preInject(drawContext, Component.ExperienceBar);
	}

	@Inject(method = "xpBar", at=@At("TAIL"))
	private void autoHud$postXpBar(CallbackInfo ci) {
		AutoHudRenderer.postInject(drawContext);
		AutoHudRenderer.preInject(drawContext, OneBarCompat.OneBarComponent);
	}

	@Inject(method = "renderOneBar", at=@At("TAIL"))
	private void autoHud$postRenderOneBar(CallbackInfo ci) {
		AutoHudRenderer.postInject(drawContext);
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
//?} else {
/*import mod.crend.libbamboo.VersionUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = VersionUtils.class, remap = false)
public class OneBarElementsMixin {
}
*///?}
