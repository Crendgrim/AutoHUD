package mod.crend.autohud.mixin.render;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Hud;
import mod.crend.autohud.config.AnimationType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Function;

@Mixin(BannerBlockEntityRenderer.class)
public class BannerBlockEntityRendererMixin {
    @ModifyArg(
            method = "render(Lnet/minecraft/block/entity/BannerBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SpriteIdentifier;getVertexConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Ljava/util/function/Function;)Lnet/minecraft/client/render/VertexConsumer;")
    )
    private Function<Identifier, RenderLayer> autoHud$getVertexConsumer(Function<Identifier, RenderLayer> layerFactory) {
        return AutoHud.config.animationType() == AnimationType.Fade ? RenderLayer::getEntityTranslucent : layerFactory;
    }

    @ModifyArg(
            method = "renderCanvas(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/model/ModelPart;Lnet/minecraft/client/util/SpriteIdentifier;ZLjava/util/List;Z)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SpriteIdentifier;getVertexConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Ljava/util/function/Function;Z)Lnet/minecraft/client/render/VertexConsumer;")
    )
    private static Function<Identifier, RenderLayer> autoHud$getRenderLayer(Function<Identifier, RenderLayer> layerFactory) {
        return Hud.inRender ? RenderLayer::getEntityTranslucent : layerFactory;
    }
}
