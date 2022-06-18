package mod.crend.autohud.mixin.render;

import mod.crend.autohud.component.Hud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method="render", at=@At("HEAD"))
    private void autoHud$preRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        Hud.inRender = true;
    }
    @Inject(method="render", at=@At("RETURN"))
    private void autoHud$postRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        Hud.inRender = false;
    }

    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShader(Ljava/util/function/Supplier;)V", ordinal = 0))
    private void autoHud$preHotbar(float f, MatrixStack matrixStack, CallbackInfo ci) {
        Hud.injectTransparency();
    }

    @ModifyArg(method = "renderExperienceBar", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"))
    private int autoHud$experienceText(int alpha) {
        if (Hud.inRender) {
            return Hud.modifyArgb(alpha);
        }
        return alpha;
    }

    @ModifyArg(method = "renderScoreboardSidebar", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"))
    private int autoHud$scoreboardSidebarString(int color) {
        if (Hud.inRender) {
            return Hud.getArgb() | 0xFFFFFF;
        }
        return color;
    }
    @ModifyArg(method = "renderScoreboardSidebar", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
    private int autoHud$scoreboardSidebarText(int color) {
        if (Hud.inRender) {
            return Hud.getArgb() | 0xFFFFFF;
        }
        return color;
    }
    @ModifyArg(method = "renderScoreboardSidebar", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"), index=5)
    private int autoHud$scoreboardSidebarFill(int color) {
        if (Hud.inRender) {
            return Hud.modifyArgb(color);
        }
        return color;
    }

}
