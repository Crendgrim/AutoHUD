package mod.crend.autohud.compat.mixin.detailab;

import com.redlimerl.detailab.render.InGameDrawer;
import mod.crend.autohud.component.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = InGameDrawer.class, remap = false)
public class InGameDrawerMixin {
	@ModifyArg(
			method = "drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIFFIIIILjava/awt/Color;Z)V",
			at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"),
			index = 3
	)
	private static float autoHud$injectAlpha(float alpha) {
		return (Hud.inRender ? Hud.alpha * alpha : alpha);
	}
}
