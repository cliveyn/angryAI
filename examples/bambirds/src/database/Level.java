package database;

import java.util.ArrayList;
import java.util.List;

import database.Match.MatchState;

public class Level {
	
	private int availableBirds;
	private int possibleDamageScore;
	private LevelState state;
	private int maximalAchievableIncreaseBirds;
	private int maximalAchievableIncreaseDamage;
	private int numberOfTriedStrategies;
	private int numberOfSuccessfulStrategies;
	private ScenarioInformation initialScenario = new ScenarioInformation();
	
    private ArrayList<Match> matches = new ArrayList<Match>();

    public int getBestScore() {
    	int bestScore = 0;
    	for (Match match : matches) {
    		if (match.getScore() > bestScore) {
    			bestScore = match.getScore();
    		}
    	}
    	return bestScore;
    }

    //public boolean isMastered() {
    //	for (Match match : matches) {
    //		if (match.getState() == MatchState.WON) {
    //			return true;
    //		}
    //	}
    //	return false;
    //}

    public List<Match> getMatches() {
	return matches;
    }

    public void addMatch(Match match) {
    	matches.add(match);
    	numberOfTriedStrategies++;
    }
    
    public LevelState getState() {
    	return state;
    }
    
    public ScenarioInformation getInitialScenario() {
		return initialScenario;
	}

	public void setInitialScenario(ScenarioInformation initialScenario) {
		this.initialScenario = initialScenario;
	}

	public void setInitialLevelInformation(int availableBirds, int possibleDamageScore) {
    	this.availableBirds = availableBirds;
    	this.possibleDamageScore = possibleDamageScore;
    	state = LevelState.OPEN;
    	maximalAchievableIncreaseBirds = (availableBirds - 1) * 10000;
    	maximalAchievableIncreaseDamage = possibleDamageScore;
    	numberOfTriedStrategies = 0;
    	numberOfSuccessfulStrategies = 0;
    }
    
    public void completeLevel(int numberOfShots, int score) {
    	state = LevelState.SUCCESS;
    	
    	if(score > getBestScore()) {    		
    		int remainingBirdsBonus = (availableBirds - numberOfShots) * 10000;
    		int maximalAchievedDamageScore = score - remainingBirdsBonus;
    		
    		maximalAchievableIncreaseBirds = (numberOfShots - 1) * 10000;
    		maximalAchievableIncreaseDamage = possibleDamageScore - maximalAchievedDamageScore;
    	}
    	
    	numberOfSuccessfulStrategies++;
    }
    
    public void markLevelAsImpossible() {
    	state = LevelState.IMPOSSIBLE;
    }
    
    public double calculatePotential() {
    	return maximalAchievableIncreaseBirds + maximalAchievableIncreaseDamage;
    }

    @Override
    public String toString() {
    	return matches.size() + " matches: " + matches + "";
    }
    
}
