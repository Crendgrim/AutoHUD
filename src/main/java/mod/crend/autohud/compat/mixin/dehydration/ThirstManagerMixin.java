package mod.crend.autohud.compat.mixin.dehydration;

import mod.crend.autohud.compat.DehydrationCompat;
import net.dehydration.thirst.ThirstManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThirstManager.class)
public class ThirstManagerMixin {
    @Inject(method="setThirstLevel", at=@At("TAIL"), remap=false)
    private void autoHud$setThirstLevel(int thirstLevel, CallbackInfo ci) {
        DehydrationCompat.Thirst.updateState();
    }
}
