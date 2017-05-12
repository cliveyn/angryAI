package physics.birdtypes;

import java.awt.Color;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.MassType;

public class RedBird extends BirdFacade{

	Circle redBirdShape;
	BodyFixture redBirdFixture;

	public RedBird() {
		// TODO Remove
	}

	public RedBird(double x, double y, double density, double friction, double restitution) {
		redBirdShape = new Circle(8 * 0.05);
		redBirdFixture = new BodyFixture(redBirdShape);
		// randomly generate the color
		this.color = new Color(255, 0, 0);

		redBirdFixture.setDensity(density);
		redBirdFixture.setFriction(friction);
		redBirdFixture.setRestitution(restitution);

		this.addFixture(redBirdFixture);
		this.setMass(MassType.NORMAL);
		this.translate(x, -y);

	}

}
