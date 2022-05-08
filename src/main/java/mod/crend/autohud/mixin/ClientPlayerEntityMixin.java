package mod.crend.autohud.mixin;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.StatState;
import mod.crend.autohud.config.RevealPolicy;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    // Jumpbar
    @Shadow public Input input;
    @Inject( method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getVehicle()Lnet/minecraft/entity/Entity;") )
    private void jumpBarChanged(CallbackInfo ci){
        if (this.input.jumping && AutoHud.config.revealOnMountJump()) Component.MountJumpBar.revealNow();
    }

    // Mount health
    @Unique
    private StatState mountHealth;
    @Inject( method = "tickRiding", at = @At(value = "RETURN") )
    private void mountHealthChange(CallbackInfo ci){
        ClientPlayerEntity thisPlayer = (ClientPlayerEntity) (Object) this;
        if (AutoHud.config.onMountHealthChange() != RevealPolicy.Disabled && thisPlayer.getVehicle() instanceof LivingEntity vehicle) {
            if (mountHealth == null) {
                mountHealth = new StatState(Component.MountHealth, (int) vehicle.getHealth(), (int) vehicle.getMaxHealth());
            }
            mountHealth.changeConditional((int) vehicle.getHealth(), AutoHud.config.onMountHealthChange());
        } else {
            mountHealth = null;
        }
    }
}
