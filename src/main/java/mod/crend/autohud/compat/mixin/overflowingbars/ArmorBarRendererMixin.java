//? if overflowing_bars {
package mod.crend.autohud.compat.mixin.overflowingbars;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import mod.crend.autohud.render.AutoHudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//? if overflowing_bars: <21 {
import fuzs.overflowingbars.client.handler.ArmorBarRenderer;
 //?} else
/*import fuzs.overflowingbars.client.gui.ArmorBarRenderer;*/

@Mixin(value = ArmorBarRenderer.class, remap = false)
@MixinEnvironment(value = "compat", type = MixinEnvironment.Env.CLIENT)
public class ArmorBarRendererMixin {
	@WrapOperation(
			//? if overflowing_bars: <21 {
			method = "renderArmorBar(Lnet/minecraft/client/gui/DrawContext;IIIIZZLfuzs/overflowingbars/config/ClientConfig$ArmorRowConfig;)V",
			//?} else
			/*method = "renderArmorBar(Lnet/minecraft/client/gui/DrawContext;IIIIZZLfuzs/overflowingbars/config/ClientConfig$AbstractArmorRowConfig;)V",*/
			at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V")
	)
	private static void autoHud$insertAlpha(float r, float g, float b, float a, Operation<Void> original) {
		original.call(r, g, b, a * AutoHudRenderer.alpha);
	}
}
//?}
