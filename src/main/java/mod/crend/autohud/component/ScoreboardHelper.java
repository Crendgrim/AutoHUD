package mod.crend.autohud.component;

import mod.crend.autohud.AutoHud;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;

public class ScoreboardHelper {
    public static void updateObjective(ScoreboardObjective objective) {
        if (Component.Scoreboard.config.active() && Component.Scoreboard.config.onChange() && Component.Scoreboard.state != null) {
            ((ScoreboardComponentState) Component.Scoreboard.state).updateObjective(objective);
        }
    }

    public static void updateScore(ScoreboardPlayerScore score) {
        if (Component.Scoreboard.config.active() && AutoHud.config.isScoreboardOnScoreChange() && Component.Scoreboard.state != null) {
            ((ScoreboardComponentState) Component.Scoreboard.state).updateScore(score);
        }
    }

    public static void removeScore(String playerName) {
        if (Component.Scoreboard.config.active() && AutoHud.config.isScoreboardOnScoreChange() && Component.Scoreboard.state != null) {
            ((ScoreboardComponentState) Component.Scoreboard.state).removeScore(playerName);
        }
    }

    public static void removeScore(String playerName, ScoreboardObjective objective) {
        if (Component.Scoreboard.config.active() && AutoHud.config.isScoreboardOnScoreChange() && Component.Scoreboard.state != null) {
            ((ScoreboardComponentState) Component.Scoreboard.state).removeScore(playerName, objective);
        }
    }

    public static void updateTeam(Team team) {
        if (Component.Scoreboard.config.active() && AutoHud.config.isScoreboardOnTeamChange() && Component.Scoreboard.state != null) {
            ((ScoreboardComponentState) Component.Scoreboard.state).updateTeam(team);
        }
    }
}
