package mod.crend.autohud.compat.mixin.armorchroma;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.libbamboo.render.CustomFramebufferRenderer;
import net.minecraft.client.gui.DrawContext;
import nukeduck.armorchroma.GuiArmor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiArmor.class, remap = false)
public class GuiArmorMixin {
	@Inject(method = "draw", at=@At("HEAD"))
	void autoHud$preArmor(DrawContext context, int left, int top, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.postInject(context);
			if (AutoHud.config.animationFade()) {
				CustomFramebufferRenderer.init();
			}
			AutoHudRenderer.preInject(context, Component.Armor);
		}
	}
	@Inject(method = "draw", at=@At("RETURN"))
	void autoHud$postArmor(DrawContext context, int left, int top, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			if (AutoHud.config.animationFade()) {
				AutoHudRenderer.preInjectFade(Component.Armor);
				CustomFramebufferRenderer.draw(context);
			}
			AutoHudRenderer.postInject(context);
			AutoHudRenderer.preInject(context, Component.Health);
		}
	}
}
