package mod.crend.autohud.mixin.render;

import mod.crend.autohud.component.Hud;
import net.minecraft.client.render.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(VertexConsumer.class)
public interface VertexConsumerMixin {
    @ModifyArg(
            method = "quad(Lnet/minecraft/client/util/math/MatrixStack$Entry;Lnet/minecraft/client/render/model/BakedQuad;[FFFF[IIZ)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumer;vertex(FFFFFFFFFIIFFF)V"),
            index = 6
    )
    default float autoHud$quad(float alpha) {
        // Transparency level
        return (Hud.inRender ? Hud.alpha * alpha : alpha);
    }
}
