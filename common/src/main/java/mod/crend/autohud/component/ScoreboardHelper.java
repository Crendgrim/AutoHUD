package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;
import mod.crend.autohud.component.state.ScoreboardComponentState;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;

public class ScoreboardHelper {
    public static void onObjectiveUpdate(ScoreboardObjective objective) {
        if (canUpdate() && AutoHud.config.shouldRevealScoreboardOnTitleChange()) {
            getScoreboardComponent().updateObjectiveDisplayName(objective);
        }
    }

    public static void onPlayerScoreUpdate(ScoreboardPlayerScore score) {
        if (canUpdate() && AutoHud.config.shouldRevealScoreboardOnScoreChange()) {
            getScoreboardComponent().onPlayerScoreUpdate(score);
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
        return Component.Scoreboard.config.active() && Component.Scoreboard.state != null;
    }

    private static ScoreboardComponentState getScoreboardComponent() {
        return (ScoreboardComponentState)Component.Scoreboard.state;
    }
}
