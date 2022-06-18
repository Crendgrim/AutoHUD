package mod.crend.autohud.mixin.render;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.config.AnimationType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Function;

@Mixin(ConduitBlockEntityRenderer.class)
public class ConduitBlockEntityRendererMixin {

    @ModifyArg(
            method="render(Lnet/minecraft/block/entity/ConduitBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at=@At(value = "INVOKE", target = "Lnet/minecraft/client/util/SpriteIdentifier;getVertexConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Ljava/util/function/Function;)Lnet/minecraft/client/render/VertexConsumer;")
    )
    private Function<Identifier, RenderLayer> autoHud$getVertexConsumer(Function<Identifier, RenderLayer> layerFactory) {
        return AutoHud.config.animationType() == AnimationType.Fade ? RenderLayer::getEntityTranslucentCull : layerFactory;
    }

}
