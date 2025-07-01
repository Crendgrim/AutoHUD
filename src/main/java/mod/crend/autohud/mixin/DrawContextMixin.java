//? if >=1.21.6 {
/*package mod.crend.autohud.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import mod.crend.autohud.render.AutoHudRenderer;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DrawContext.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class DrawContextMixin {

	@ModifyArg(
			method = "fill(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/texture/TextureSetup;IIIIILjava/lang/Integer;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/state/ColoredQuadGuiElementRenderState;<init>(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/texture/TextureSetup;Lorg/joml/Matrix3x2f;IIIIIILnet/minecraft/client/gui/ScreenRect;)V"),
			index = 7
	)
	public int autoHud$transparentFill(int color) {
		return AutoHudRenderer.modifyArgb(color);
	}
	@ModifyArg(
			method = "fill(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/texture/TextureSetup;IIIIILjava/lang/Integer;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/state/ColoredQuadGuiElementRenderState;<init>(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/texture/TextureSetup;Lorg/joml/Matrix3x2f;IIIIIILnet/minecraft/client/gui/ScreenRect;)V"),
			index = 8
	)
	public int autoHud$transparentFillGradient(int color2) {
		return AutoHudRenderer.modifyArgb(color2);
	}

	@ModifyArg(
			method = "drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;IIIZ)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/state/TextGuiElementRenderState;<init>(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;Lorg/joml/Matrix3x2f;IIIIZLnet/minecraft/client/gui/ScreenRect;)V"),
			index = 5
	)
	public int autoHud$transparentText(int color) {
		return AutoHudRenderer.modifyArgb(color);
	}

	@ModifyArg(
			method = "drawTexturedQuad(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lcom/mojang/blaze3d/textures/GpuTextureView;IIIIFFFFI)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/state/TexturedQuadGuiElementRenderState;<init>(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/client/texture/TextureSetup;Lorg/joml/Matrix3x2f;IIIIFFFFILnet/minecraft/client/gui/ScreenRect;)V"),
			index = 11
	)
	public int autoHud$transparentQuad(int color) {
		int newcolor = AutoHudRenderer.modifyArgb(color);
		return AutoHudRenderer.modifyArgb(color);
	}

}
*///?}
