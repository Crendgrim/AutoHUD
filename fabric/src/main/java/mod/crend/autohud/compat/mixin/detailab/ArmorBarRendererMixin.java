package mod.crend.autohud.compat.mixin.detailab;

import com.redlimerl.detailab.render.ArmorBarRenderer;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ArmorBarRenderer.class, remap = false)
public class ArmorBarRendererMixin {
	@Inject(method = "render", at=@At("HEAD"))
	void autoHud$preRender(DrawContext context, PlayerEntity player, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.preInject(context, Component.Armor);
		}
	}
	@Inject(method = "render", at=@At("RETURN"))
	void autoHud$postRender(DrawContext context, PlayerEntity player, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.postInject(context);
		}
	}
}
