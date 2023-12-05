package mod.crend.autohud.mixin;

import mod.crend.autohud.component.ScoreboardHelper;
import net.minecraft.scoreboard.*;
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
    public void autoHud$onPlayerScoreUpdate(ScoreHolder scoreHolder, ScoreboardObjective objective, ScoreboardScore score, CallbackInfo ci) {
        ScoreboardHelper.onPlayerScoreUpdate(scoreHolder, objective, score);
    }
    	
    @Inject(method="onScoreRemoved", at=@At("HEAD"))
    public void autoHud$onPlayerScoreRemoved(ScoreHolder scoreHolder, ScoreboardObjective objective, CallbackInfo ci) {
        ScoreboardHelper.onPlayerScoreRemove(scoreHolder.getNameForScoreboard(), objective);
    }

    @Inject(method="onScoreHolderRemoved", at=@At("HEAD"))
    public void autoHud$onPlayerRemoved(ScoreHolder scoreHolder, CallbackInfo ci) {
        ScoreboardHelper.onPlayerScoreRemove(scoreHolder.getNameForScoreboard());
    }

    @Inject(method="resetScore", at=@At("HEAD"))
    public void autoHud$onPlayerScoreReset(ScoreHolder scoreHolder, ScoreboardObjective objective, CallbackInfo ci) {
        ScoreboardHelper.onPlayerScoreRemove(scoreHolder.getNameForScoreboard(), objective);
    }

    @Inject(method="addScoreHolderToTeam", at=@At("RETURN"))
    public void autoHud$onPlayerAddedToTeam(String playerName, Team teamAddedTo, CallbackInfoReturnable<Boolean> ci) {
        ScoreboardHelper.onPlayerAddedToTeam(playerName, teamAddedTo);
    }
    @Inject(method="removeScoreHolderFromTeam", at=@At("TAIL"))
    public void autoHud$onPlayerRemovedFromTeam(String playerName, Team teamRemovedFrom, CallbackInfo ci) {
        ScoreboardHelper.onPlayerRemovedFromTeam(playerName, teamRemovedFrom);
    }
}
