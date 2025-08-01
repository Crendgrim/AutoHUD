//? if detailab {
package mod.crend.autohud.compat.mixin.detailab;

import com.redlimerl.detailab.render.ArmorBarRenderer;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ArmorBarRenderer.class, remap = false)
@MixinEnvironment(value = "compat", type = MixinEnvironment.Env.CLIENT)
public class ArmorBarRendererMixin {
	@Inject(method = "render", at = @At("HEAD"))
	void autoHud$preRender(DrawContext context, PlayerEntity player, CallbackInfo ci) {
		//? if <1.21 {
		ComponentRenderer.ARMOR.beginRender(context);
		//?} else
		/*ComponentRenderer.ARMOR_FADE.beginRender(context);*/
	}
	@Inject(method = "render", at = @At("TAIL"))
	void autoHud$postRender(DrawContext context, PlayerEntity player, CallbackInfo ci) {
		//? if <1.21 {
		ComponentRenderer.ARMOR.endRender(context);
		//?} else
		/*ComponentRenderer.ARMOR_FADE.endRender(context);*/
	}
}
//?}
