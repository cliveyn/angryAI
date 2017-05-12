package database;

import java.util.Random;

import database.Match.MatchState;
import main.BamBirdModule;
import meta.LevelSelection;

public class Database extends BamBirdModule {

	private static Database instance = null;

	// TODO: this has to be dynamically determined
	private final int numberOfLevels = 21;
	private Level[] levels = new Level[numberOfLevels];

	private Match currentMatch = null;

	public static Database getInstance() {
		if (instance == null) {
			instance = new Database();
		}
		return instance;
	}

	private Database() {
		for (int i = 0; i < numberOfLevels; i++) {
			levels[i] = new Level();
		}
	}

	// returns null if newMatch() hasn't been called!
	public Match getCurrentMatch() {
		return currentMatch;
	}

	public Level getLevel(int level) {
		if (level < 1 || level > numberOfLevels) {
			throw new IllegalArgumentException(
					"db.getLevel: level greater than expected (max " + numberOfLevels + "): " + level);
		}
		// will this code ever be reached?
		if (levels[level - 1] == null) {
			levels[level - 1] = new Level();
		}
		return levels[level - 1];
	}

	public Match newMatch(int level) {
		if (level < 1 || level > numberOfLevels) {
			throw new IllegalArgumentException(
					"db.newMatch: level greater than expected (max " + numberOfLevels + "): " + level);
		}
		if (currentMatch != null && currentMatch.getState() == MatchState.PLAYING) {
			currentMatch.setState(MatchState.ABORTED);
		}
		currentMatch = new Match();
		getLevel(level).addMatch(currentMatch);
		return currentMatch;
	}

	public void setInitialLevelInformation(int levelID, int availableBirds, int possibleDamageScore) {
		levels[levelID + 1].setInitialLevelInformation(availableBirds, possibleDamageScore);
	}

	public void completeLevel(int levelID) {
		if (currentMatch == null) {
			return;
		}
		levels[levelID + 1].completeLevel(currentMatch.getShots().size(), currentMatch.getScore());
	}

	@Override
	public void shutdown() {
		// could be used to save state to disk
	}

	@Override
	public String toString() {
		String s = numberOfLevels + " levels, current match: " + currentMatch + ", levels=";
		for (int i = 0; i < numberOfLevels; i++) {
			if (levels[i] == null)
				continue;
			s += "[" + (i + 1) + " | " + levels[i].toString() + "]";
		}
		return s;
	}

}
