package meta;

import java.util.List;

public class LevelSelection {

	private int numberOfLevels;
	private int currentLevel;
	private boolean incrementByOne = true;

	public LevelSelection(int numberOfLevels) {
		this.numberOfLevels = numberOfLevels;
		currentLevel = 0;
	}

	public int selectNextLevel(List<Level> levels) {
		currentLevel++;

		if (currentLevel > numberOfLevels) {
			incrementByOne = false;
		}
		if (incrementByOne) {
			return currentLevel;
		} else {
			double[] props = new double[levels.size()];
			for (int i = 0; i < levels.size(); i++) {
				Level l = levels.get(i);
				double prop = 1 / (3 * (double) l.getNumberOfTimesPlayed());
				double eMaxPoints = l.getEstimatedMaximalPoints();
				double bestScore = l.getBestScore();

				prop *= ((eMaxPoints - bestScore) / eMaxPoints);

				if (bestScore > eMaxPoints) {
					props[i] = 0;
				} else {
					props[i] = prop;
				}

				System.out.println("Prop for Level " + (i + 1) + " " + prop);
			}
			int bestLevelIndex = 0;
			double bestProp = -10000;
			for (int i = 0; i < levels.size(); i++) {
				if (props[i] > bestProp) {
					bestLevelIndex = i;
					bestProp = props[i];
				}
			}

			System.out.println("Selecting Level " + (bestLevelIndex + 1) + " Propability: " + (bestProp * 100) + "%");

			currentLevel = bestLevelIndex + 1;

			return bestLevelIndex + 1;
		}
	}

	public int getCurrentLevel() {
		return currentLevel;
	}

	public int getNumberOfLevels() {
		return numberOfLevels;
	}
	
	public boolean getIncrementByOne(){
		return incrementByOne;
	}
}
