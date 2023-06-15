package mod.crend.autohud.forge.mixin.gui;

import mod.crend.autohud.component.Component;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeGui.class)
public class ForgeGuiMixin {

	@Inject(method = "render", at = @At("HEAD"))
	private void autoHud$preRender(DrawContext context, float tickDelta, CallbackInfo ci) {
		AutoHudRenderer.inRender = true;
		AutoHudRenderer.tickDelta = tickDelta;
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void autoHud$postRender(DrawContext context, float tickDelta, CallbackInfo ci) {
		AutoHudRenderer.inRender = false;
	}

	@Inject(method = "renderAir", at  = @At("HEAD"))
	private void autoHud$preAir(int width, int height, DrawContext context, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.preInject(context, Component.Air);
		}
	}
	@Inject(method = "renderAir", at  = @At("RETURN"))
	private void autoHud$postAir(int width, int height, DrawContext context, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.postInject(context);
		}
	}

	@Inject(method = "renderHealthMount", at  = @At("HEAD"))
	private void autoHud$preRenderHealthMount(int width, int height, DrawContext context, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.preInject(context, Component.MountHealth);
		}
	}
	@Inject(method = "renderHealthMount", at  = @At("RETURN"))
	private void autoHud$postRenderHealthMount(int width, int height, DrawContext context, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			AutoHudRenderer.postInject(context);
		}
	}

}
