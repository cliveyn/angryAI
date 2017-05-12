package main;

/*import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.dyn4j.dynamics.World;

import ab.demo.other.ActionRobot;
import ab.demo.other.Shot;
import ab.vision.ABObject;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;
import adaptation.Adaptation;
import knowledge.Knowledge;
import physics.Physics;
import physics.birdtypes.BirdTypes;
import physics.calculation.Calculation;
import physics.calculation.CalculationStrategy;
import physics.visualisation.PhysicsSimulation;
import physics.worldFactory.ParameterFitter;
import physics.worldFactory.WorldSettings;

public class BamBirdPhysic {
	private Physics physics;
	private Knowledge knowledge;
	// private Planner planner;
	private Adaptation heuristic;
	private ActionRobot actionRobot;

	// Levels 8 are unstable
	private int currentLevel = 1;

	private static BamBirdPhysic instance = null;

	private ParameterFitter parameterFitter = new ParameterFitter();

	private BamBirdPhysic() {

	}

	public static BamBirdPhysic getInstance() {
		if (instance == null) {
			instance = new BamBirdPhysic();
		}
		return instance;
	}

	private void fitParameters() {
		long tDelta = 1000;

		Vision vision = physics.getVisionForScreenShot(ActionRobot.doScreenShot());

		double yInit = vision.findBirdsRealShape().get(0).getCenterY();

		WorldSettings worldSettings = new WorldSettings();
		// do shooting here
		Calculation.calcParallelShootingVector(worldSettings.birdVelocity);

		try {
			Thread.sleep(tDelta);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		vision = physics.getVisionForScreenShot(ActionRobot.doScreenShot());
		double yEnd = vision.findBirdsRealShape().get(0).getCenterY();

		parameterFitter.fitGlobalGravity(yInit, yEnd, tDelta);
	}

	private void doStateHandling(GameState state) {
		if (state == GameState.WON) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			actionRobot.loadLevel(++currentLevel);
		} else if (state == GameState.LOST) {
			actionRobot.restartLevel();
		} else if (state == GameState.MAIN_MENU || state == GameState.EPISODE_MENU) {
			ActionRobot.GoFromMainMenuToLevelSelection();
			actionRobot.loadLevel(currentLevel);
		}
	}

	private void physicsTest() {

		physics = new Physics();
		WorldSettings worldSettings = new WorldSettings();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		String[] simulationParameters = new String[2];

		// Scale in pixels per meter
		simulationParameters[0] = "11"; // SCALE
		simulationParameters[1] = "1.0e9"; // IntNano Base

		try {
			BufferedImage image = ImageIO
					.read(new File("src/physics/featureExtraction/screenshots/saved" + currentLevel + ".png"));

			Vision vision = physics.getVisionForScreenShot(image);
			List<List<ABObject>> objects = physics.extractFeaturesForVision(vision);

			// for(int i = 0; i < 10; i++) {
			// long pre = System.currentTimeMillis();
			// Simulator sim = new Simulator();
			// WorldFactory wf = new WorldFactory();
			// World world = wf.generateWorld(objects);
			// sim.executeSimulation(world);
			// System.out.println("sim " + i);
			// long post = System.currentTimeMillis();
			// System.out.println("Time: " + (post - pre));
			// }

			PhysicsSimulation window = new PhysicsSimulation(simulationParameters);
			// Simulator sim = new Simulator();

			double meterScale = Math.round(vision.findSlingshotMBR().getHeight() / 4.9);
			System.out.println("Meter scale is " + meterScale + " pixels/meter");

			World world = physics.buildWorld(objects);

			List<ABObject> pigsList = objects.get(0);
			List<ABObject> birdList = objects.get(1);

			double lowestY = Integer.MAX_VALUE;
			double scale = 0.05;

			for (ABObject bird : birdList) {
				if (lowestY > -bird.getY() * scale) {
					lowestY = -bird.getY() * scale;
				}
			}
			lowestY -= 40;

			Point pig = pigsList.get(0).getCenter();
			Point bird = birdList.get(birdList.size() - 1).getCenter();

			// fit pigs and birds into coordinate system
			Point releaseLoc = new Point((int) (scale * bird.x), (int) (scale * bird.y + lowestY));
			Point hitLoc = new Point((int) (scale * pig.x), (int) (scale * pig.y + lowestY));

			double velocity = 21.5;

			CalculationStrategy strat = CalculationStrategy.LOWER;

			Shot shot = physics.calcShot(releaseLoc, hitLoc, velocity, strat);
			world = physics.addShotToWorld(world, shot, BirdTypes.REDBIRD, strat, hitLoc);

			physics.printGlobalIDsToConsole(world);

			// sim.executeSimulation(world);
			window.setWorld(world);
			window.setVisible(true);
			window.start();
		} catch (IOException e) {
			System.out.println("Could not load image");
		}

	}

	private void setup() throws IOException {
		physics = new Physics();
		knowledge = new Knowledge();
		// planner = new Planner();
		heuristic = new Adaptation();
		actionRobot = new ActionRobot();
	}

	private void shutdown() {
		physics.shutdown();
		knowledge.shutdown();
		// planner.shutdown();
		heuristic.shutdown();
	}

	public void makeScreenshot(int level) {

		try {
			setup();
			actionRobot.loadLevel(level);
			doStateHandling(actionRobot.getState());
			ActionRobot.fullyZoomOut();
			BufferedImage image = ActionRobot.doScreenShot();
			File outputfile = new File("src/physics/featureExtraction/screenshots/saved" + level + ".png");
			ImageIO.write(image, "png", outputfile);

		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			shutdown();
		}
	}

	public static void main(String[] args) {
		BamBirdPhysic.getInstance().physicsTest();
	}

	public Physics getPhysics() {
		return physics;
	}

	public Knowledge getKnowledge() {
		return knowledge;
	}

	// public Planner getPlanner() {
	// return planner;
	// }

	public Adaptation getHeuristic() {
		return heuristic;
	}
}*/
