package physics.birdtypes;

import java.awt.Color;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.MassType;

public class BlackBird extends BirdFacade {

	Circle blackBirdShape = new Circle(0.8);
	BodyFixture blackBirdFixture = new BodyFixture(blackBirdShape);

	public BlackBird(double xCoordinate, double yCoordinate, double density, double friction, double restitution) {

		this.color = new Color(0, 0, 0);

		blackBirdFixture.setFriction(friction);
		blackBirdFixture.setDensity(density);
		blackBirdFixture.setRestitution(restitution);

		this.addFixture(blackBirdFixture);
		this.setMass(MassType.NORMAL);
		this.translate(xCoordinate, yCoordinate);

	}

}
