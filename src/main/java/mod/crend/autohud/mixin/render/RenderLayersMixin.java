package mod.crend.autohud.mixin.render;

import mod.crend.autohud.component.Hud;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderLayers.class)
public class RenderLayersMixin {
    // BlockItem transparency support
    @Redirect(method="getEntityBlockLayer", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/render/TexturedRenderLayers;getEntityCutout()Lnet/minecraft/client/render/RenderLayer;"))
    private static RenderLayer autoHud$getEntityCutout() {
        return (Hud.inRender ? TexturedRenderLayers.getItemEntityTranslucentCull() : TexturedRenderLayers.getEntityCutout());
    }
}
