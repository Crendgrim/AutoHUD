package mod.crend.autohud.component;

import net.minecraft.client.MinecraftClient;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectIntMutablePair;
import mod.crend.autohud.mixin.TeamMixinAccessor;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.*;

public class ScoreboardComponentState extends ValueComponentState<ScoreboardObjective> {
	/**
	 * Cache the slot used for just in case if we end up having our current objective removed and
	 * have to know if the objective slot provided is also either a team sidebar (if a player is 
	 * inside of a team that uses a sidebar then it typically takes priority over the default sidebar)
	 * 
	 * <p> Should there be no objectives found, this by default returns -1.
	 */
    final int objectiveSlotUsed;
    /**
     * Used to cache the team that the player was in when creating the component state.
     */
    final String cachedTeamUsedForObjectiveGen;
    /**
     * The old display name of the objective. Used to cross-reference changes between this
     * and the new display name.
     */
    Text oldDisplayName;
    

	/**
	 * Caches teams so that the component state knows what to look out for.
	 * 
	 * <p> The key represents the {@linkplain Team#getName() name of the team} whereas the pair 
	 * represents both the actual team (specifically the cached version of the team for us to 
	 * compare against) and how many players are in this team.
	 */
    Map<String, Pair<Team, Integer>> cachedTeams = new HashMap<>();
    /**
     * Caches the players that are in this objective.
     * 
     * <p> The key represents the {@linkplain ScoreboardPlayerScore#getPlayerName() player name} 
     * whetheras the pair represents both the team the player is in (which is referenced to 
     * {@link #cachedTeams}) and the current score the player has. Both entries inside the pair 
     * can be nullable but not the pair itself.
     */
    Map<String, Pair<String, Integer>> cachedPlayerScores = new HashMap<>();

    /**
     * Used over the constructor to ensure that there's an objective slot to be accounted rather than
     * ignored when attempting to perform checks on what sidebar to show, though the best way to
     * make sure when a change occurs is to at least try to listen for the player's team change and
     * account for the sidebar changes as well. Team changes usually are prioritized since this is
     * what the game shows you over the standard sidebar. (At the time this code was written, this had
     * the objective slot used to check for objective changes but that broke the scoreboard visibility
     * so it was forced to be ditched)
     */
    @SuppressWarnings("resource")
    public static ScoreboardComponentState createScoreboardComponent(Component component) {
    	Scoreboard scoreboard = MinecraftClient.getInstance().world.getScoreboard();
    	ScoreboardObjective objectiveToUse = null;
    	int objectiveSlotUsed = -1;
    	int teamColorIndex;
    	
    	Team playerTeam = scoreboard.getPlayerTeam(MinecraftClient.getInstance().player.getEntityName());
    	if (playerTeam != null && (teamColorIndex = playerTeam.getColor().getColorIndex()) >= 0) {
    		objectiveSlotUsed = Scoreboard.MIN_SIDEBAR_TEAM_DISPLAY_SLOT_ID + teamColorIndex;
    		objectiveToUse = scoreboard.getObjectiveForSlot(objectiveSlotUsed);
    	}
    	
    	if (objectiveToUse == null) {
    		objectiveSlotUsed = Scoreboard.SIDEBAR_DISPLAY_SLOT_ID;
    		objectiveToUse = scoreboard.getObjectiveForSlot(objectiveSlotUsed);
    	}
    	
    	return new ScoreboardComponentState(component, objectiveToUse, objectiveSlotUsed);
    }
    public ScoreboardComponentState(Component component, ScoreboardObjective objective, int objectiveSlotUsed, Team startingTeam) {
    	super(component, () -> objective, true); this.objectiveSlotUsed = objectiveSlotUsed; 
    	this.cachedTeamUsedForObjectiveGen = startingTeam == null ? null : startingTeam.getName(); 
    	this.collectPlayerScores();
    }
    public ScoreboardComponentState(Component component, ScoreboardObjective objective, int objectiveSlotUsed) {
    	this(component, objective, objectiveSlotUsed, null);
    }

    private void collectPlayerScores() {
        if (super.oldValue == null) return;
        
        this.cachedTeams.clear();
        this.cachedPlayerScores.clear();
        this.oldDisplayName = super.oldValue.getDisplayName();
        
        super.oldValue.getScoreboard().getAllPlayerScores(super.oldValue).forEach(this::addPlayerScoreAndTeam);
    }

    private void addPlayerScoreAndTeam(ScoreboardPlayerScore playerScore) {
    	Team playerTeam = super.oldValue.getScoreboard().getPlayerTeam(playerScore.getPlayerName());
    	
    	String teamName = playerTeam == null ? null : playerTeam.getName();
    	this.cachedPlayerScores.put(playerScore.getPlayerName(), new ObjectIntMutablePair<>(teamName, playerScore.getScore()));
    	
    	if (teamName != null) {
    		// First check if there's a cached team; if there is then update the amount of players
    		// using the team +1, otherwise create a new one while copying the team's important parts and
    		// set it to 1
    		if (this.cachedTeams.containsKey(teamName)) {
    			Pair<Team, Integer> teamEntry = this.cachedTeams.get(teamName);
    			teamEntry.right(teamEntry.right() + 1);
    		} else {
    			this.cachedTeams.put(teamName, new ObjectIntMutablePair<>(copyTeam(playerTeam), 1));
    		}
    	}
    }
    
    public void updateObjectiveDisplayName(ScoreboardObjective objective) {
    	// The only reason why updating the display name almost never works besides the first run:
    	// Whenever one plays locally, this issue is almost non-existent but it is slightly more common
    	// on minigame servers as they tend to change the objective a load of times; so for now until
    	// there is a way to switch objectives, it will never detect any display name changes since
    	// this component keeps the older sidebar objective. This also propagates to anything that 
    	// checks the objectives like #onPlayerScoreUpdate.
        if (Objects.equals(super.oldValue, objective)) {
            Text newDisplayName = objective.getDisplayName();
            
            if (!this.isTextEqual(newDisplayName, this.oldDisplayName)) {
                this.oldDisplayName = newDisplayName;
                super.component.revealCombined();
            }
        }
    }
    public void onPlayerScoreUpdate(ScoreboardPlayerScore playerScore) {
    	if (Objects.equals(super.oldValue, playerScore.getObjective())) {
    		if (this.cachedPlayerScores.containsKey(playerScore.getPlayerName())) { 
    			// if there's an existing player cached, update the cache and perform checks
    			Pair<String, Integer> cachedPair = this.cachedPlayerScores.get(playerScore.getPlayerName());
    			
    			if (cachedPair.right() == null || cachedPair.right() != playerScore.getScore()) {
    				super.component.revealCombined();
    				cachedPair.right(playerScore.getScore());
    			}
    		} else {
    			// else add this new player since we don't easily get to know when it was recently added
    			this.addPlayerScoreAndTeam(playerScore);
    		}
    		
    	}
    	
    }
    public void onPlayerScoreRemove(String playerName) {
//    	this.cachedPlayerScores.keySet().removeIf(playerScore -> playerScore.equals(playerName));
        
    	if (this.cachedPlayerScores.containsKey(playerName)) { 
    		Pair<String, Integer> cachedPair = this.cachedPlayerScores.get(playerName);
    		// before removing the player, remove them from the team's count if they are on a team
        	if (cachedPair.left() != null && this.cachedTeams.containsKey(cachedPair.left())) {
        		this.onPlayerRemovedFromTeam(this.cachedTeams.get(cachedPair.left()).left());
        	}
        	
        	this.cachedPlayerScores.remove(playerName);
        }
    }
    public void onPlayerScoreRemove(String playerName, ScoreboardObjective objective) {
        if (Objects.equals(super.oldValue, objective)) {
            this.onPlayerScoreRemove(playerName);
        }
    }
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
    	
    	// Remove the cached team after we remove all of the players associated with the team
    	if (this.cachedTeams.containsKey(team.getName())) this.cachedTeams.remove(team.getName());
    	
    	if (revealComponent) super.component.revealCombined();
    }
    public void onTeamUpdated(Team team) {
    	if (this.cachedTeams.isEmpty() || !this.cachedTeams.containsKey(team.getName())) return;
    	
    	Pair<Team, Integer> cachedTeam = this.cachedTeams.get(team.getName());
    	// Ensure that if they don't equal then reveal and update the current cached team with the new one
    	if (!this.isTeamEqual(team, cachedTeam.left())) {
    		super.component.revealCombined();
    		// this.cachedTeams.put(team.getName(), new Pair<>(copyTeam(team), cachedTeam.getRight()));
    		cachedTeam.left(copyTeam(team));
    	}
    }
    // Note: We are not checking if this player name *has* been added, instead we are checking
    // if the player name exists in our cached list.
    public void onPlayerAddedToTeam(String playerName, Team team) {
    	if (this.cachedPlayerScores.isEmpty()) return;
    	
    	Pair<String, Integer> cachedPlayerTeamAndScore = this.cachedPlayerScores.get(playerName);
    	
    	if (cachedPlayerTeamAndScore == null) return;
    	
    	if (!Objects.equals(cachedPlayerTeamAndScore.left(), team.getName())) {
    		final boolean cachedTeamIsEmpty = cachedPlayerTeamAndScore.left() == null;
    		
    		// cachedTeam still can return null, so better be safe than sorry
    		Pair<Team, Integer> cachedTeam = cachedTeamIsEmpty ? 
    			null : this.cachedTeams.get(cachedPlayerTeamAndScore.left());
    		boolean cachedTeamHasDifferences = cachedTeamIsEmpty || cachedTeam == null ? 
    			false : !this.isTeamEqual(team, cachedTeam.left());
    		
    		if (cachedTeamIsEmpty || cachedTeamHasDifferences) {
    			// Ensure that the cached team is not empty and that the team 
    			// checked has some changes in order to remove the player from the team
    			// (which actually doesn't remove the player from the team, but will in
    			// the sense of counts and in redirecting from the old team to a new one)
    			if (!cachedTeamIsEmpty && cachedTeamHasDifferences)
    				this.onPlayerRemovedFromTeam(cachedTeam.left());
    		
    			super.component.revealCombined();
    		
    			// Set the new cached team while keeping the same score
    			cachedPlayerTeamAndScore.left(team.getName());
    			
    			this.onPlayerAddedToTeam(team);
    		} 
    	}
    }

	public void onPlayerRemovedFromTeam(String playerName, Team team) {
    	if (this.cachedPlayerScores.isEmpty()) return;
    	
    	Pair<String, Integer> cachedPair = this.cachedPlayerScores.get(playerName);
    	
    	if (cachedPair != null && Objects.equals(cachedPair.left(), team.getName())) {
    		super.component.revealCombined();
    		// Clear the cached team while keeping the same score
    		cachedPair.left(null); this.onPlayerRemovedFromTeam(team);
    	}
    }
	
	private void onPlayerRemovedFromTeam(Team teamToRemoveFrom) {
		Pair<Team, Integer> cachedTeam = this.cachedTeams.get(teamToRemoveFrom.getName());
		if (cachedTeam == null) return; 
		
		if (cachedTeam.right() - 1 < 1)
			this.cachedTeams.remove(teamToRemoveFrom.getName());
		else
			cachedTeam.right(cachedTeam.right() - 1);
	}
	private void onPlayerAddedToTeam(Team teamAddedTo) {
		Pair<Team, Integer> cachedTeam = this.cachedTeams.get(teamAddedTo.getName());
		if (cachedTeam == null)
			this.cachedTeams.put(teamAddedTo.getName(), new ObjectIntMutablePair<>(copyTeam(teamAddedTo), 1));
		else
			cachedTeam.right(cachedTeam.right() + 1);
	}
	
	public void clear() {
		this.cachedTeams.clear();
		this.cachedPlayerScores.clear();
		this.oldDisplayName = null;
	}
	
    /**
     * We only end up checking the things we care about a team-- the visuals. 
     * 
     * <p> Why not the rest you may ask? Because they don't have, or don't do, any effect upon what the
     * user sees and we only care about name, color, prefix and suffix changes.
     * 
     * @param team the original team being compared against, can be a cached team as well
     * @param cachedTeam the team that is stored on {@link ScoreboardComponentState}, can be the other way around too
     * 
     * @return {@code true} if the team equals in what matters
     */
    private boolean isTeamEqual(Team team, Team cachedTeam)  {
    	if (team == cachedTeam) return true;
    	else if (team == null && cachedTeam != null) return false;
    	else if (team != null && cachedTeam == null) return false;
    	return team.getName().equals(cachedTeam.getName()) && team.getColor() == cachedTeam.getColor() && 
    		this.isTextEqual(team.getDisplayName(), cachedTeam.getDisplayName()) && this.isTextEqual(team.getPrefix(), cachedTeam.getPrefix()) && 
    		this.isTextEqual(team.getSuffix(), cachedTeam.getSuffix());
    }
    private boolean isTextEqual(Text left, Text right) {
    	final boolean leftEmpty = left == null; 
    	final boolean rightEmpty = right == null;
    	
    	if (leftEmpty != rightEmpty) return false;
    	if (leftEmpty && rightEmpty) return true;
    	
    	return left.getString().equals(right.getString());
    }
    /**
     * Copies a team, preferably the original team, with the following values: 
     * Team name, display name, color, prefix and suffix.
     * 
     * @param team to copy from
     * @return a copy of {@code team}, only including the values mentioned in the description
     */
    private static Team copyTeam(Team team) {
    	Team copiedTeam = new Team(null, team.getName());
    	((TeamMixinAccessor)copiedTeam).autohud$setDisplayName(team.getDisplayName());
    	((TeamMixinAccessor)copiedTeam).autohud$setColor(team.getColor());
    	teamSetPrefix(copiedTeam, team.getPrefix());
    	teamSetSuffix(copiedTeam, team.getSuffix());
    	return copiedTeam;
    }

	private static void teamSetPrefix(Team copiedTeam, Text prefix) {
		((TeamMixinAccessor)copiedTeam).autohud$setPrefix(prefix == null ? LiteralText.EMPTY : prefix.copy());
	}
	private static void teamSetSuffix(Team copiedTeam, Text suffix) {
		((TeamMixinAccessor)copiedTeam).autohud$setSuffix(suffix == null ? LiteralText.EMPTY : suffix.copy());
	}
}
