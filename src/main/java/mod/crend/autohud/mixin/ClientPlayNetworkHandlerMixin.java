package mod.crend.autohud.mixin;

import mod.crend.autohud.component.ScoreboardHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method="onTeam", at=@At(value="INVOKE", target = "java/util/Optional.ifPresent(Ljava/util/function/Consumer;)V", shift=At.Shift.AFTER))
    private void autoHud$onTeamUpdate(TeamS2CPacket packet, CallbackInfo ci) {
        if (MinecraftClient.getInstance().world == null) {
            return;
        }

        // JuggleStruggle: The reason why both getTeamOperation cannot be REMOVE is because we already perform team removal checks.
        // As for team being present, it... just means that it is required to know that it has been updated.
        if (packet.getTeamOperation() == TeamS2CPacket.Operation.REMOVE) {
            // JuggleStruggle: We need to be sure that we are only calling the rest of the method if, and only if, there's
            // no player listing going on as that takes precedence over team addition/removal to avoid multiple calls when
            // not needed. Seems to me that "add" from team operation will never get called since it uses player list operation as well.
            if (packet.getPlayerListOperation() == null) {
                ScoreboardHelper.onTeamRemoved(MinecraftClient.getInstance().world.getScoreboard().getTeam(packet.getTeamName()));
            }
        } else {
            if (packet.getTeam().isPresent()) {
                ScoreboardHelper.onTeamUpdated(MinecraftClient.getInstance().world.getScoreboard().getTeam(packet.getTeamName()));
            }
        }
    }
}
