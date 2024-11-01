package mod.crend.autohud.compat.mixin.dehydration;

//? if dehydration {
import mod.crend.autohud.compat.DehydrationCompat;
import net.dehydration.thirst.ThirstManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ThirstManager.class, remap = false)
public class ThirstManagerMixin {
    @Inject(method="setThirstLevel", at=@At("TAIL"))
    private void autoHud$setThirstLevel(int thirstLevel, CallbackInfo ci) {
        DehydrationCompat.Thirst.updateState();
    }

    @Inject(method = "update", at=@At("TAIL"))
    private void autoHud$update(PlayerEntity player, CallbackInfo ci) {
        DehydrationCompat.Thirst.updateState();
    }
}
//?} else {

/*import mod.crend.libbamboo.VersionUtils;
import org.spongepowered.asm.mixin.Mixin;
@Mixin(VersionUtils.class)
public class ThirstManagerMixin {
}
*///?}
