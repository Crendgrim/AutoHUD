package mod.crend.autohud.mixin.render;


import mod.crend.autohud.component.Hud;
import net.minecraft.client.model.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ModelPart.class)
public class ModelPartMixin {
    /*
    This causes banners to be off-colour, but not actually transparent.
    Might mean the texture is transparent but the model remains solid?
     */
    @ModifyVariable(method="render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V", at=@At("HEAD"), ordinal = 3, argsOnly = true)
    public float autoHud$render(float alpha) {
        return Hud.inRender ? Hud.alpha * alpha : alpha;
    }
}
