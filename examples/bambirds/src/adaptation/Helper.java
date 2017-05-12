package adaptation;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.dyn4j.dynamics.Body;

import ab.vision.ABObject;
import ab.vision.ABType;
import physics.birdtypes.BirdTypes;
import physics.calculation.CalculationStrategy;
import physics.materials.GameObject;

class Helper {
	
	/**
	 * Get the bird that is currently on the sling
	 * @param objects the object hierarchy of the objects in the game
	 * @return bird on the sling
	 */
	static ABObject getBirdOnSling(List<List<ABObject>> objects, ABType birdType) {
		List<ABObject> birds = objects.get(1);
                ABObject bird = birds.get(0);
                for (ABObject b : birds) {
                    if (b.getCenterY() < bird.getCenterY()) {
                        bird = b;
                    }
                }
                return bird;
                
                /*for (List<ABObject> list: objects) {
			if (list.stream().anyMatch(t -> t.type == ABType.BlueBird || t.type == ABType.BlackBird || t.type == ABType.RedBird || t.type == ABType.WhiteBird || t.type == ABType.YellowBird)) {
				ABObject bird = list.get(0);
				if (bird != null & bird.type == birdType) {
					return bird;
				}
			}
		}
		
		System.out.println("ADAPTATION: could not match the bird on sling with the bird-type");
		return null;*/
	}
	
	/**
	 * Helper method to flatten a two-dimensional list to a one-dimensional list
	 * @param list 2-dim list
	 * @return 1-dim list
	 */
	static List<ABObject> flattenABObjectsList(List<List<ABObject>> list) {
		List<ABObject> flat = list.stream()
			        .flatMap(l -> l.stream())
			        .collect(Collectors.toList());
		
		return flat;
	}
	
	/**
	 * Extract the ABObject with the id-string from a list of ABObjects
	 * @param list ABObjects from which to extract the one
	 * @param targetId id-string for the object to extract
	 * @return list of extracted objects -> should be a single object
	 */
	static List<ABObject> extractGameObjectsWithId(List<ABObject> list, String targetId) {
		return list.stream().filter(s -> s.globalID.equals(targetId)).collect(Collectors.toList());
	}
	
	/**
	 * DEPRECATED: Extract the Body with the id-string from a list of Body-s
	 * @param list Body-s from which to extract the one
	 * @param idString id-string for the object to extract
	 * @return list of extracted objects -> should be a single object
	 */
	static List<Body> extractBodysWithId(List<Body> list, String idString) {
		return list.stream().filter(s -> ((GameObject)s).globalID.equals(idString)).collect(Collectors.toList());
	}
	
	/**
	 * Extract a Body-instance with a specific id-string from a list of Body-s 
	 * @param list Body-s from which to extract the one
	 * @param idString id-string for the object to extract
	 * @return extracted Body-instance
	 */
	static Body extractBodyWithId(List<Body> list, String idString) {
		Iterator<Body> bodyIterator = list.iterator();
		
		Body extractedBody = null;
		
		while (bodyIterator.hasNext()) {
			Body tempBody = bodyIterator.next();
			if (((GameObject)tempBody).globalID != null) {
				if (((GameObject)tempBody).globalID.equals(idString)) {
					extractedBody = tempBody;
					break;
				}
			}
		}
		
		return extractedBody;
	}
	
	/**
	 * Sort a list of EvaluatedShots in descending order
	 * @param shots list of EvaluatedShots
	 */
	static void sortShotsDescending(List<EvaluatedShot> shots) {
		Collections.sort(shots, (s1, s2) -> new Double(s2.getScore()).compareTo(s1.getScore()));
	}
	
	/**
	 * Sort a list of EvaluatedShots in ascending order
	 * @param shots list of EvaluatedShots
	 */
	static void sortShotsAscending(List<EvaluatedShot> shots) {
		Collections.sort(shots, (s1, s2) -> new Double(s1.getScore()).compareTo(s2.getScore()));
	}
	
	/**
	 * Get a list with tap-time-variance values to adapt the tap-time
	 * @param start start value
	 * @param step spacing of the values
	 * @param birdType type of bird
	 * @return list of values to use for adaptation of the tap-time
	 */
	static List<Integer> getTapTimeVariance(int start, int step, ABType birdType) {
		int end = 25;
		if (birdType == ABType.YellowBird) {
			start = Math.max(-10, start);
			end = 30;
		} else {
			start = Math.max(-10, start);
		}
		
		ArrayList<Integer> values = new ArrayList<Integer>();
		for (int i = start; i <= end; i += step) {
			values.add(i);
		}
		
		return values;
	}
	
	/**
	 * Get a list of calculation strategies for the trajectory of the shot to perform
	 * @return list of calculation strategies
	 */
	static List<CalculationStrategy> getTrajectories(boolean isHorizontal, int deltaYPosition) {
		ArrayList<CalculationStrategy> traj = new ArrayList<CalculationStrategy>();
		traj.add(CalculationStrategy.UPPER);
		if ((!isHorizontal) || deltaYPosition > 15) {
			traj.add(CalculationStrategy.LOWER);
		}
		
		return traj;
 	}
	
	/**
	 * Convert the type of the bird on the sling for the physics simulation
	 * @param type of the bird in the game
	 * @return type fitting for the physics simulation
	 */
	static BirdTypes getBirdType(ABType type) {
		switch (type) {
		case BlueBird:
			return BirdTypes.BLUEBIRD;
		case RedBird:
			return BirdTypes.REDBIRD;
		case YellowBird:
			return BirdTypes.YELLOWBIRD;
		case BlackBird:
			return BirdTypes.BLACKBIRD;
		case WhiteBird:
			return BirdTypes.WHITEBIRD;
			default:
				return BirdTypes.REDBIRD; 
		}
	}
	
	/**
	 * Check if the target object is oriented horizontally or vertically
	 * @param object target object to aim at
	 * @return boolean (true if horizontal / false if vertical)
	 */
	static boolean isObjectHorizontal(ABObject object) {
		Rectangle rect = object.getBounds();
		return rect.width > rect.height;
	}
}
