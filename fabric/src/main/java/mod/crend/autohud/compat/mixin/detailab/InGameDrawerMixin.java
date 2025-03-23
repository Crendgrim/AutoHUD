package mod.crend.autohud.compat.mixin.detailab;

//? if detailab {
import com.redlimerl.detailab.render.InGameDrawer;
import mod.crend.autohud.render.AutoHudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = InGameDrawer.class, remap = false)
public class InGameDrawerMixin {
	@ModifyArg(
			//? if <1.21 {
			method = "drawTexture(Lnet/minecraft/client/gui/DrawContext;IIFFIIIILjava/awt/Color;Z)V",
			//?} else
			/*method = "Lcom/redlimerl/detailab/render/InGameDrawer;drawTexture(Lnet/minecraft/util/Identifier;Lnet/minecraft/client/gui/DrawContext;IIFFIIIILjava/awt/Color;Z)V",*/
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"
			),
			index = 3
	)
	private static float autoHud$injectAlpha(float alpha) {
		return (AutoHudRenderer.inRender ? AutoHudRenderer.alpha * alpha : alpha);
	}
}
//?} else {

/*import mod.crend.libbamboo.VersionUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = VersionUtils.class, remap = false)
public class InGameDrawerMixin {
}
*///?}