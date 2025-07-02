//? if drg_flares {
package mod.crend.autohud.compat.mixin.drg_flares;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import me.lizardofoz.drgflares.client.FlareHUDRenderer;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FlareHUDRenderer.class, remap = false)
@MixinEnvironment(value = "compat", type = MixinEnvironment.Env.CLIENT)
public class FlareHUDRendererMixin {

	@Inject(method = "render", at=@At("HEAD"))
	private static void autoHud$preRender(DrawContext context, float tickDelta, CallbackInfo ci) {
		ComponentRenderer.HOTBAR.beginRender(context);
	}

	@Inject(method = "render", at=@At("RETURN"))
	private static void autoHud$postRender(DrawContext context, float tickDelta, CallbackInfo ci) {
		ComponentRenderer.HOTBAR.endRender(context);
	}
}
//?}
