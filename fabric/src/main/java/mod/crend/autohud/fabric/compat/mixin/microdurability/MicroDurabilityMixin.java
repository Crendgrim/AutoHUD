package mod.crend.autohud.fabric.compat.mixin.microdurability;

import com.github.reviversmc.microdurability.RendererBase;
import com.mojang.blaze3d.systems.RenderSystem;
import mod.crend.autohud.fabric.compat.MicroDurabilityCompat;
import mod.crend.autohud.component.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = RendererBase.class, priority = 1500)
public class MicroDurabilityMixin {
    @ModifyArg(method = "onHudRender", at=@At(value = "INVOKE", target = "Lcom/github/reviversmc/microdurability/RendererBase;renderBar(Lnet/minecraft/item/ItemStack;II)V"), index = 2)
    private int autoHud$offsetRenderBar(int y) {
        return y + (int) MicroDurabilityCompat.MicroDurabilityComponent.getOffsetY(Hud.tickDelta);
    }

    @ModifyArg(
            method = "renderBar",
            at=@At(value = "INVOKE", target = "Lcom/github/reviversmc/microdurability/RendererBase;renderGuiQuad(Lnet/minecraft/client/render/BufferBuilder;IIIIIIII)V"),
            index=8
    )
    int autoHud$injectAlpha(int alpha) {
        if (Hud.inRender) {
            RenderSystem.enableBlend();
            return Math.round(MicroDurabilityCompat.MicroDurabilityComponent.getAlpha(Hud.tickDelta) * alpha);
        }
        return alpha;
    }
}
