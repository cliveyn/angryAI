package physics.birdtypes;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Ellipse;
import org.dyn4j.geometry.Vector2;

import physics.materials.GameObject;

public class WhiteBirdEgg extends GameObject {

	Ellipse whiteBirdEggShape = new Ellipse(0.2, 0.3);
	BodyFixture whiteBirdFixture = new BodyFixture(whiteBirdEggShape);

	public WhiteBirdEgg() {
		Vector2 bumbum = new Vector2(0.0, -10.0);
		applyForce(bumbum);

	}
}
