//? if >=1.21.6 {
/*package mod.crend.autohud.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.text.OrderedText;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DrawContext.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class DrawContextMixin {

	@WrapMethod(
			method = "fill(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/texture/TextureSetup;IIIIILjava/lang/Integer;)V"
	)
	private void autoHud$fillWithTransparency(RenderPipeline pipeline, TextureSetup textureSetup, int x1, int y1, int x2, int y2, int color, Integer color2, Operation<Void> original) {
		if (AutoHudRenderer.inRender) {
			color = ColorHelper.withAlpha(Math.round(ColorHelper.getAlpha(color) * AutoHudRenderer.alpha), color);
			if (color2 != null) {
				color2 = ColorHelper.withAlpha(Math.round(ColorHelper.getAlpha(color2) * AutoHudRenderer.alpha), color2);
			}
		}
		original.call(pipeline, textureSetup, x1, y1, x2, y2, color, color2);
	}

	@WrapMethod(
			method = "drawTexturedQuad(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lcom/mojang/blaze3d/textures/GpuTextureView;IIIIFFFFI)V"
	)
	private void autoHud$quadWithTransparency(RenderPipeline pipeline, GpuTextureView texture, int x1, int y1, int x2, int y2, float u1, float u2, float v1, float v2, int color, Operation<Void> original) {
		if (AutoHudRenderer.inRender) {
			if (pipeline.getLocation().equals(RenderPipelines.CROSSHAIR.getLocation()))
				color = ColorHelper.scaleRgb(color, AutoHudRenderer.alpha);
			else
				color = ColorHelper.withAlpha(Math.round(ColorHelper.getAlpha(color) * AutoHudRenderer.alpha), color);
		}
		original.call(pipeline, texture, x1, y1, x2, y2, u1, u2, v1, v2, color);
	}

	@WrapMethod(
			method = "drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;IIIZ)V"
	)
	private void autoHud$textWithTransparency(TextRenderer textRenderer, OrderedText text, int x, int y, int color, boolean shadow, Operation<Void> original) {
		if (AutoHudRenderer.inRender) {
			color = ColorHelper.withAlpha(Math.round(ColorHelper.getAlpha(color) * AutoHudRenderer.alpha), color);
		}
		original.call(textRenderer, text, x, y, color, shadow);
	}

}
*///?}
