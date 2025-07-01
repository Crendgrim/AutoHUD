package mod.crend.autohud.compat.mixin.armor_hud;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InGameHud.class, priority = 1200, remap = false)
@MixinEnvironment(value = "compat", type = MixinEnvironment.Env.CLIENT)
public class ArmorHudMixin {
	@Dynamic("added by BerdinskiyBears Armor Hud")
	@Inject(method="drawSlots1", at=@At("HEAD"))
	private void autoHud$preDrawSlots1(DrawContext context, int y, int x, int w, int l, CallbackInfo ci) {
		ComponentRenderer.HOTBAR.beginRender(context);
	}
	@Dynamic("added by BerdinskiyBears Armor Hud")
	@Inject(method="drawSlots1", at=@At("TAIL"))
	private void autoHud$postDrawSlots1(DrawContext context, int y, int x, int w, int l, CallbackInfo ci) {
		ComponentRenderer.HOTBAR.endRender(context);
	}

	@Dynamic("added by BerdinskiyBears Armor Hud")
	@Inject(method="drawSlots2", at=@At("HEAD"))
	private void autoHud$preDrawSlots2(DrawContext context, int y, int x, int w, int l, CallbackInfo ci) {
		ComponentRenderer.HOTBAR.beginRender(context);
	}
	@Dynamic("added by BerdinskiyBears Armor Hud")
	@Inject(method="drawSlots2", at=@At("TAIL"))
	private void autoHud$postDrawSlots2(DrawContext context, int y, int x, int w, int l, CallbackInfo ci) {
		ComponentRenderer.HOTBAR.endRender(context);
	}
}
