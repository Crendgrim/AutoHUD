//? if >=1.21.6 {
/*package mod.crend.autohud.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Components;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.autohud.render.ItemGuiElementRenderStateAccessor;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.ItemGuiElementRenderState;
import net.minecraft.client.gui.render.state.TexturedQuadGuiElementRenderState;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiRenderer.class)
@MixinEnvironment(type = MixinEnvironment.Env.CLIENT)
public class GuiRendererMixin {

	@SuppressWarnings("ConstantConditions") // IntelliJ is schizophrenic, the cast works fine
	@WrapOperation(method = "prepareItem", at = @At(value = "NEW", target = "net/minecraft/client/gui/render/state/TexturedQuadGuiElementRenderState"))
	TexturedQuadGuiElementRenderState autoHud$transparentItem(
			RenderPipeline pipeline,
			TextureSetup textureSetup,
			Matrix3x2f pose,
			int x1,
			int y1,
			int x2,
			int y2,
			float u1,
			float u2,
			float v1,
			float v2,
			int color,
			@Nullable ScreenRect scissorArea,
			@Nullable ScreenRect bounds,
			Operation<TexturedQuadGuiElementRenderState> original,
			@Local(argsOnly = true) ItemGuiElementRenderState itemRenderState
	) {
		if ((Object) itemRenderState instanceof ItemGuiElementRenderStateAccessor accessor
				&& accessor.autohud$isHudItem() && Components.Hotbar.alpha < 1) {
			float alpha = Math.max(Components.Hotbar.getAlpha(AutoHudRenderer.tickDelta), AutoHud.config.getHotbarItemsMaximumFade());
			color = ColorHelper.withAlpha(Math.round(ColorHelper.getAlpha(color) * alpha), color);
			pipeline = RenderPipelines.GUI_TEXTURED;
		}
		return original.call(pipeline, textureSetup, pose, x1, y1, x2, y2, u1, u2, v1, v2, color, scissorArea, bounds);
	}

}
*///?}
