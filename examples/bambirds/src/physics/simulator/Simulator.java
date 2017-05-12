package physics.simulator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Vector2;

import physics.materials.GameObject;

public class Simulator {

	private World world = new World();

	public World executeSimulation(World world) {
		this.world = world;

		System.out.println("PHYSICS - Simulation started at: " + LocalDateTime.now().toLocalTime());

		// for (int i = 0; i < 2000; i++) {
		// this.world.update(0.001);
		//
		// }
		//this.world.step(2000);

		System.out.println("PHYSICS - Simulation finished at: " + LocalDateTime.now().toLocalTime());

		return world;
	}

	// TODO Needs to be tested
	public void getSupportedPieces(World world) {
		List<Body> bodies = new ArrayList<Body>();
		List<Body> supObjects = new ArrayList<Body>();
		World tempWorld;

		for (int i = 0; i < world.getBodyCount(); i++) {
			// Resetting objects
			tempWorld = world;
			bodies = world.getBodies();
			supObjects.clear();

			// Removing the body from the world
			tempWorld.removeBody(world.getBody(i));

			executeSimulation(tempWorld);

			for (Body body : bodies) {
				if (body.getLinearVelocity() != new Vector2(0.0, 0.0)) {
					supObjects.add(body);
				}
			}

			((GameObject) world.getBody(i)).supObjects = supObjects;

		}

	}

}
