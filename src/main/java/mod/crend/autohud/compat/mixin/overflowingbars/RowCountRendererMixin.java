//? if overflowing_bars {
package mod.crend.autohud.compat.mixin.overflowingbars;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import mod.crend.autohud.render.AutoHudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
//? if overflowing_bars: <21 {
import fuzs.overflowingbars.client.handler.RowCountRenderer;
 //?} else
/*import fuzs.overflowingbars.client.gui.RowCountRenderer;*/

@Mixin(value = RowCountRenderer.class, remap = false)
@MixinEnvironment(value = "compat", type = MixinEnvironment.Env.CLIENT)
public class RowCountRendererMixin {
	@ModifyVariable(method = "drawBorderedSprite", at = @At("HEAD"), argsOnly = true, index = 10)
	private static float autoHud$drawSpriteWithTransparency(float alpha) {
		return alpha * AutoHudRenderer.alpha;
	}

	@ModifyVariable(method = "drawBorderedText", at = @At("HEAD"), argsOnly = true, index = 5)
	private static int autoHud$drawTextWithTransparency(int alpha) {
		return (int) (alpha * AutoHudRenderer.alpha);
	}
}
//?}
