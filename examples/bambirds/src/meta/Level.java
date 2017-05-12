package meta;

import java.util.List;

import ab.vision.ABObject;
import ab.vision.ABType;

public class Level {
	private int estimatedMaximalPoints = 0;
	private int bestScore = 0;
	private int numberOfTimesPlayed = 0;
	
	public void setEstimatedMaximalPoints(List<List<ABObject>> objects){
		int points = 0;
		for(List<ABObject> l : objects){
			for(ABObject o : l){
				ABType type = o.getType();
				if(type == ABType.BlackBird || type == ABType.BlueBird || type == ABType.RedBird || type == ABType.WhiteBird || type == ABType.YellowBird){
					points += 10000;
				}
				if(type == ABType.Pig){
					points += 5300;
				}
				if(type == ABType.Wood){
					points += 700;
				}
				if(type == ABType.Ice){
					points += 650;
				}
				if(type == ABType.Stone){
					points += 800;
				}
			}
		}
		estimatedMaximalPoints = points;
	}
	
	public void setScore(int score){
		if(score > bestScore){
			bestScore = score;
		}
	}
	
	public int getEstimatedMaximalPoints(){
		return estimatedMaximalPoints;
	}
	
	public void incrementPlayedCounter(){
		numberOfTimesPlayed++;
	}
	
	public int getNumberOfTimesPlayed(){
		return numberOfTimesPlayed;
	}
	
	public int getBestScore(){
		return bestScore;
	}
	
	public String toString(){
		return "estimatedMaxPoints: " + estimatedMaximalPoints + " bestScore: " + bestScore + " numberOfTimesPlayed: " + numberOfTimesPlayed; 
	}
}
