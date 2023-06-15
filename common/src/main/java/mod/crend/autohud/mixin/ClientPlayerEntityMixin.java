package mod.crend.autohud.mixin;

import com.mojang.authlib.GameProfile;
import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import mod.crend.autohud.component.state.PolicyComponentState;
import mod.crend.autohud.config.RevealPolicy;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "updateHealth", at = @At("TAIL"))
    private void autoHud$updateHealth(float health, CallbackInfo ci) {
        Component.Health.updateState();
    }
    @Inject(method = "setExperience", at = @At("TAIL"))
    private void autoHud$setExperience(float progress, int total, int level, CallbackInfo ci) {
        Component.ExperienceBar.updateState();
    }


    // Jumpbar
    @Shadow public Input input;
    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getVehicle()Lnet/minecraft/entity/Entity;"))
    private void autoHud$jumpBarChanged(CallbackInfo ci){
        if (this.input.jumping && AutoHud.config.mountJumpBar().onChange()) Component.MountJumpBar.revealNow();
    }

    // Mount health
    @Inject(method = "tickRiding", at = @At("RETURN"))
    private void autoHud$mountHealthChange(CallbackInfo ci){
        ClientPlayerEntity thisPlayer = (ClientPlayerEntity) (Object) this;
        if (AutoHud.config.mountHealth().policy() != RevealPolicy.Disabled && thisPlayer.getVehicle() instanceof LivingEntity vehicle) {
            if (Component.MountHealth.state == null) {
                Component.MountHealth.state = new PolicyComponentState(Component.MountHealth, () -> (int) vehicle.getHealth(), () -> (int) vehicle.getMaxHealth());
                Component.MountHealth.revealCombined();
            } else {
                Component.MountHealth.updateState();
            }
        } else {
            Component.MountHealth.state = null;
        }
    }
}
