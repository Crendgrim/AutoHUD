//? if simplyskills {
package mod.crend.autohud.compat.mixin.simplyskills;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.gui.DrawContext;
import net.sweenus.simplyskills.client.gui.CustomHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CustomHud.class, remap = false)
@MixinEnvironment(value = "compat", type = MixinEnvironment.Env.CLIENT)
public class CustomHudMixin {
	@Inject(method = "render", at= @At("HEAD"))
	void autoHud$preRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		ComponentRenderer.HOTBAR.beginRender(context);
	}
	@Inject(method = "render", at= @At("RETURN"))
	void autoHud$postRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		ComponentRenderer.HOTBAR.endRender(context);
	}
}
//?}
