package mod.crend.autohud.mixin.render;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.component.Hud;
import net.minecraft.client.render.item.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @ModifyArg(method="renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at=@At(value="INVOKE", target="Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I"), index=3)
    private int autoHud$itemCount(int color) {
        if (Hud.inRender) {
            return Hud.modifyArgb(color);
        }
        return color;
    }

    @ModifyVariable(method="renderGuiQuad", at=@At("HEAD"), ordinal=7, argsOnly = true)
    private int autoHud$renderQuad(int alpha) {
        if (Hud.inRender) {
            RenderSystem.enableBlend();
            return (int) (Hud.alpha * alpha);
        }
        return alpha;
    }

}
