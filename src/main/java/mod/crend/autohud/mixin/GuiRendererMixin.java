//? if >=1.21.6 {
/*package mod.crend.autohud.mixin;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.sugar.Local;
import mod.crend.autohud.render.AutoHudRenderer;
import mod.crend.autohud.render.RenderWrapper;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.SpecialGuiElementRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.special.SpecialGuiElementRenderState;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {
	/^
	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
	public void autoHud$renderWrapper(GuiRenderState state, VertexConsumerProvider.Immediate vertexConsumers, List<?> specialElementRenderers, CallbackInfo ci, @Local ImmutableMap.Builder<Class<? extends SpecialGuiElementRenderState>, SpecialGuiElementRenderer<?>> builder) {
		builder.put(RenderWrapper.RenderWrapperState.class, new RenderWrapper(vertexConsumers));
	}
	^/

	@ModifyArg(
			method = "prepareItemInitially",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderState;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V"),
			index = 2
	)
	public int autoHud$transparentItem(int light) {
		return AutoHudRenderer.modifyArgb(light);
	}
}
*///?}