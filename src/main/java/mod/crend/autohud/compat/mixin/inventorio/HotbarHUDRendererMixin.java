//? if inventorio {
package mod.crend.autohud.compat.mixin.inventorio;

import de.rubixdev.inventorio.client.ui.HotbarHUDRenderer;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HotbarHUDRenderer.class, remap = false)
@MixinEnvironment(value = "compat", type = MixinEnvironment.Env.CLIENT)
public class HotbarHUDRendererMixin {
	@Inject(method = "renderSegmentedHotbar", at=@At("HEAD"))
	void autoHud$preHotbar(DrawContext context, CallbackInfoReturnable<Boolean> cir) {
		ComponentRenderer.HOTBAR.beginRender(context);
	}
	@Inject(method = "renderSegmentedHotbar", at=@At("RETURN"))
	void autoHud$postHotbar(DrawContext context, CallbackInfoReturnable<Boolean> cir) {
		ComponentRenderer.HOTBAR.endRender(context);
	}

	@Inject(method = "renderHotbarAddons", at=@At("HEAD"))
	void autoHud$preHotbarAddons(DrawContext context, CallbackInfo ci) {
		ComponentRenderer.HOTBAR.beginRender(context);
	}
	@Inject(method = "renderHotbarAddons", at=@At("RETURN"))
	void autoHud$postHotbarAddons(DrawContext context, CallbackInfo ci) {
		ComponentRenderer.HOTBAR.endRender(context);
	}
}
//?}
