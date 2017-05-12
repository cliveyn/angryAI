package main;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.dyn4j.dynamics.World;

import ab.demo.other.ClientActionRobot;
import ab.demo.other.ClientActionRobotJava;
import ab.demo.other.Shot;
import ab.planner.TrajectoryPlanner;
import ab.utils.ABUtil;
import ab.vision.ABObject;
import ab.vision.ABType;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;
import adaptation.Adaptation;
import adaptation.EvaluatedShot;
import database.Database;
import database.Match;
import database.Match.MatchState;
import knowledge.BadWorldException;
import knowledge.Knowledge;
import meta.Level;
import meta.LevelSelection;
import physics.Physics;
import physics.birdtypes.BirdTypes;
import physics.calculation.CalculationStrategy;
import physics.worldFactory.WorldSettings;
import planner.DecisionObject;
import planner.PlannerStringConverter;

public class BamBird {
	private Physics physics;
	private List<List<ABObject>> currentABObjects;

	private SWIConnector connector;

	private Knowledge knowledge;
	private PlannerStringConverter plannerStringConverter;
	private Adaptation adaption;
	public ClientActionRobotJava actionRobot;

	private LevelSelection levelSelection;
	private ArrayList<Level> levels;

	private int shotsExecuted = 0;

	private static BamBird instance = null;

	private static boolean enableAdaption = false;

	public static String serverHost = "";

	private static int team_id = 424242;

	private BamBird() {

	}

	public static BamBird getInstance() {
		if (instance == null) {
			instance = new BamBird();
		}
		return instance;
	}

	private void loadNextLevel() {
		if (levelSelection.getCurrentLevel() != 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			levels.get(levelSelection.getCurrentLevel() - 1).incrementPlayedCounter();
			int[] scores = actionRobot.checkMyScore();
			for(int i = 0; i<levels.size() && i<scores.length; i++){
				levels.get(i).setScore(scores[i]);
			}
		}

		int level = levelSelection.selectNextLevel(levels);

		actionRobot.loadLevel((byte) level);

		System.out.println("Loading Level " + level);
		
		for (Level l : levels){
			System.out.println(l);
		}
	}

	private void doStateHandling(World simulatedAfterWorld, World realAfterWorld) {
		GameState state = actionRobot.checkState();
		if (state == GameState.MAIN_MENU || state == GameState.EPISODE_MENU || state == GameState.LEVEL_SELECTION) {
			shotsExecuted = 0;

			loadNextLevel();
		} else if (state == GameState.WON) {
			shotsExecuted = 0;
			loadNextLevel();
		} else if (state == GameState.LOST) {
			shotsExecuted = 0;

			loadNextLevel();
		} else if (state == GameState.PLAYING) {
			// double equality =
			// BamBird.compareWorldsForEquality(simulatedAfterWorld,
			// realAfterWorld);
		} else {
			shotsExecuted = 0;

			loadNextLevel();
		}
	}

	private ABObject getObjectWithID(String id) {
		for (List<ABObject> l : currentABObjects) {
			for (ABObject o : l) {
				if (o.globalID.equals(id)) {
					return o;
				}
			}
		}
		return null;
	}

	private Shot calcShot(Rectangle sling, ABObject bird, Point launch, Point target) {
		int dx = (int) launch.getX() - (int) bird.getCenterX();
		int dy = (int) launch.getY() - (int) bird.getCenterY();

		TrajectoryPlanner tp = new TrajectoryPlanner();
		
		int tapInterval = 0;
		
		ABType type = bird.getType();
		if(type == ABType.BlackBird){
			tapInterval = 105;
		}else if(type == ABType.BlueBird){
			tapInterval = 60;
		}else if(type == ABType.WhiteBird){
			tapInterval = 90;
		}else if(type == ABType.YellowBird){
			tapInterval = 90;
		}
		
		int tapTime = tp.getTapTime(sling, launch, target, tapInterval);
		
		return new Shot((int) (bird.getCenter().x - bird.getWidth() / 2), bird.getCenter().y, dx, dy, 0, tapTime);
	}

	private void start(String pathToSwipl) {
		try {
			setup(pathToSwipl);

			byte[] info = actionRobot.configure(ClientActionRobot.intToByteArray(team_id));

			levels = new ArrayList<Level>(info[2]);
			for (int i = 0; i < info[2]; i++) {
				levels.add(new Level());
			}

			levelSelection = new LevelSelection(info[2]);

			World beforeWorld = null;
			World simulatedAfterWorld = null;
			World realAfterWorld = null;

			Shot shot = null;

			while (true) {
				shot = null;
				
				Thread.sleep(2000);
				
				doStateHandling(simulatedAfterWorld, realAfterWorld);

				if (shotsExecuted > 10) {
					loadNextLevel();
					continue;
				}

				Vision vision = physics.getVisionForScreenShot(actionRobot.doScreenShot());

				Rectangle sling = vision.findSlingshotMBR();
				long start = System.currentTimeMillis();
				while (sling == null && actionRobot.checkState() == GameState.PLAYING) {
					System.out.println("No slingshot detected. Please remove pop up or zoom out");
					BufferedImage screenshot = actionRobot.doScreenShot();
					vision = new Vision(screenshot);
					sling = vision.findSlingshotMBR();

					if (System.currentTimeMillis() - start >= 5000) {
						System.out.println("NO slingshot detected after 5 seconds. Choose different Level");
						loadNextLevel();
						break;
					}
				}
				if(sling == null || actionRobot.checkState() != GameState.PLAYING){
					continue;
				}

				currentABObjects = physics.extractFeaturesForVision(vision);
				if (currentABObjects == null || currentABObjects.size() == 0) {
					System.out.println("no objects detected ... continue");
					continue;
				}

				if (levels.get(levelSelection.getCurrentLevel() - 1).getNumberOfTimesPlayed() == 0) {
					Level level = new Level();
					level.setEstimatedMaximalPoints(currentABObjects);
					levels.set(levelSelection.getCurrentLevel() - 1, level);
				}

				try {
					Path file = knowledge.buildModell(currentABObjects, vision,
							"situation" + levelSelection.getCurrentLevel());
					if(file == null){
						continue;
					}

					connector.writeCommand("'" + file.toString() + "'.");
					long timeOut;
					if(levelSelection.getIncrementByOne()){
						timeOut = 7000;
					}else{
						timeOut = 30000;
					}
					String result = connector.getResult(timeOut);
					System.out.println(result);
					ArrayList<DecisionObject> plannerDecisions = plannerStringConverter
							.convertString(result);

					if (plannerDecisions != null && plannerDecisions.size() > 1) {
						plannerDecisions.remove(plannerDecisions.size()-1);
						
						DecisionObject object = null;
						if (levels.get(levelSelection.getCurrentLevel() - 1).getNumberOfTimesPlayed() == 0) {
							object = plannerDecisions.get(0);
							System.out.println("Selected Decision Object: " + object);
						}else{
							object = plannerDecisions.get((new Random()).nextInt(plannerDecisions.size()));
							System.out.println("Randomly Selected Decision Object: " + object);
						}
						if (enableAdaption) {
							beforeWorld = physics.buildWorld(currentABObjects);
							EvaluatedShot evShot = adaption.calculateShot(beforeWorld, object);
							shot = evShot.getShot();
						} else {
							ArrayList<String> targets = object.getTargets();
							if (targets != null && targets.size() > 0) {
								System.out.println("Target String: " + object.toString());
								ABObject target = getObjectWithID(targets.get(0));
								if (target != null) {

									System.out.println("Target: " + target.globalID + "(" + target.x + "," + target.y
											+ "," + target.height + "," + target.width + ")");

									if (target != null) {
										System.out.println("no adaption, faking shot");
										TrajectoryPlanner tp = new TrajectoryPlanner();

										List<Point> rel = tp.estimateLaunchPoint(sling, target.getCenter());

										List<ABObject> birds = currentABObjects.get(1);
										ABObject bird = birds.get(0);
										for (ABObject b : birds) {
											if (b.getCenterY() < bird.getCenterY()) {
												bird = b;
											}
											System.out.println(b.globalID + ": " + b.getCenter());
										}

										Shot shot1;
										Shot shot2;
										if (rel.size() >= 2) {
											shot1 = calcShot(sling, bird, rel.get(0), target.getCenter());
											shot2 = calcShot(sling, bird, rel.get(1), target.getCenter());
											
											if(!ABUtil.isReachable(vision, target.getCenter(), shot1)){
												if(!ABUtil.isReachable(vision, target.getCenter(), shot2)){
													shot = shot1;
												}else{
													System.err.println("Not reachable with normal Shot. Trying high shot...");
													shot = shot2;
												}
											}else{
												shot = shot1;
											}
										}else{
											shot = calcShot(sling, bird, rel.get(0), target.getCenter());
										}
									}
								}
							}
						}
					}

					if (shot == null) {
						shot = getDemoShot();
						System.err.println("Error in Pipeline … continuing with NaiveAgent");
					}

					if (shot != null) {
						// Shot is executed in the real game
						actionRobot.shoot(shot.getX(), shot.getY(), shot.getDx(), shot.getDy(), 0,
								(int) shot.getT_tap(), false);
						shot = null;
					}
					shotsExecuted++;
				} catch (BadWorldException e) {
					continue;
				}

			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			shutdown();
		}
	}

	public static double compareWorldsForEquality(World beforeWorld, World afterWorld) {
		return WorldEqualityComparator.compareWorldsForEquality(beforeWorld, afterWorld);
	}

	private void setup(String pathToSwipl) throws IOException {
		physics = new Physics();
		knowledge = new Knowledge();
		plannerStringConverter = new PlannerStringConverter();
		adaption = new Adaptation();
		actionRobot = new ClientActionRobotJava(serverHost);

		connector = new SWIConnector(pathToSwipl, BamBirdPaths.prologFunctions);

		(new Thread(connector)).start();
	}

	private void shutdown() {
		physics.shutdown();
		knowledge.shutdown();
		plannerStringConverter.shutdown();
		adaption.shutdown();
	}

	public static void main(String[] args) {
		if (args.length < 3) {
			System.out
					.println("Please provide valid arguments. [server_host, team_id, path_to_swipl, enable_adaption]");
			return;
		}

		serverHost = args[0];
		team_id = new Integer(args[1]);

		if (args.length >= 4) {
			if (args[3].equals("1")) {
				BamBird.enableAdaption = true;
			}
		}
        System.out.println("Version 16_7_14_10_10");
		BamBird.getInstance().start(args[2]);
	}

	private Shot getDemoShot() {
		// capture Image
		Random randomGenerator = new Random();
		TrajectoryPlanner tp = new TrajectoryPlanner();

		BufferedImage screenshot = actionRobot.doScreenShot();

		Vision vision = new Vision(screenshot);

		Rectangle sling = vision.findSlingshotMBR();

		long start = System.currentTimeMillis();

		while (sling == null && actionRobot.checkState() == GameState.PLAYING) {
			System.out.println("No slingshot detected. Please remove pop up or zoom out");
			screenshot = actionRobot.doScreenShot();
			vision = new Vision(screenshot);
			sling = vision.findSlingshotMBR();

			if (System.currentTimeMillis() - start >= 5000) {
				System.out.println("NO slingshot detected after 5 seconds. Choose different Level");
				loadNextLevel();
				return null;
			}
		}
		List<ABObject> pigs = vision.findPigsMBR();
		if (sling != null) {

			if (!pigs.isEmpty()) {

				Point releasePoint = null;
				Shot shot = new Shot();
				int dx, dy;
				{
					ABObject pig = pigs.get(randomGenerator.nextInt(pigs.size()));

					Point _tpt = pig.getCenter();
					ArrayList<Point> pts = tp.estimateLaunchPoint(sling, _tpt);

					if (pts.size() == 1)
						releasePoint = pts.get(0);
					else if (pts.size() == 2) {
						if (randomGenerator.nextInt(6) == 0)
							releasePoint = pts.get(1);
						else
							releasePoint = pts.get(0);
					} else if (pts.isEmpty()) {
						System.out.println("No release point found for the target");
						System.out.println("Try a shot with 45 degree");
						releasePoint = tp.findReleasePoint(sling, Math.PI / 4);
					}
					Point refPoint = tp.getReferencePoint(sling);
					if (releasePoint != null) {
						double releaseAngle = tp.getReleaseAngle(sling, releasePoint);
						System.out.println("Release Point: " + releasePoint);
						System.out.println("Release Angle: " + Math.toDegrees(releaseAngle));
						int tapInterval = 0;
						switch (actionRobot.getBirdTypeOnSling()) {
						case RedBird:
							tapInterval = 0;
							break;
						case YellowBird:
							tapInterval = 65 + randomGenerator.nextInt(25);
							break;
						case WhiteBird:
							tapInterval = 70 + randomGenerator.nextInt(20);
							break;
						case BlackBird:
							tapInterval = 70 + randomGenerator.nextInt(20);
							break;
						case BlueBird:
							tapInterval = 65 + randomGenerator.nextInt(20);
							break;
						default:
							tapInterval = 60;
						}

						int tapTime = tp.getTapTime(sling, releasePoint, _tpt, tapInterval);
						dx = (int) releasePoint.getX() - refPoint.x;
						dy = (int) releasePoint.getY() - refPoint.y;
						shot = new Shot(refPoint.x, refPoint.y, dx, dy, 0, tapTime);
						return shot;
					} else {
						System.err.println("No Release Point Found");
						return null;
					}
				}
			}
		}
		return null;
	}

	public Physics getPhysics() {
		return physics;
	}

	public Knowledge getKnowledge() {
		return knowledge;
	}

	public PlannerStringConverter getPlanner() {
		return plannerStringConverter;
	}

	public Adaptation getAdaption() {
		return adaption;
	}

	public List<List<ABObject>> getCurrentABObjects() {
		return currentABObjects;
	}
}
