package physics.pigtypes;

import java.awt.Color;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.MassType;

public class BigPig extends PigFacade {

	Circle pigCircle;
	BodyFixture pigFixture;

	public BigPig() {
		health = 5200;
		damageThreshold = 2;
		this.color = new Color(0, 255, 0);
	}

	public BigPig(double x, double y, double width, double density, double friction, double restitution) {
		health = 5200;
		damageThreshold = 2;
		this.color = new Color(0, 255, 0);

		pigCircle = new Circle(width * 0.8);
		pigFixture = new BodyFixture(pigCircle);

		pigFixture.setDensity(density);
		pigFixture.setFriction(friction);
		pigFixture.setRestitution(restitution);

		this.addFixture(pigFixture);
		this.setMass(MassType.NORMAL);
		this.translate(x, -y);
		this.setLinearDamping(0.05);
	}

}
