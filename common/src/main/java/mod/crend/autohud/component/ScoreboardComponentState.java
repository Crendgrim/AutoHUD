package mod.crend.autohud.component;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectIntMutablePair;
import mod.crend.autohud.mixin.TeamMixinAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class ScoreboardComponentState extends ValueComponentState<ScoreboardObjective> {
    /**
     * The cached display name of the objective. Used to cross-reference changes
     * between this and the new display name.
     */
    Text cachedDisplayName;
    /**
     * Caches teams so that the component state knows what to look out for.
     * 
     * <p> The key represents the {@linkplain Team#getName() name of the team} 
     * whereas the pair represents both the actual team (specifically the cached 
     * version of the team for us to compare against) and how many players are in 
     * this team.
     */
    Map<String, Pair<Team, Integer>> cachedTeams = new HashMap<>();
    /**
     * Caches the players that are in this objective.
     * 
     * <p> The key represents the {@linkplain ScoreboardPlayerScore#getPlayerName()
     * player name} whether as the pair represents both the team the player is in
     * (which is referenced to {@link #cachedTeams}) and the current score the
     * player has. Both entries inside the pair can be nullable but not the pair
     * itself.
     */
    Map<String, Pair<String, Integer>> cachedPlayerScores = new HashMap<>();

    public ScoreboardComponentState(Component component) {
        // the newValueSupplier in the use of super constructor does not allow the use
        // of "this" qualifier, so instead all of it had to be redone to allow it
        super(component, ScoreboardComponentState::createObjective, true);
        this.collectPlayerScores(super.oldValue);
    }

    private static ScoreboardObjective createObjective() {
        Scoreboard scoreboard = MinecraftClient.getInstance().world.getScoreboard();
        ScoreboardObjective objectiveToUse = null;
        int teamColorIndex;

        Team playerTeam = scoreboard.getPlayerTeam(MinecraftClient.getInstance().player.getEntityName());
        if (playerTeam != null && (teamColorIndex = playerTeam.getColor().getColorIndex()) >= 0) {
            objectiveToUse = scoreboard.getObjectiveForSlot(Scoreboard.MIN_SIDEBAR_TEAM_DISPLAY_SLOT_ID + teamColorIndex);
        }

        if (objectiveToUse == null) {
            objectiveToUse = scoreboard.getObjectiveForSlot(Scoreboard.SIDEBAR_DISPLAY_SLOT_ID);
        }

        return objectiveToUse;
    }

    @Override
    protected void onUpdateReveal(ScoreboardObjective newObjective) {
        // Actually ensure that the new objective and the cached objective aren't the 
        // same even though in ValueComponentState#doReveal checks for it and if its 
        // config isn't active, which the latter would be causing constant updates and 
        // as a result this is the reason why it has to be comparing equality checks.
        // The reason why do this is to clear anything that this component state has 
        // stored (though it isn't fully sure if old player names & teams do get removed
        // once there's a new objective to set in) and cache anything new from this new
        // objective.
        if (!Objects.equals(newObjective, super.oldValue)) this.collectPlayerScores(newObjective);
    }
    
    private void collectPlayerScores(ScoreboardObjective objective) {
        if (objective == null) return;

        this.cachedTeams.clear();
        this.cachedPlayerScores.clear();
        this.cachedDisplayName = objective.getDisplayName();

        objective.getScoreboard().getAllPlayerScores(objective).forEach(this::addPlayerScoreAndTeam);
    }

    private void addPlayerScoreAndTeam(ScoreboardPlayerScore playerScore) {
        Team playerTeam = playerScore.getObjective().getScoreboard().getPlayerTeam(playerScore.getPlayerName());
        String teamName = playerTeam == null ? null : playerTeam.getName();

        this.cachedPlayerScores.put(playerScore.getPlayerName(),
                new ObjectIntMutablePair<>(teamName, playerScore.getScore()));

        if (teamName == null) return;

        // First check if there's a cached team; if there is then update the amount of
        // players using the team +1, otherwise create a new one while copying the team's
        // important parts and set it to 1
        if (this.cachedTeams.containsKey(teamName)) {
            Pair<Team, Integer> teamEntry = this.cachedTeams.get(teamName);
            teamEntry.right(teamEntry.right() + 1);
        } else {
            this.cachedTeams.put(teamName, new ObjectIntMutablePair<>(copyTeam(playerTeam), 1));
        }
    }

    public void updateObjectiveDisplayName(ScoreboardObjective objective) {
        // The only reason why updating the display name almost never works besides the
        // first run with the exception of updating the objective within this component state:
        // Whenever one plays locally, this issue is almost non-existent but it is slightly 
        // more common on minigame servers as they tend to change the objective a load of 
        // times; so for now until there is a way to switch objectives, it will never detect 
        // any display name changes since this component keeps the older sidebar objective. 
        // This also propagates to anything that checks the objectives like #onPlayerScoreUpdate.
        if (Objects.equals(super.oldValue, objective)) {
            Text newDisplayName = objective.getDisplayName();

            if (!isTextEqual(newDisplayName, this.cachedDisplayName)) {
                this.cachedDisplayName = newDisplayName;
                super.component.revealCombined();
            }
        }
    }

    public void onPlayerScoreUpdate(ScoreboardPlayerScore playerScore) {
        if (Objects.equals(super.oldValue, playerScore.getObjective())) {
            if (this.cachedPlayerScores.containsKey(playerScore.getPlayerName())) {
                // if there's an existing player cached, update the cache and perform checks
                Pair<String, Integer> cachedTeamAndScore = this.cachedPlayerScores.get(playerScore.getPlayerName());

                if (cachedTeamAndScore.right() == null || cachedTeamAndScore.right() != playerScore.getScore()) {
                    super.component.revealCombined();
                    cachedTeamAndScore.right(playerScore.getScore());
                }
            } else {
                // else add this new player since we don't easily get to know when it was
                // recently added
                this.addPlayerScoreAndTeam(playerScore);
            }

        }

    }

    public void onPlayerScoreRemove(String playerName) {
        if (this.cachedPlayerScores.containsKey(playerName)) {
            Pair<String, Integer> cachedTeamAndScore = this.cachedPlayerScores.get(playerName);
            // Before removing the player, remove them from the team's count if they are on a team
            if (cachedTeamAndScore.left() != null && this.cachedTeams.containsKey(cachedTeamAndScore.left())) {
                this.onTeamRemovedFromPlayer(this.cachedTeams.get(cachedTeamAndScore.left()).left());
            }

            this.cachedPlayerScores.remove(playerName);
        }
    }

    public void onPlayerScoreRemove(String playerName, ScoreboardObjective objective) {
        // Ensure the objective equals before removing a player; we are not going to 
        // remove any players that exists in this objective while the targeting 
        // objective doesn't match
        if (Objects.equals(super.oldValue, objective)) {
            this.onPlayerScoreRemove(playerName);
        }
    }

    /**
     * Removes the provided team from any cached players and cached team so that in the
     * future, it does not cause problems regarding non-existent teams.
     * @param team the team to remove from this component state
     */
    public void onTeamRemoved(Team team) {
        if (this.cachedPlayerScores.isEmpty()) return;

        boolean revealComponent = false;
        Iterator<Map.Entry<String, Pair<String, Integer>>> scores = this.cachedPlayerScores.entrySet().iterator();

        while (scores.hasNext()) {
            Pair<String, Integer> cachedPair = scores.next().getValue();
            if (Objects.equals(cachedPair.left(), team.getName())) {
                cachedPair.left(null); revealComponent = true;
            }
        }

        // Remove the cached team after we remove all of the players associated with the
        // team
        this.cachedTeams.remove(team.getName());

        if (revealComponent)
            super.component.revealCombined();
    }

    public void onTeamUpdated(Team team) {
        if (this.cachedTeams.isEmpty() || !this.cachedTeams.containsKey(team.getName())) return;

        Pair<Team, Integer> cachedTeam = this.cachedTeams.get(team.getName());
        // Ensure that if they don't equal then reveal and update the current cached
        // team with the new one
        if (!isTeamEqual(team, cachedTeam.left())) {
            super.component.revealCombined();
            cachedTeam.left(copyTeam(team));
        }
    }

    // Note: We are not checking if this player name *has* been added, instead we
    // are checking if the player name exists in our cached list.
    public void onPlayerAddedToTeam(String playerName, Team team) {
        if (this.cachedPlayerScores.isEmpty()) return;

        Pair<String, Integer> cachedTeamAndScore = this.cachedPlayerScores.get(playerName);

        if (cachedTeamAndScore == null) return;

        if (!Objects.equals(cachedTeamAndScore.left(), team.getName())) {
            final boolean cachedPlayerTeamIsEmpty = cachedTeamAndScore.left() == null;

            // cachedTeam still can return null, so better be safe than sorry
            Pair<Team, Integer> cachedTeam = cachedPlayerTeamIsEmpty ? 
                    null : this.cachedTeams.get(cachedTeamAndScore.left());
            boolean cachedTeamHasDifferences = !cachedPlayerTeamIsEmpty && cachedTeam != null &&
                    !isTeamEqual(team, cachedTeam.left());

            if (cachedPlayerTeamIsEmpty || cachedTeamHasDifferences) {
                // Ensure that the cached team is not empty and that the team checked has 
                // some changes in order to remove the player from the team (which actually 
                // doesn't remove the player from the team, but will in the sense of counts 
                // and in redirecting from the old team to a new one)
                if (cachedTeamHasDifferences)
                    this.onTeamRemovedFromPlayer(cachedTeam.left());

                super.component.revealCombined();

                // Set the new cached team while keeping the same score
                cachedTeamAndScore.left(team.getName());

                this.onTeamAddedToPlayer(team);
            }
        }
    }

    public void onPlayerRemovedFromTeam(String playerName, Team team) {
        if (this.cachedPlayerScores.isEmpty()) return;

        Pair<String, Integer> cachedTeamAndScore = this.cachedPlayerScores.get(playerName);

        if (cachedTeamAndScore != null && Objects.equals(cachedTeamAndScore.left(), team.getName())) {
            super.component.revealCombined();
            // Clear the cached team while keeping the same player score
            cachedTeamAndScore.left(null);
            this.onTeamRemovedFromPlayer(team);
        }
    }

    private void onTeamAddedToPlayer(Team teamAddedTo) {
        Pair<Team, Integer> cachedTeam = this.cachedTeams.get(teamAddedTo.getName());
        if (cachedTeam == null)
            this.cachedTeams.put(teamAddedTo.getName(), new ObjectIntMutablePair<>(copyTeam(teamAddedTo), 1));
        else
            cachedTeam.right(cachedTeam.right() + 1);
    }

    private void onTeamRemovedFromPlayer(Team teamRemovedFrom) {
        Pair<Team, Integer> cachedTeam = this.cachedTeams.get(teamRemovedFrom.getName());
        if (cachedTeam == null) return;

        if (cachedTeam.right() - 1 < 1)
            this.cachedTeams.remove(teamRemovedFrom.getName());
        else
            cachedTeam.right(cachedTeam.right() - 1);
    }

    /**
     * We only end up checking the things we care about a team-- the visuals.
     * 
     * <p> Why not the rest you may ask? Because they don't have, or don't do, any
     * effect upon what the user sees and we only care about name, color, prefix and
     * suffix changes.
     * 
     * @param team the original team being compared against, can be a cached team as well
     * @param cachedTeam the team that is stored on {@link ScoreboardComponentState}, can be the other way around too
     * 
     * @return {@code true} if the team equals in what matters
     */
    private static boolean isTeamEqual(Team team, Team cachedTeam) {
        if (team == cachedTeam) return true;
        if (team == null || cachedTeam == null) return false;
            
        return team.getName().equals(cachedTeam.getName()) && team.getColor() == cachedTeam.getColor()
                && isTextEqual(team.getDisplayName(), cachedTeam.getDisplayName())
                && isTextEqual(team.getPrefix(), cachedTeam.getPrefix())
                && isTextEqual(team.getSuffix(), cachedTeam.getSuffix());
    }

    private static boolean isTextEqual(Text left, Text right) {
        final boolean leftIsEmpty = left == null;
        final boolean rightIsEmpty = right == null;

        if (leftIsEmpty && rightIsEmpty) return true;
        if (leftIsEmpty != rightIsEmpty) return false;

        return left.getString().equals(right.getString());
    }

    /**
     * Copies a team, preferably the original team, with the following values: Team
     * name, display name, color, prefix and suffix.
     * 
     * @param team to copy from
     * @return a copy of {@code team}, only including the values mentioned in the description
     */
    private static Team copyTeam(Team team) {
        Team copiedTeam = new Team(null, team.getName());
        ((TeamMixinAccessor) copiedTeam).autohud$setDisplayName(team.getDisplayName());
        ((TeamMixinAccessor) copiedTeam).autohud$setColor(team.getColor());
        teamSetPrefix(copiedTeam, team.getPrefix());
        teamSetSuffix(copiedTeam, team.getSuffix());
        return copiedTeam;
    }

    private static void teamSetPrefix(Team copiedTeam, Text prefix) {
        ((TeamMixinAccessor) copiedTeam).autohud$setPrefix(prefix == null ? LiteralText.EMPTY : prefix.copy());
    }

    private static void teamSetSuffix(Team copiedTeam, Text suffix) {
        ((TeamMixinAccessor) copiedTeam).autohud$setSuffix(suffix == null ? LiteralText.EMPTY : suffix.copy());
    }
}