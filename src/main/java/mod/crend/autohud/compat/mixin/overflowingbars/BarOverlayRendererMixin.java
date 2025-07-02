//? if overflowing_bars {
package mod.crend.autohud.compat.mixin.overflowingbars;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import mod.crend.autohud.compat.OverflowingBarsCompat;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.autohud.render.ComponentRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if overflowing_bars: <21 {
import fuzs.overflowingbars.client.handler.BarOverlayRenderer;
//?} else
/*import fuzs.overflowingbars.client.gui.BarOverlayRenderer;*/

@Mixin(value = BarOverlayRenderer.class, remap = false)
@MixinEnvironment(value = "compat", type = MixinEnvironment.Env.CLIENT)
public class BarOverlayRendererMixin {
	@Inject(method = "renderHealthLevelBars", at = @At("HEAD"))
	private static void autoHud$preHealth(
			//? if overflowing_bars: >=21
			/*MinecraftClient minecraft,*/
			DrawContext context,
			//? if overflowing_bars: <21
			int screenWidth, int screenHeight, MinecraftClient client,
			int leftHeight, boolean rowCount, CallbackInfo ci
	) {
		//? if fabric && overflowing_bars: >=21 {
		/*ComponentRenderer.HEALTH.beginRender(context);
		*///?} else
		ComponentRenderer.HEALTH.beginFade(context);
	}
	@Inject(method = "renderHealthLevelBars", at = @At("RETURN"))
	private static void autoHud$postHealth(
		//? if overflowing_bars: >=21
		/*MinecraftClient minecraft,*/
		DrawContext context,
		//? if overflowing_bars: <21
		int screenWidth, int screenHeight, MinecraftClient client,
		int leftHeight, boolean rowCount, CallbackInfo ci
	) {
		//? if fabric && overflowing_bars: >=21 {
		/*ComponentRenderer.HEALTH.endRender(context);
		*///?} else
		ComponentRenderer.HEALTH.endFade(context);
	}

	//? if fabric {
	@Inject(method = "renderArmorLevelBar", at = @At("HEAD"))
	private static void autoHud$preArmor(
			//? if overflowing_bars: >=21
			/*MinecraftClient minecraft,*/
			DrawContext context,
			//? if overflowing_bars: <21
			int screenWidth, int screenHeight, MinecraftClient client,
			int leftHeight, boolean rowCount, boolean unmodified, CallbackInfo ci
	) {
		ComponentRenderer.ARMOR.beginRender(context);
	}
	@Inject(method = "renderArmorLevelBar", at = @At("RETURN"))
	private static void autoHud$postArmor(
		//? if overflowing_bars: >=21
		/*MinecraftClient minecraft,*/
		DrawContext context,
		//? if overflowing_bars: <21
		int screenWidth, int screenHeight, MinecraftClient client,
		int leftHeight, boolean rowCount, boolean unmodified, CallbackInfo ci
	) {
		ComponentRenderer.ARMOR.endRender(context);
	}
	//?}

	@Inject(method = "renderToughnessLevelBar", at = @At("HEAD"))
	private static void autoHud$preToughness(
		//? if overflowing_bars: >=21
		/*MinecraftClient minecraft,*/
		DrawContext context,
		//? if overflowing_bars: <21
		int screenWidth, int screenHeight, MinecraftClient client,
		int guiHeight, boolean rowCount, boolean leftSide, boolean unmodified, CallbackInfo ci
	) {
		OverflowingBarsCompat.TOUGHNESS_RENDERER.beginRender(context);
	}
	@Inject(method = "renderToughnessLevelBar", at = @At("RETURN"))
	private static void autoHud$postToughness(
		//? if overflowing_bars: >=21
		/*MinecraftClient minecraft,*/
		DrawContext context,
		//? if overflowing_bars: <21
		int screenWidth, int screenHeight, MinecraftClient client,
		int guiHeight, boolean rowCount, boolean leftSide, boolean unmodified, CallbackInfo ci
	) {
		OverflowingBarsCompat.TOUGHNESS_RENDERER.endRender(context);
	}

	@WrapOperation(method = "resetRenderState", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"))
	private static void autoHud$insertAlpha(float r, float g, float b, float a, Operation<Void> original) {
		original.call(r, g, b, a * AutoHudRenderer.alpha);
	}
}
//?}
