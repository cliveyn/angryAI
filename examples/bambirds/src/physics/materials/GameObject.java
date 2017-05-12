package physics.materials;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.List;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;

import physics.visualisation.Renderer;

//import simulation.obsolete.Renderer;

/**
 * Custom Body class to add drawing functionality.
 *
 * @author William Bittle
 * @version 3.0.2
 * @since 3.0.0
 */
public abstract class GameObject extends Body {
	/** The color of the object */
	protected Color color;
	

	/** The scale 45 pixels per meter */
	public static final double SCALE = 10;

	/** Field for the globalID */
	public String globalID;

	/** List of supported Objects */
	public List<Body> supObjects = null;
	
	protected double health = 10;
	protected double maxHealth = 10;
	protected double damageThreshold = 1;


	/**
	 * Default constructor.
	 */
	public GameObject() {
		// randomly generate the color
		this.color = new Color((float) Math.random() * 0.5f + 0.5f, (float) Math.random() * 0.5f + 0.5f,
				(float) Math.random() * 0.5f + 0.5f);
	}

	/**
	 * Draws the body.
	 * <p>
	 * Only coded for polygons and circles.
	 *
	 * @param g
	 *            the graphics object to render to
	 */
	public void render(Graphics2D g) {
		// save the original transform
		AffineTransform ot = g.getTransform();

		// transform the coordinate system from world coordinates to local
		// coordinates
		AffineTransform lt = new AffineTransform();
		lt.translate(this.transform.getTranslationX() * SCALE, this.transform.getTranslationY() * SCALE);
		lt.rotate(this.transform.getRotation());

		// apply the transform
		g.transform(lt);

		// loop over all the body fixtures for this body
		for (BodyFixture fixture : this.fixtures) {
			// get the shape on the fixture
			Convex convex = fixture.getShape();

			// COMMENT OUT IF NO GRAPHICS
			Renderer.render(g, convex, SCALE, color);
		}

		// set the original transform
		g.setTransform(ot);
	}
	
	public double getHealth() {
		return health;
	}
	
	public double getHealthIndicator(){
		return health/maxHealth;
	}

	public double getDamageThreshold() {
		return damageThreshold;
	}

	public void setHealth(double health) {
		this.health = health;
	}

}