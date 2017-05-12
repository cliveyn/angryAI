package physics.birdtypes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.MassType;

public class BlueBird extends BirdFacade {

	Circle blueBirdShape = new Circle(0.2);
	BodyFixture blueBirdFixture = new BodyFixture(blueBirdShape);

	public BlueBird(double xCoordinate, double yCoordinate, double density, double friction, double restitution) {

		this.color = new Color(0, 0, 255);

		blueBirdFixture.setFriction(friction);
		blueBirdFixture.setDensity(density);
		blueBirdFixture.setRestitution(restitution);

		this.addFixture(blueBirdFixture);
		this.setMass(MassType.NORMAL);
		this.translate(xCoordinate, yCoordinate);

	}

	public List<BlueBird> trigger(double xOnWay, double yOnWay) {
		//
		// Vector2 oben = new Vector2(1.0, 2.0);
		// Vector2 unten = new Vector2(1.0, 0.5);
		//
		// BlueBird b1 = this.clone();
		// BlueBird b2 = this.clone();
		//
		// b1.applyForce(oben);
		// b2.applyForce(unten);

		List<BlueBird> list = new ArrayList<BlueBird>();
		// list.add(b1);
		// list.add(b2);
		return list;
	}

	//// @Override
	// public BlueBird clone() {
	// BlueBird bird = new BlueBird(this.getWorldPoint(arg0), yCoordinate,
	//// density, friction, restitution)
	// }

}
