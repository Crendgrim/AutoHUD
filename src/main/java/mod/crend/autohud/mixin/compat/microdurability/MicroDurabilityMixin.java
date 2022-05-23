package mod.crend.autohud.mixin.compat.microdurability;

import com.github.reviversmc.microdurability.RendererBase;
import mod.crend.autohud.compat.MicroDurabilityCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = RendererBase.class, priority = 1500)
public class MicroDurabilityMixin {
    @ModifyArg(method = "onHudRender", at=@At(value = "INVOKE", target = "Lcom/github/reviversmc/microdurability/RendererBase;renderBar(Lnet/minecraft/item/ItemStack;II)V"), index = 2)
    private int autoHud$offsetRenderBar(int y) {
        return y + (int) MicroDurabilityCompat.MicroDurabilityComponent.getDeltaY();
    }
}
