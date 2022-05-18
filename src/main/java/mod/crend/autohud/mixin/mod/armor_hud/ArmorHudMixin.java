package mod.crend.autohud.mixin.mod.armor_hud;

import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.Hud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InGameHud.class, priority = 1200)
public class ArmorHudMixin {
    @Dynamic("added by BerdinskiyBears Armor Hud")
    @Inject(method="drawSlots1", at=@At(value = "HEAD"))
    private void preDrawSlots1(MatrixStack matrices, int y, int x, int w, int l, CallbackInfo ci) {
        Hud.preInject(matrices, Component.Hotbar);
    }
    @Dynamic("added by BerdinskiyBears Armor Hud")
    @Inject(method="drawSlots1", at=@At(value = "TAIL"))
    private void postDrawSlots1(MatrixStack matrices, int y, int x, int w, int l, CallbackInfo ci) {
        Hud.postInject(matrices);
    }

    @Dynamic("added by BerdinskiyBears Armor Hud")
    @Inject(method="drawSlots2", at=@At(value = "HEAD"))
    private void preDrawSlots2(MatrixStack matrices, int y, int x, int w, int l, CallbackInfo ci) {
        Hud.preInject(matrices, Component.Hotbar);
    }
    @Dynamic("added by BerdinskiyBears Armor Hud")
    @Inject(method="drawSlots2", at=@At(value = "TAIL"))
    private void postDrawSlots2(MatrixStack matrices, int y, int x, int w, int l, CallbackInfo ci) {
        Hud.postInject(matrices);
    }
}
