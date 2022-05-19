package mod.crend.autohud.mixin;

import mod.crend.autohud.component.Component;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method="setAir", at=@At("TAIL"))
    public void autohud$setAir(int air, CallbackInfo ci) {
        if (((Object) this) instanceof ClientPlayerEntity) {
            Component.Air.updateState();
        }
    }
}
