package mod.crend.autohud.mixin;

import mod.crend.autohud.component.ScoreboardHelper;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Scoreboard.class)
public class ScoreboardMixin {
    @Inject(method="updateExistingObjective", at=@At("HEAD"))
    public void autoHud$onObjectiveUpdate(ScoreboardObjective objective, CallbackInfo ci) {
        ScoreboardHelper.onObjectiveUpdate(objective);
    }
    
    @Inject(method="updateScore", at=@At("HEAD"))
    public void autoHud$onPlayerScoreUpdate(ScoreboardPlayerScore score, CallbackInfo ci) {
        ScoreboardHelper.onPlayerScoreUpdate(score);
    }
    	
    @Inject(method="updatePlayerScore(Ljava/lang/String;)V", at=@At("HEAD"))
    public void autoHud$onPlayerScoreReset(String playerName, CallbackInfo ci) {
        ScoreboardHelper.onPlayerScoreRemove(playerName);
    }
    	
    @Inject(method="updatePlayerScore(Ljava/lang/String;Lnet/minecraft/scoreboard/ScoreboardObjective;)V", at=@At("HEAD"))
    public void autoHud$onPlayerScoreReset(String playerName, ScoreboardObjective objective, CallbackInfo ci) {
        ScoreboardHelper.onPlayerScoreRemove(playerName, objective);
    }
    	
    @Inject(method="addPlayerToTeam", at=@At("RETURN"))
    public void autoHud$onPlayerAddedToTeam(String playerName, Team teamAddedTo, CallbackInfoReturnable<Boolean> ci) {
        ScoreboardHelper.onPlayerAddedToTeam(playerName, teamAddedTo);
    }
    @Inject(method="removePlayerFromTeam", at=@At("TAIL"))
    public void autoHud$onPlayerRemovedFromTeam(String playerName, Team teamRemovedFrom, CallbackInfo ci) {
        ScoreboardHelper.onPlayerRemovedFromTeam(playerName, teamRemovedFrom);
    }
}
