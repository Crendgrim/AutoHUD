package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.state.ScoreboardComponentState;
//? if >=1.20.3
/*import net.minecraft.scoreboard.ScoreHolder;*/
import net.minecraft.scoreboard.ScoreboardObjective;
//? if <1.20.3 {
import net.minecraft.scoreboard.ScoreboardPlayerScore;
//?} else {
/*import net.minecraft.scoreboard.ScoreboardScore;
*///?}
import net.minecraft.scoreboard.Team;

public class ScoreboardHelper {
    public static void onObjectiveUpdate(ScoreboardObjective objective) {
        if (canUpdate() && AutoHud.config.shouldRevealScoreboardOnTitleChange()) {
            getScoreboardComponent().updateObjectiveDisplayName(objective);
        }
    }

    //? if <1.20.3 {
    public static void onPlayerScoreUpdate(ScoreboardPlayerScore score) {
    //?} else {
    /*public static void onPlayerScoreUpdate(ScoreHolder scoreHolder, ScoreboardObjective objective, ScoreboardScore score) {
    *///?}
        if (canUpdate() && AutoHud.config.shouldRevealScoreboardOnScoreChange()) {
            //? if <1.20.3 {
            getScoreboardComponent().onPlayerScoreUpdate(score);
            //?} else {
            /*getScoreboardComponent().onPlayerScoreUpdate(scoreHolder, objective, score);
            *///?}
        }
    }

    public static void onPlayerScoreRemove(String playerName) {
        if (canUpdate() && AutoHud.config.shouldRevealScoreboardOnScoreChange()) {
            getScoreboardComponent().onPlayerScoreRemove(playerName);
        }
    }

    public static void onPlayerScoreRemove(String playerName, ScoreboardObjective objective) {
        if (canUpdate() && AutoHud.config.shouldRevealScoreboardOnScoreChange()) {
            getScoreboardComponent().onPlayerScoreRemove(playerName, objective);
        }
    }

    public static void onTeamRemoved(Team team) {
        if (canUpdate() && AutoHud.config.shouldRevealScoreboardOnTeamChange()) {
            getScoreboardComponent().onTeamRemoved(team);
        }
    }

    public static void onTeamUpdated(Team team) {
        if (canUpdate() && AutoHud.config.shouldRevealScoreboardOnTeamChange()) {
            getScoreboardComponent().onTeamUpdated(team);
        }
    }

    public static void onPlayerAddedToTeam(String playerName, Team team) {
        if (canUpdate() && AutoHud.config.shouldRevealScoreboardOnTeamChange()) {
            getScoreboardComponent().onPlayerAddedToTeam(playerName, team);
        }
    }

    public static void onPlayerRemovedFromTeam(String playerName, Team team) {
        if (canUpdate() && AutoHud.config.shouldRevealScoreboardOnTeamChange()) {
            getScoreboardComponent().onPlayerRemovedFromTeam(playerName, team);
        }
    }

    // The next two methods are used to cut down on unnecessary duplicate code
    private static boolean canUpdate() {
        return Components.Scoreboard.config.active() && Components.Scoreboard.state != null;
    }

    private static ScoreboardComponentState getScoreboardComponent() {
        return (ScoreboardComponentState) Components.Scoreboard.state;
    }
}
