package database;

import java.util.ArrayList;

import ab.demo.other.Shot;

public class Match {

    public enum MatchState {
    	PLAYING, WON, LOST, ABORTED
    }

    private MatchState state = MatchState.PLAYING;
    private int score = 0;
    private ArrayList<Shot> shots = new ArrayList<Shot>();

    public MatchState getState() {
    	return state;
    }

    public void setState(MatchState state) {
    	this.state = state;
    }

    public int getScore() {
    	return score;
    }

    public void setScore(int score) {
    	this.score = score;
    }

    public ArrayList<Shot> getShots() {
    	return shots;
    }

    public void addShot(Shot shot) {
    	shots.add(shot);
    }

    @Override
    public String toString() {
    	return "Match (" + state + "), " + score + " points, " + shots.size() + " shots: " + shots;
    }
    
}
