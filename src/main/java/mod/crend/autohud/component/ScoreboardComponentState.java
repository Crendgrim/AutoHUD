package mod.crend.autohud.component;

import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;

import java.util.*;

public class ScoreboardComponentState extends ValueComponentState<ScoreboardObjective> {

    Text oldDisplayName;
    Team playerTeam;
    int oldTeamId;
    Text oldTeamName;

    Map<ScoreboardPlayerScore, Integer> playerScores = new HashMap<>();

    public ScoreboardComponentState(Component component) {
        super(component, ScoreboardComponentState::getObjective, true);
        collectPlayerScores();
    }

    public static ScoreboardObjective getObjective() {
        Scoreboard scoreboard = MinecraftClient.getInstance().world.getScoreboard();
        int m;
        Team team = scoreboard.getPlayerTeam(MinecraftClient.getInstance().player.getEntityName());
        if (team != null && (m = team.getColor().getColorIndex()) >= 0) {
            ScoreboardObjective teamObjective = scoreboard.getObjectiveForSlot(Scoreboard.MIN_SIDEBAR_TEAM_DISPLAY_SLOT_ID + m);
            if (teamObjective != null) return teamObjective;
        }
        return scoreboard.getObjectiveForSlot(Scoreboard.SIDEBAR_DISPLAY_SLOT_ID);
    }

    private void collectPlayerScores() {
        if (oldValue == null) return;
        playerTeam = oldValue.getScoreboard().getPlayerTeam(MinecraftClient.getInstance().player.getEntityName());
        playerScores.clear();
        oldValue.getScoreboard().getKnownPlayers().forEach(player -> {
            ScoreboardPlayerScore score = oldValue.getScoreboard().getPlayerScore(player, oldValue);
            playerScores.put(score, score.getScore());
        });
    }


    public void updateObjective(ScoreboardObjective objective) {
        if (Objects.equals(oldValue, objective)) {
            Text newDisplayName = oldValue.getDisplayName();
            if (newDisplayName != oldDisplayName) {
                oldDisplayName = newDisplayName;
                component.revealCombined();
            }
        }
    }
    public void updateScore(ScoreboardPlayerScore playerScore) {
        if (Objects.equals(oldValue, playerScore.getObjective())) {
            Integer score = playerScores.get(playerScore);
            if (score == null) {
                component.revealCombined();
            } else {
                if (score != playerScore.getScore()) {
                    component.revealCombined();
                }
            }
            playerScores.put(playerScore, playerScore.getScore());
        }
    }
    public void removeScore(String playerName) {
        playerScores.keySet().removeIf(playerScore -> playerScore.getPlayerName().equals(playerName));
    }
    public void removeScore(String playerName, ScoreboardObjective objective) {
        if (Objects.equals(oldValue, objective)) {
            removeScore(playerName);
        }
    }
    public void updateTeam(Team team) {
        if (Objects.equals(playerTeam, team)) {
            int newTeamId = playerTeam.getColor().getColorIndex();
            if (newTeamId != oldTeamId) {
                oldTeamId = newTeamId;
                component.revealCombined();
                return;
            }
            Text newTeamName = playerTeam.getDisplayName();
            if (!newTeamName.equals(oldTeamName)) {
                oldTeamName = newTeamName;
                component.revealCombined();
            }
        }
    }
}
