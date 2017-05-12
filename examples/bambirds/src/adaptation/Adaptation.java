package adaptation;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;

import ab.demo.NaiveAgent;
import ab.demo.other.ActionRobot;
import ab.demo.other.Shot;
import ab.planner.TrajectoryPlanner;
import ab.vision.ABObject;
import ab.vision.ABType;
import ab.vision.Vision;
import ab.vision.GameStateExtractor.GameState;
import main.BamBird;
import main.BamBirdModule;
import physics.Physics;
import physics.birdtypes.BirdTypes;
import physics.calculation.Calculation;
import physics.calculation.CalculationStrategy;
import physics.materials.GameObject;
import planner.DecisionObject;

public class Adaptation extends BamBirdModule {
	
	private Physics physicsEngine;
	private TrajectoryPlanner tp;
	
	private Random randomGenerator;
	private boolean firstShot;
	private Point prevTarget;
	
	private List<TargetArea> _targetAreas;
	
	public Adaptation() {
		BamBird instance = BamBird.getInstance();
		this.physicsEngine = instance.getPhysics();
		
		tp = new TrajectoryPlanner();
		
		prevTarget = null;
		firstShot = true;
		randomGenerator = new Random();
	}
	
	
	private Rectangle getSling() {
		BufferedImage screenshot = BamBird.getInstance().actionRobot.doScreenShot();

		// process image
		Vision vision = new Vision(screenshot);
		
		
		Rectangle sling = vision.findSlingshotMBR();

		// confirm the slingshot
		while (sling == null && BamBird.getInstance().actionRobot.checkState() == GameState.PLAYING) {
			System.out
			.println("No slingshot detected. Please remove pop up or zoom out");
			screenshot = BamBird.getInstance().actionRobot.doScreenShot();
			vision = new Vision(screenshot);
			sling = vision.findSlingshotMBR();
		}
		
		return sling;
	}
	
	private Point getHitPointOfTargetVertically(Rectangle boundingBox, double factor) {
		int leftX = boundingBox.x;
		int yCoord = (int)Math.round(boundingBox.y - (factor * boundingBox.height)); // test if y goes up or down!!!
		return new Point(leftX, yCoord);
	}
	
	private Point getHitPointOfTargetHorizontally(Rectangle boundingBox, double factor) {
		int upperY = boundingBox.y;
		int xCoord = (int)Math.round(boundingBox.x + (factor * boundingBox.width));
		return new Point(xCoord, upperY);
	}
	
	/**
	 * Calculate the coordinates where to hit the target object according to TargetArea 
	 * categorisation (high, mid, low -> for vertical objects or left, center, right for horizontal objects)
	 * -> not using interpolation but the Rectangle's convenient methods
	 * @param boundingBox boundingBox of the target object
	 * @param area the TargetArea categorisation to aim at
	 * @return the hit-point
	 */
	private Point getHitPointOfTargetEasy(Rectangle boundingBox, TargetArea area) {
		
		Point hitPoint;
		if (area == TargetArea.High || area == TargetArea.Mid || area == TargetArea.Low) {
			int leftX = boundingBox.x;
			int yCoord = 0;
			switch (area) {
			case Low:
				yCoord = (int)boundingBox.getMinY();
				break;
			case Mid:
				yCoord = (int)boundingBox.getCenterY();
				break;
				default:
					yCoord = (int)boundingBox.getMaxY();
			}
			hitPoint =  new Point(leftX, yCoord);
		} else {
			int upperY = boundingBox.y;
			int xCoord = 0;
			switch (area) {
			case Left:
				xCoord = (int)boundingBox.getMinX();
				break;
			case Center:
				xCoord = (int)boundingBox.getCenterX();
				break;
				default:
					xCoord = (int)boundingBox.getMaxX();
			}
			hitPoint =  new Point(xCoord, upperY);
		}
		
		
		return hitPoint;
	}
	
	/**
	 * Calculate the coordinates where to hit the target object according to TargetArea 
	 * categorisation (high, mid, low -> for vertical objects or left, center, right for horizontal objects)
	 * -> using interpolation
	 * @param boundingBox boundingBox of the target object
	 * @param area the TargetArea categorisation to aim at
	 * @return the hit-point
	 */
	private Point getHitPointOfTarget(Rectangle boundingBox, TargetArea area) {
		double factor = 0.05; // High or Left
		
		switch(area) {
		case Low:
		case Right:
			factor = 0.95;
			break;
		case Mid:
		case Center:
			factor = 0.5;
			break;
			default:break;
		}
		
		Point hitPoint;
		if (area == TargetArea.High || area == TargetArea.Mid || area == TargetArea.Low) {
			int leftX = boundingBox.x;
			int yCoord = (int)Math.round(boundingBox.y - (factor * boundingBox.height));
			hitPoint =  new Point(leftX, yCoord);
		} else {
			int upperY = boundingBox.y;
			int xCoord = (int)Math.round(boundingBox.x + (factor * boundingBox.width));
			hitPoint =  new Point(xCoord, upperY);
		}
		
		
		return hitPoint;
	}
	
	// returns array with 0-2 elements, if 2 elements are inside the second one (index 1) is the high shot, 
	// the first one (index 0) is the low shot
	private ArrayList<Point> getReleasePointsForTrajectories(Point target, Rectangle sling) {
		return tp.estimateLaunchPoint(sling, target);
	}
	
	/**
	 * Check if the object can be hit ....
	 * @param target
	 * @param sling
	 * @return
	 */
	private boolean canHit(Point target, Rectangle sling) {
		return tp.estimateLaunchPoint(sling, target).size() > 0;
	}
	
	/**
	 * Deprecated : calculate the tap time 
	 * @param sling
	 * @param releasePoint
	 * @param targetPoint
	 * @return
	 */
	private int getTapTime(Rectangle sling, Point releasePoint, Point targetPoint) {
		int tapInterval = 0;
		switch (BamBird.getInstance().actionRobot.getBirdTypeOnSling()) 
		{

		case RedBird:
			tapInterval = 0; break;               // start of trajectory
		case YellowBird:
			tapInterval = 65 + randomGenerator.nextInt(25);break; // 65-90% of the way
		case WhiteBird:
			tapInterval =  70 + randomGenerator.nextInt(20);break; // 70-90% of the way
		case BlackBird:
			tapInterval =  70 + randomGenerator.nextInt(20);break; // 70-90% of the way
		case BlueBird:
			tapInterval =  65 + randomGenerator.nextInt(20);break; // 65-85% of the way
		default:
			tapInterval =  60;
		}

		int tapTime = tp.getTapTime(sling, releasePoint, targetPoint, tapInterval);
		
		return tapTime;
	}
	
	/**
	 * Estimate the tap-time according to the time of the flight
	 * @param flightTime time it takes the bird to hit the target point
	 * @return estimated time after which to tap
	 */
	private long getTapTime(long flightTime, ABType birdType) {
		int tapInterval = 0;
		switch (birdType) 
		{

		case RedBird:
			tapInterval = 0; break;               // start of trajectory
		case YellowBird:
			tapInterval = 65 + randomGenerator.nextInt(25);break; // 65-90% of the way
		case WhiteBird:
			tapInterval =  70 + randomGenerator.nextInt(20);break; // 70-90% of the way
		case BlackBird:
			tapInterval =  70 + randomGenerator.nextInt(20);break; // 70-90% of the way
		case BlueBird:
			tapInterval =  65 + randomGenerator.nextInt(20);break; // 65-85% of the way
		default:
			tapInterval =  60;
		}
		
		return (long)(flightTime * (tapInterval / 100.));
	}
	
	/**
	 * Estimate the tap-time according to the time of the flight
	 * @param flightTime time it takes the bird to hit the target point
	 * @param variance adapt the time (yellow [-10 ... 30] best [0 ... 25], white/black/blue [-10 ... 25] best [0 ... 20])
	 * @return estimated time after which to tap
	 */
	private long getTapTime(long flightTime, int variance, ABType birdType) {
		int tapInterval = 0;
		
		if (birdType == ABType.YellowBird) {
			variance = Math.min(30, Math.max(-10, variance));
		} else {
			variance = Math.min(25, Math.max(-10, variance));
		}
		
		
		switch (birdType) 
		{

		case RedBird:
			tapInterval = 0; break;               // start of trajectory
		case YellowBird:
			tapInterval = 65 + variance;break; // 65-90% of the way
		case WhiteBird:
			tapInterval = 70 + variance;break; // 70-90% of the way
		case BlackBird:
			tapInterval = 70 + variance;break; // 70-90% of the way
		case BlueBird:
			tapInterval = 65 + variance;break; // 65-85% of the way
		default:
			tapInterval = 60;
		}
		
		return (long)(flightTime * (tapInterval / 100.));
	}
	
	/**
	 * Deprecated: make an instance of Shot
	 * @param sling
	 * @param releasePoint
	 * @param tapTime
	 * @return
	 */
	private Shot shoot(Rectangle sling, Point releasePoint, int tapTime) {
		Point refPoint = tp.getReferencePoint(sling);
		
		int dx = (int)releasePoint.getX() - refPoint.x;
		int dy = (int)releasePoint.getY() - refPoint.y;
		System.out.println("We shoot --- save your arse .....");
		return new Shot(refPoint.x, refPoint.y, dx, dy, 0, tapTime);
	}
	
	
	/**
	 * Takes an action plan and simulates different shots to adapt the params
	 * 
	 * @param world the current state of the game as world
	 * @param target the object describing the targets and goals of the action to perform
	 * @return 'null' if (1) there no targets are given (either the DecisionObject is null or it does not return a list of object-IDs) / the passed in world is null / a bird cannot be found or (2) if the given targets cannot be found in the objects list / return an EvaluatedShot with a score of 0.0 if after the simulated shot the target has full health or if the target can no longer be found in the world, an EvaluatedShot otherwise
	 */
	public EvaluatedShot calculateShot(World world, DecisionObject target) {
		if (world == null || target == null) {
			System.out.println("ADAPTATION: method expected to receive a world and a target that are non-null");
			return null;
		}
		
		Shot shot = null;
		
		System.out.println("get ready to process target ....");
		
		//BufferedImage screenshot = ActionRobot.doScreenShot();

		// process image
		//Vision vision = new Vision(screenshot);
		//Rectangle sling = vision.findSlingshotMBR();
		//ABType birdType = aRobot.getBirdTypeOnSling();
		
		ArrayList<String> targets = target.getTargets();
		
		String mainTargetId = "";
		if (targets != null && !targets.isEmpty()) {
			mainTargetId = targets.get(0);
		} else {
			System.out.println("ADAPTATION: DecisionObject does not specify a target.");
			return null;
		}
		
		int numberOfGoals = target.getGoals().size();
		
		ABObject targetObject = null;
		
		List<List<ABObject>> objectsList = BamBird.getInstance().getCurrentABObjects();
		List<ABObject> objects_flat = Helper.flattenABObjectsList(objectsList);
		
		ABObject bird = Helper.getBirdOnSling(objectsList, ABType.RedBird);
		if (bird == null) {
			System.out.println("ADAPTATION: cannot get the bird (game-object) from lists");
			return null;
		}
		ABType birdType = bird.getType();
		
		String targetId = mainTargetId;
                System.out.println("ADAPTATION: Target ID: " + targetId);
		System.out.println("ADAPTATION: Maintarget ID: " + mainTargetId);
                
		List<ABObject> results =  Helper.extractGameObjectsWithId(objects_flat, targetId);
		if (!results.isEmpty()) {
			targetObject = results.get(0);
		} else {
			System.out.println("ADAPTATION: cannot get the geometry for the target's globalId");
			return null;
		}
		
		boolean isHorizontallyOriented = Helper.isObjectHorizontal(targetObject);
		int heightDifference = 1000; /// pretty much infinite ....
		if (isHorizontallyOriented) {
			heightDifference = bird.y - targetObject.y;
		}
		
		List<TargetArea> targetAreas = getTargetAreas(isHorizontallyOriented);
		
		List<CalculationStrategy> trajectories = Helper.getTrajectories(isHorizontallyOriented, heightDifference);
		
		double healthOfGoals = 0.0;
		double destructionIndicator = 0.0;
		ArrayList<EvaluatedShot> performedShots = new ArrayList<EvaluatedShot>();
		
		for (TargetArea area: targetAreas) {
			
			// if the destruction is high, just pass that and no longer evaluate
			if (destructionIndicator > 0.1) {
				break;
			}
			
			// get the coordinates for the bird (munition) and the hit-point on the target -> to calculate the shot
			Point birdLocation = bird.getCenter();
			Point hitPoint = this.getHitPointOfTarget(targetObject.getBounds(), area);
			
			for (CalculationStrategy cs: trajectories) {
				// seems highly unlikely to shoot with a lower trajectory shot targeted at the right side 
				// of a horizontally oriented target object
				if	(isHorizontallyOriented && cs == CalculationStrategy.LOWER && area == TargetArea.Right) {
					continue;
				}

				// calculate the shot
				shot = physicsEngine.calcShot(birdLocation, hitPoint, 21.5, cs);
				long tapTime = this.getTapTime(shot.getTOF(), birdType);
				shot.setT_tap(tapTime);
				
				// simulate the shot
				World worldWithShot = physicsEngine.addShotToWorld(world, shot, Helper.getBirdType(bird.type), cs, hitPoint);
				if (worldWithShot == null) {
					System.out.println("ADAPTATION: physicsEngine did not return a world with shot");
					return null;
				}
				World worldAfterShot = physicsEngine.getResultWorldState(worldWithShot);
				if (worldAfterShot == null) {
					System.out.println("ADAPTATION: physicsEngine did not return a world after shot");
					return null;
				}
				
				// analyse the result of the shot
				List<Body> afterShotObjects = world.getBodies();
				ArrayList<String> goalIds = target.getGoals();
				
				// if the list of objects after the shot is empty
				if (afterShotObjects.isEmpty()) {
					performedShots.add(new EvaluatedShot(shot, 0.0, worldAfterShot));
					continue;
				}
				
				// check if the target was hit -> if not, return an EvaluatedShot with a score of 0.0
				Body mainTarget = Helper.extractBodyWithId(afterShotObjects, targetId);
				if (mainTarget == null || ((GameObject)mainTarget).getHealthIndicator() > 0.99) { 
					performedShots.add(new EvaluatedShot(shot, 0.0, worldAfterShot));
					continue;
				} else {
					System.out.println("ADAPTATION: could not evaluated state of target after shot");
				}
				
				/// There are no goals to evaluate the shot -> therefore evaluate the destruction of the target
				if (numberOfGoals < 1) { 
					performedShots.add(new EvaluatedShot(shot, 1.0 - ((GameObject)mainTarget).getHealthIndicator(), worldAfterShot));
					continue;
				}
				
				// Iterate over the goals (if any) and check their health to fully evaluate the shot
				for (String goalId: goalIds) {
					Body possGoal =  Helper.extractBodyWithId(afterShotObjects, goalId);
					if (possGoal != null) {
						double health = ((GameObject)possGoal).getHealthIndicator();
						
						healthOfGoals += health;
					} else {
						System.out.println("ADAPTATION: the gaol with the given globalId could not be found within the world");
					}
				}
				
				// the score is the inverse of the health ....
				destructionIndicator =  1.0 - (healthOfGoals / (double)numberOfGoals);
				performedShots.add(new EvaluatedShot(shot, destructionIndicator, worldAfterShot));
				
				// if the destruction is high, just pass that and no longer evaluate
				if (destructionIndicator >= 0.99) {
					break;
				} else {
					destructionIndicator = 0.0;
				}
				healthOfGoals = 0.0;
			}
			
		}
		
		// short the performed shots in the simulation descendingly
		Helper.sortShotsDescending(performedShots);
		
		// return the shot with the highest score -> thus the most destructive shot ˜ according to the simulation
		return performedShots.get(0);
	}
	
	/**
	 * Get a list of TargetAreas dependent on the orientation of the target object
	 * @param isHorizontal
	 * @return list of TargetAreas
	 */
	private List<TargetArea> getTargetAreas(boolean isHorizontal) {
		
		if (_targetAreas == null || 
				(isHorizontal && !_targetAreas.contains(TargetArea.Center)) || 
				(!isHorizontal && !_targetAreas.contains(TargetArea.Mid))) {
			_targetAreas = new ArrayList<TargetArea> ();
			if (isHorizontal) {
				_targetAreas.add(TargetArea.Left);
				_targetAreas.add(TargetArea.Center);
				_targetAreas.add(TargetArea.Right);
			} else {
				_targetAreas.add(TargetArea.High);
				_targetAreas.add(TargetArea.Mid);
				_targetAreas.add(TargetArea.Low);
			}
		}
		
		return _targetAreas;
	}
	
	
	 
	/**
	 * Dummy agent shot calculation on random target
	 * @param world not used
	 * @param d not used
	 * @return Shot wrapped into an EvaluatedShot
	 */
	public EvaluatedShot calculateShot2(World world, DecisionObject d){
		Shot shot = new Shot();
		
		// capture Image
				BufferedImage screenshot = BamBird.getInstance().actionRobot.doScreenShot();

				// process image
				Vision vision = new Vision(screenshot);

				// find the slingshot
				Rectangle sling = this.getSling();
				
		        // get all the pigs
		 		List<ABObject> pigs = vision.findPigsMBR();

				//GameState state = aRobot.getState();

				// if there is a sling, then play, otherwise just skip.
				if (sling != null) {

					if (!pigs.isEmpty()) {

						Point releasePoint = null;
						
						
						{
							// random pick up a pig
							ABObject pig = pigs.get(randomGenerator.nextInt(pigs.size()));
							
							Point _tpt = pig.getCenter();// if the target is very close to before, randomly choose a
							// point near it
							if (prevTarget != null && distance(prevTarget, _tpt) < 10) {
								double _angle = randomGenerator.nextDouble() * Math.PI * 2;
								_tpt.x = _tpt.x + (int) (Math.cos(_angle) * 10);
								_tpt.y = _tpt.y + (int) (Math.sin(_angle) * 10);
								System.out.println("Randomly changing to " + _tpt);
							}

							prevTarget = new Point(_tpt.x, _tpt.y);

							// estimate the trajectory
							ArrayList<Point> pts = this.getReleasePointsForTrajectories(_tpt, sling);
							
							// do a high shot when entering a level to find an accurate velocity
							if (firstShot && pts.size() > 1) 
							{
								releasePoint = pts.get(1);
							}
							else if (pts.size() == 1)
								releasePoint = pts.get(0);
							else if (pts.size() == 2)
							{
								// randomly choose between the trajectories, with a 1 in
								// 6 chance of choosing the high one
								if (randomGenerator.nextInt(3) == 0)
									releasePoint = pts.get(1);
								else
									releasePoint = pts.get(0);
							}
							else
								if(pts.isEmpty())
								{
									System.out.println("No release point found for the target");
									System.out.println("Try a shot with 45 degree");
									releasePoint = tp.findReleasePoint(sling, Math.PI/4);
								}


							//Calculate the tapping time according the bird type 
							if (releasePoint != null) {
								double releaseAngle = tp.getReleaseAngle(sling,
										releasePoint);
								System.out.println("Release Point: " + releasePoint);
								System.out.println("Release Angle: "
										+ Math.toDegrees(releaseAngle));
								int tapTime = this.getTapTime(sling, releasePoint, _tpt);

								shot = this.shoot(sling, releasePoint, tapTime);
								
							}
							else
								{
									System.err.println("No Release Point Found");
									//return state;
								}
						}
					}
				}
				
				return new EvaluatedShot(shot, 0.8, null);
	}
	
	private double distance(Point p1, Point p2) {
		return Math
				.sqrt((double) ((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
						* (p1.y - p2.y)));
	}

	public Shot calculateShot(String objectId){
		ABObject targetObject = null;
		
		// List<ABObjects> objects = physicsEngine.getListOfObjects();
		// ABObject targetObject = objects.filter(objectId);
		// BoundingBox rect = targetObject.boudingBox;
		
		BufferedImage screenshot = BamBird.getInstance().actionRobot.doScreenShot();
		Vision vision = physicsEngine.getVisionForScreenShot(screenshot);
		List<List<ABObject>> objects = physicsEngine.extractFeaturesForVision(vision);
		
		//Point pt = targetObject.getCenter();
		//targetObject.
		
		
		
		//Shot shot = new Shot(pt.x, pt.y, 0, 0)
		return null;
	}
	
	
	
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}
}