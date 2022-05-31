package mod.crend.autohud.mixin;

import org.objectweb.asm.Opcodes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;

import mod.crend.autohud.component.Hud;
import mod.crend.autohud.component.ScoreboardHelper;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	@Shadow private ClientWorld world;
	
    @Inject(method="onPlayerRespawn", at=@At("TAIL"))
    private void autoHud$onPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
        Hud.resetState();
    }
    @Inject(method="onTeam", at=@At(value="INVOKE", target = "java/util/Optional.ifPresent(Ljava/util/function/Consumer;)V", shift=At.Shift.AFTER))
    private void autoHud$onTeamUpdate(TeamS2CPacket packet, CallbackInfo ci) {
    	// JuggleStruggle: The reason why both getTeamOperation cannot be REMOVE is because we already perform team removal checks in 
    	// autoHud$onTeamBeforeRemoval. As for team being present, it... just means that it is required to know that it has been updated.
    	if (packet.getTeamOperation() != TeamS2CPacket.Operation.REMOVE && packet.getTeam().isPresent())
    		ScoreboardHelper.onTeamUpdated(this.world.getScoreboard().getTeam(packet.getTeamName()));
    }
    @Inject(method="onTeam", at=@At(value="JUMP", opcode=Opcodes.IF_ACMPNE, ordinal=3))
    private void autoHud$onTeamBeforeRemoval(TeamS2CPacket packet, CallbackInfo ci) {
    	// JuggleStruggle: We need to be sure that we are only calling the rest of the method if, and only if, there's
    	// no player listing going on as that takes precedence over team addition/removal to avoid multiple calls when
    	// not needed. Seems to me that "add" from team operation will never get called since it uses player list operation as well.
    	if (packet.getPlayerListOperation() != null) return;
    	
    	if (packet.getTeamOperation() == TeamS2CPacket.Operation.REMOVE) 
    		ScoreboardHelper.onTeamRemoved(this.world.getScoreboard().getTeam(packet.getTeamName()));
    }
}
