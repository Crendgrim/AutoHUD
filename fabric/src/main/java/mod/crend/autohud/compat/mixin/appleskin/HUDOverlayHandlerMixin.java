package mod.crend.autohud.compat.mixin.appleskin;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squeek.appleskin.client.HUDOverlayHandler;

@Mixin(value = HUDOverlayHandler.class, remap = false)
public class HUDOverlayHandlerMixin {

	@Inject(
			//? if <1.20.5 {
			method = "onPreRender",
			//?} else
			/*method = "onPreRenderFood",*/
			at = @At("TAIL")
	)
	void autoHud$injectTransparency(
			DrawContext context,
			//? if >=1.20.5
			/*PlayerEntity player, int top, int right,*/
			CallbackInfo ci
	) {
		if (AutoHud.config.animationFade()) {
			AutoHudRenderer.preInjectFade(Component.Hunger, context);
		}
	}

	//? if <1.21.2 {
	@ModifyVariable(method = "enableAlpha", at=@At("HEAD"), argsOnly = true)
	float autoHud$modifyAlpha(float alpha) {
		return AutoHudRenderer.inRender ? AutoHudRenderer.alpha * alpha : alpha;
	}
	//?}
}
