package database;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import planner.DecisionObject;
import ab.vision.ABObject;
import ab.vision.Vision;
import adaptation.EvaluatedShot;

public class ScenarioInformation {
	
	private List<List<ABObject>> objects;
	private Path file;
	private ArrayList<DecisionObject> decisionObjects = new ArrayList<DecisionObject>();
	private int nextBestDecisionObject = 0;
	private EvaluatedShot currentBestShot;
	
	public List<List<ABObject>> getObjects() {
		return objects;
	}
	
	public void setObjects(List<List<ABObject>> objects) {
		if (objects == null || objects.size() == 0) {
			throw new IllegalArgumentException("Feature Extraction returned 0 Objects ...");
		}
		this.objects = objects;
	}
	
	public Path getFile() {
		return file;
	}
	
	public void setFile(Path file) {
		this.file = file;
	}
	
	public ArrayList<DecisionObject> getDecisionObjects() {
		return decisionObjects;
	}
	
	public void setDecisionObjects(ArrayList<DecisionObject> decisionObjects) {
		this.decisionObjects = decisionObjects;
	}
	
	public DecisionObject getTopTarget() {
	    	if (decisionObjects.isEmpty()) return null;
		return decisionObjects.get(nextBestDecisionObject);
	}
	
	// TODO: immediately extend List of n top targets here so that in case of all levels being IMPOSSIBLE, we might reset
	//			their status and go on with further candidates
	public boolean chooseNextCandidate() {
		nextBestDecisionObject++;
		return nextBestDecisionObject < decisionObjects.size();
	}
	
}
