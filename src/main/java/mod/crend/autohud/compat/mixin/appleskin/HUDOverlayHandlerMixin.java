package mod.crend.autohud.compat.mixin.appleskin;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squeek.appleskin.client.HUDOverlayHandler;

@Mixin(value = HUDOverlayHandler.class, remap = false)
public class HUDOverlayHandlerMixin {

	@Inject(method = "onPreRender", at=@At("TAIL"))
	void autoHud$injectTransparency(DrawContext drawContext, CallbackInfo ci) {
		if (AutoHud.config.animationFade()) {
			AutoHudRenderer.preInjectFade(Component.Hunger);
		}
	}

	@ModifyVariable(method = "enableAlpha", at=@At("HEAD"), argsOnly = true)
	float autoHud$modifyAlpha(float alpha) {
		return AutoHudRenderer.inRender ? AutoHudRenderer.alpha * alpha : alpha;
	}
}
