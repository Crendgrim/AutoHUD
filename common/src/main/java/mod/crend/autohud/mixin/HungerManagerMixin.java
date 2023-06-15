package mod.crend.autohud.mixin;

import mod.crend.autohud.component.Component;
import net.minecraft.entity.player.HungerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class HungerManagerMixin {
    @Inject(method="setFoodLevel", at=@At("TAIL"))
    private void autoHud$setFoodLevel(int foodLevel, CallbackInfo ci) {
        Component.Hunger.updateState();
    }
}
