package physics.birdtypes;

import java.awt.Color;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

public class WhiteBird extends BirdFacade{

	Ellipse whiteBirdShape = new Ellipse(0.6, 0.9);
	BodyFixture whiteBirdFixture = new BodyFixture(whiteBirdShape);

	public WhiteBird(double xCoordinate, double yCoordinate, double density, double friction, double restitution) {
		this.color = new Color(255, 255, 255);

		whiteBirdFixture.setFriction(friction);
		whiteBirdFixture.setDensity(density);
		whiteBirdFixture.setRestitution(restitution);

		this.addFixture(whiteBirdFixture);
		this.setMass(MassType.NORMAL);
		this.translate(xCoordinate, yCoordinate);

	}

	public WhiteBirdEgg trigger() {
		Vector2 undweg = new Vector2(5.0, 100.0);
		applyForce(undweg);
		WhiteBirdEgg egg = new WhiteBirdEgg();
		return egg;
	}

}
