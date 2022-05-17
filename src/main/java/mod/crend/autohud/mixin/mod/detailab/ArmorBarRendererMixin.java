package mod.crend.autohud.mixin.mod.detailab;

import com.redlimerl.detailab.render.ArmorBarRenderer;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Hud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorBarRenderer.class)
public class ArmorBarRendererMixin {
    @Inject(method="render", at=@At(value = "HEAD"))
    private void preRender(MatrixStack matrices, PlayerEntity player, CallbackInfo ci) {
        Hud.preInject(matrices, Component.Armor);
    }
    @Inject(method="render", at=@At(value = "TAIL"))
    private void postRender(MatrixStack matrices, PlayerEntity player, CallbackInfo ci) {
        Hud.postInject(matrices);
    }
}
