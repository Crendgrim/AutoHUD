package mod.crend.autohud.forge.mixin.gui;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Hud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeGui.class)
public class ForgeGuiMixin {

	@Inject(method = "render", at = @At("HEAD"))
	private void autoHud$preRender(MatrixStack matrixStack, float tickDelta, CallbackInfo ci) {
		Hud.inRender = true;
		Hud.tickDelta = tickDelta;
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void autoHud$postRender(MatrixStack matrixStack, float tickDelta, CallbackInfo ci) {
		Hud.inRender = false;
	}

	@Inject(method = "renderAir", at  = @At("HEAD"))
	private void autoHud$preAir(int width, int height, MatrixStack matrixStack, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			Hud.preInject(matrixStack, Component.Air);
		}
	}
	@Inject(method = "renderAir", at  = @At("RETURN"))
	private void autoHud$postAir(int width, int height, MatrixStack matrixStack, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			Hud.postInject(matrixStack);
		}
	}

	@Inject(method = "renderHealthMount", at  = @At("HEAD"))
	private void autoHud$preRenderHealthMount(int width, int height, MatrixStack matrixStack, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			Hud.preInject(matrixStack, Component.MountHealth);
		}
	}
	@Inject(method = "renderHealthMount", at  = @At("RETURN"))
	private void autoHud$postRenderHealthMount(int width, int height, MatrixStack matrixStack, CallbackInfo ci) {
		if (AutoHud.targetStatusBars) {
			Hud.postInject(matrixStack);
		}
	}

}
