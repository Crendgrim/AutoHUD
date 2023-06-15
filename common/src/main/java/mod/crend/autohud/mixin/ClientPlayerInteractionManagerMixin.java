package mod.crend.autohud.mixin;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.Component;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    @Inject(method="syncSelectedSlot", at=@At(value = "INVOKE", target="Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"))
    private void autoHud$revealOnSlotChange(CallbackInfo ci) {
        if (AutoHud.config.isHotbarOnSlotChange()) {
            Component.Hotbar.revealCombined();
        }
    }
}
