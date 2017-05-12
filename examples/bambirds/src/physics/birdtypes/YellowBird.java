package physics.birdtypes;

import java.awt.Color;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;

public class YellowBird extends BirdFacade{


	Triangle yellowBirdShape = new Triangle(new Vector2(0.0, 0.5), new Vector2(-0.5, -0.5), new Vector2(0.5, 0.5));
	BodyFixture yellowBirdFixture = new BodyFixture(yellowBirdShape);

	public YellowBird(double xCoordinate, double yCoordinate, double density, double friction, double restitution) {
		// randomly generate the color
		this.color = new Color(255, 255, 0);

		yellowBirdFixture.setFriction(friction);
		yellowBirdFixture.setDensity(density);
		yellowBirdFixture.setRestitution(restitution);

		this.addFixture(yellowBirdFixture);
		this.setMass(MassType.NORMAL);
		this.translate(xCoordinate, yCoordinate);

	}

	public void trigger() {
		Vector2 nitro = new Vector2(1.0, 1.0);
		applyForce(nitro);
	}
}
