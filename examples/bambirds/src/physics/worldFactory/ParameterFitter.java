package physics.worldFactory;

import physics.Physics;

public class ParameterFitter {

	Physics controller;
	WorldSettings worldSettings;

	public ParameterFitter() {
		controller = new Physics();
		worldSettings = controller
				.readWorldSettingsFromTextFile("src/physics/worldFactory/worldSettings.txt");
	}

	// Fits the global gravity parameter using the red bird as reference
	public void fitGlobalGravity(double yInit, double yEnd, double tDelta) {
		double yDelta = yInit - yEnd;

		// Value in m/s^2
		double gravity = yDelta / (0.5 * tDelta);
		System.out.println("Gravity calculated: " + gravity);

		controller.writeWorldSettingsToTextFile(worldSettings);
	}
}
