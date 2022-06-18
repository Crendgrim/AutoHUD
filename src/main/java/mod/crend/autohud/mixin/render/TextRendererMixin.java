package mod.crend.autohud.mixin.render;

import mod.crend.autohud.component.Hud;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextRenderer.class)
public class TextRendererMixin {

    // ItemRenderer overlay
    // Experience number
    // Scoreboard sidebar: String
    @Inject(method = "draw(Ljava/lang/String;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZIIZ)I", at=@At("HEAD"), cancellable = true)
    private void autoHud$draw(String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, boolean seeThrough, int backgroundColor, int light, boolean rightToLeft, CallbackInfoReturnable<Integer> cir) {
        if (Hud.inRender && Hud.alpha <= 0.015) {
            cir.setReturnValue(0);
        }
    }

    // Scoreboard sidebar: Text
    @Inject(method = "draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I", at=@At("HEAD"), cancellable = true)
    private void autoHud$draw(MatrixStack matrices, Text text, float x, float y, int color, CallbackInfoReturnable<Integer> cir) {
        if (Hud.inRender && Hud.alpha <= 0.015) {
            cir.setReturnValue(0);
        }
    }

}
