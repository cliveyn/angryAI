package physics;

import physics.worldFactory.WorldSettings;

/**
 *
 * @author Team Physics (Stefan Gruss, Leon Martin, Robert Mueller, Sascha
 *         Riechel)
 *
 *         This class is just for testing.
 *
 */
public class SimulationSandbox {

	public static void main(String[] args) {

		Physics controller = new Physics();
		//
		// Vision vision = null;
		// try {
		// vision = controller.getVisionForScreenShot(
		// ImageIO.read(new
		// File("src/BamBird/src/physics/featureExtraction/screenshots/saved" +
		// 1 + ".png")));
		// } catch (IOException e) {
		// System.out.println("Could not load image");
		// }
		//
		// String[] simulationParameters = new String[2];
		// // Scale in pixels per meter
		// simulationParameters[0] = "0.5"; // SCALE
		// simulationParameters[1] = "1.0e9"; // IntNano Base
		//
		// List<List<ABObject>> objects =
		// controller.extractFeaturesForVision(vision);
		// PhysicsSimulation window = new
		// PhysicsSimulation(simulationParameters);
		// WorldFactory wf = new WorldFactory();
		//
		// World world = wf.generateWorld(objects);
		// wf.addShot(world, shot, birdType, calcStrat);

		// FIT FIT FIT
		// ParameterFitter parameterFitter = new ParameterFitter();
		// parameterFitter.fitGlobalGravity();

		// window.setWorld(world);
		// window.setVisible(true);
		// window.start();

		WorldSettings worldSettings = new WorldSettings();
		controller.writeWorldSettingsToTextFile(worldSettings);

		//
		// SimulationController controller = new SimulationController(new
		// WorldSettings());
		// SimulationController controller = new
		// SimulationController(TestWorldFactory.initializeWorldOne());
		//
		// // Printing the x-/y-coordinates of object with ID 1 at the initial
		// // state
		// System.out.println("-- Initial state --");
		// System.out.print("X: " +
		// controller.getInitialWorld().getBodies().get(1).getWorldCenter().x +
		// " ");
		// System.out.println("Y: " +
		// controller.getInitialWorld().getBodies().get(1).getWorldCenter().y);
		//
		// // Printing the time stamp of the simulation
		// System.out.println("Point of time: " +
		// controller.getInitialWorld().getAccumulatedTime());
		//
		// controller.initiateSimulation();
		//
		// // Printing the x-/y-coordinates of object with ID 1 after the
		// // simulation (=result state)
		// // is over
		// System.out.println("-- Result state --");
		// System.out.print("X: " +
		// controller.getResultWorld().getBodies().get(1).getWorldCenter().x +
		// "");
		// System.out.println("Y: " +
		// controller.getResultWorld().getBodies().get(1).getWorldCenter().y);
		//
		// // Printing the time stamp of the simulation
		// System.out.println("Point of time: " +
		// controller.getResultWorld().getAccumulatedTime());
	}
}
