package physics.featureExtraction;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import ab.vision.ABObject;
import ab.vision.Vision;

public class Extractor {

	private List<ABObject> pigs;
	private List<ABObject> birdsList;
	private List<ABObject> hillsList;
	private List<ABObject> blocksList;
	private List<ABObject> tntList;
	private List<Point> trajobjectsList;
	private Rectangle sling;

	public Extractor() {

	}

	public void extractFeaturesForVision(Vision vision) {

		sling = vision.findSlingshotMBR();

		// If the level is loaded (in PLAYING　state)but no slingshot detected,
		// then the agent will request to fully zoom out.
		while (sling == null) {
			System.out.println("no slingshot detected. Please remove pop up or zoom out");
			return;
		}

		// get all the pigs
		pigs = vision.findPigsRealShape();
		birdsList = vision.findBirdsRealShape();
		hillsList = vision.findHills();
		blocksList = vision.findBlocksRealShape();
		tntList = vision.findTNTs();
		trajobjectsList = vision.findTrajPoints();
	}

	public List<List<ABObject>> getABLists() {
		List<List<ABObject>> abList = new ArrayList<>();
		abList.add(pigs);
		abList.add(birdsList);
		abList.add(blocksList);
		abList.add(hillsList);
		abList.add(tntList);

		generateIDs(abList);

		return abList;
	}

	public List<Point> getTrajobjectsList() {
		return trajobjectsList;
	}

	public Rectangle getSling() {
		return sling;
	}

	public void generateIDs(List<List<ABObject>> list) {
		int counter = 0;
		for (List<ABObject> innerList : list) {
			for (ABObject obj : innerList) {
				obj.globalID = obj.getType().toString().toLowerCase() + counter++;
			}
			counter = 0;
		}

	}

}
