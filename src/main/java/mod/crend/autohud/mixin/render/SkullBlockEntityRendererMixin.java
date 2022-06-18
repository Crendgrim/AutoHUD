package mod.crend.autohud.mixin.render;

import mod.crend.autohud.component.Hud;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SkullBlockEntityRenderer.class)
public class SkullBlockEntityRendererMixin {
    @Redirect(method = "getRenderLayer", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntityCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer autoHud$getEntityCutoutNoCull(Identifier texture) {
        return Hud.inRender ? RenderLayer.getEntityTranslucent(texture) : RenderLayer.getEntityCutoutNoCull(texture);
    }
    @Redirect(method = "getRenderLayer", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getEntityCutoutNoCullZOffset(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer autoHud$getEntityCutoutNoCullZOffset(Identifier texture) {
        return Hud.inRender ? RenderLayer.getEntityTranslucent(texture) : RenderLayer.getEntityCutoutNoCullZOffset(texture);
    }
}
