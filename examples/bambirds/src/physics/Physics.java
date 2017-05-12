package physics;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.List;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;

import ab.demo.other.Shot;
import ab.vision.ABObject;
import ab.vision.Vision;
import main.BamBirdModule;
import physics.birdtypes.BirdTypes;
import physics.calculation.Calculation;
import physics.calculation.CalculationStrategy;
import physics.featureExtraction.Extractor;
import physics.materials.GameObject;
import physics.simulator.Simulator;
import physics.worldFactory.WorldFactory;
import physics.worldFactory.WorldSettings;

public class Physics extends BamBirdModule {

	private final Extractor extractor;
	public WorldSettings worldSettings;

	private WorldFactory worldFactory;
	private Simulator simulator;
	
	private Vision vision;

	/**
	 * Default constructor.
	 */
	public Physics() {
		extractor = new Extractor();
		worldSettings = new WorldSettings();
		worldFactory = new WorldFactory();
		simulator = new Simulator();
	}

	/**
	 * This method builds a world using an instance of WorldSettings and lists,
	 * which contain all ABObjects. The returned world is equivalent to the
	 * initial state of the world.
	 *
	 * @param worldSettings
	 * @param abList
	 */
	public World buildWorld(List<List<ABObject>> abList) {
		World initialWorld = worldFactory.generateWorld(abList, new Rectangle());
		return initialWorld;
	}
	
	public boolean hitted(Object bullet, Object victim){
		boolean hit = false;
		
		// check listeners if collision happened
		
		return hit;
	}

	/**
	 * This method adds a shot to a world in order to simulate its effects on
	 * the world using the method {@link getResultWorldState(World world)} later
	 * on.
	 *
	 * @param shot
	 * @return
	 */
	public World addShotToWorld(World world, Shot shot, BirdTypes birdType,
			CalculationStrategy calcStrat, Point2D hitLoc) {
		world = worldFactory.addShot(world, shot, birdType, calcStrat,
				hitLoc);
		return world;
	}

	/**
	 * Generates a shot (that may be added to the world afterwards)
	 * 
	 * @param releaseLoc
	 *            Location of the bird currently in the sling
	 * @param hitLoc
	 *            target
	 * @param velocity
	 *            velocity to be shot with
	 * @param calcStrat
	 *            upper or lower trajectory
	 * @return Shot with drag parameters as dX and dY, current time, time of flight
	 */
	public Shot calcShot(Point2D releaseLoc, Point2D hitLoc, double velocity,
			CalculationStrategy calcStrat) {
		Shot shot = new Shot();
		shot.setT_shot(System.nanoTime());
		shot.setX(((Double)releaseLoc.getX()).intValue());
		shot.setY(((Double)releaseLoc.getY()).intValue());
		shot.setVelocity(velocity);

		// calculate trajectory
		double shootingAngle = 0;
		if (calcStrat == CalculationStrategy.LOWER) {
			shootingAngle = Calculation.calcLowerTrajectoryAngle(releaseLoc,
					hitLoc, shot.getVelocity());
		} else if (calcStrat == CalculationStrategy.UPPER) {
			shootingAngle = Calculation.calcUpperTrajectoryAngle(releaseLoc,
					hitLoc, shot.getVelocity());
		}
		
		System.out.println("PHYSICS - Shooting Angle is: " + shootingAngle);

		// calculate release point
		Point temp = Calculation.calcDragForLaunchPoint(shootingAngle, shot);
		shot.setDx(-temp.x);
		shot.setDy(temp.y);

		//calculate and set time of flight
		shot.setTOF(Calculation.calculateTimeOfFlight(velocity, shootingAngle));
		
		System.out.println(shot);

		return shot;
	}

	/**
	 * This method takes a world, conducts a simulation and returns the result
	 * state of the world.
	 *
	 * @param world
	 * @return
	 */
	public World getResultWorldState(World world) {
		World resultWorld = simulator.executeSimulation(world);
		return resultWorld;
	}

	/**
	 * This method writes the passed instance of WorldSettings to a text file.
	 *
	 * @param worldSettings
	 */
	public void writeWorldSettingsToTextFile(WorldSettings worldSettings) {

		try (PrintWriter pw = new PrintWriter(new FileWriter(
				"src/physics/worldFactory/worldSettings.txt"))) {

			// Writing every field and its value of a WorldSettings object in a
			// new line of the worldSettings.txt
			for (Field field : worldSettings.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Object value = field.get(worldSettings);
				if (value != null) {
					pw.println(field.getName() + " " + value);
				}
			}

			// For debugging
			// System.out.println("Writing WorldSettings to text file done!");
		} catch (IOException | IllegalArgumentException
				| IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method reads the contents of a text file and returns them as an
	 * instance of WorldSettings.
	 *
	 * @return
	 */
	public WorldSettings readWorldSettingsFromTextFile(String url) {
		try (BufferedReader br = new BufferedReader(new FileReader(url))) {

			String line;
			Field field;
			String[] splittedLine;

			// Translating a line of the text file to the corresponding fields
			// of the world setting objects
			while ((line = br.readLine()) != null) {
				splittedLine = line.split(" ");

				field = worldSettings.getClass().getDeclaredField(
						splittedLine[0]);
				field.setAccessible(true);

				double value = Double.parseDouble(splittedLine[1]);
				field.set(worldSettings, value);
			}

		} catch (IOException | IllegalArgumentException | NoSuchFieldException
				| SecurityException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return worldSettings;
	}

	/**
	 * Adds a list of all pieces, which are supported by a GameObject, to the
	 * specified GameObject. This method should only be called, iff necessary,
	 * since it can lead to performance problems.
	 *
	 * @param world
	 */
	public void getSupportedPieces(World world) {
		simulator.getSupportedPieces(world);
	}

	public Vision getVisionForScreenShot(BufferedImage screenshot) {
		this.vision = new Vision(screenshot);
		return vision;
	}

	public List<List<ABObject>> extractFeaturesForVision(Vision vision) {
		extractor.extractFeaturesForVision(vision);
		return extractor.getABLists();
	}
	
	public Vision getVision(){
		return vision;
	}
	
	public void printGlobalIDsToConsole(World world){
		System.out.print("PHYSICS - Available global IDs: ");
		for(Body obj : world.getBodies()){
			GameObject gobj = (GameObject) obj;
			System.out.print(gobj.globalID +" ");
		}
		System.out.println();
	}
	
	public double getLowestY(){
		return worldFactory.getLowestY();
	}

	@Override
	public void shutdown() {

	}

}
