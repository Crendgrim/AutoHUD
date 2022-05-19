package mod.crend.autohud.mixin;

import mod.crend.autohud.component.Hud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method="onPlayerRespawn", at=@At("TAIL"))
    private void autoHud$onPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
        Hud.resetState();
    }
}
