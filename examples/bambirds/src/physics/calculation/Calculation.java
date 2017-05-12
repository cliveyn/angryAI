package physics.calculation;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.dyn4j.geometry.Vector2;

import ab.demo.other.Shot;

public class Calculation {

	// Gravity factor FIXME mach was
	private static double g = 9.81;

	// Scalings for shooting angle
	private static double scaleLower = 0.3;
	private static double scaleUpper = 0.875;

	/**
	 * Calculates the release point of a bird (the point, where you are dragging
	 * the bird at in order to shoot it away)
	 *
	 * @param angle
	 *            calculated shooting angle
	 * @param shot
	 * @return release point of a bird
	 */
	public static Point calcDragForLaunchPoint(double angle, Shot shot) {
		return new Point((int) (100 * Math.cos(angle)), (int) (100 * Math.sin(angle)));
	}

	/**
	 * Converts angle and velocity into a vector for shooting birds with.
	 *
	 * @param launchCoordinates
	 *            Coordinates of the launching point
	 * @param hitLoc
	 *            Coordinates of the point to be hit
	 * @param launchVelocity
	 *            Initial Velocity of the bird
	 * @return Upper Angle that has to be shot in order to hit the Location of
	 *         the input.
	 */
	public static Vector2 calcUpperShootingVector(Point launchCoordinates, Point2D hitLoc, double launchVelocity) {
		return calcVelocityVector(calcUpperTrajectoryAngle(launchCoordinates, hitLoc, launchVelocity),
				launchVelocity);
	}

	/**
	 * Converts angle and velocity into a vector for shooting birds with.
	 *
	 * @param launchCoordinates
	 *            Coordinates of the launching point
	 * @param hitLoc
	 *            Coordinates of the point to be hit
	 * @param launchVelocity
	 *            Initial Velocity of the bird
	 * @return Lower Angle that has to be shot in order to hit the Location of
	 *         the input.
	 */
	public static Vector2 calcLowerShootingVector(Point launchCoordinates, Point2D hitLoc, double launchVelocity) {
		return calcVelocityVector(calcLowerTrajectoryAngle(launchCoordinates, hitLoc, launchVelocity),
				launchVelocity);
	}

	/**
	 * Parallel Shooting or Bird Dropping
	 *
	 * @param launchCoordinates
	 *            Not necessary!
	 * @param launchVelocity
	 *            If you set the velocity to 0.0, the bird will just drop.
	 * @return
	 */
	public static Vector2 calcParallelShootingVector(double launchVelocity) {
		return calcVelocityVector(calcParallelTrajectoryAngle(), launchVelocity);
	}

	public static Vector2 calcVelocityVector(double angle, double launchVelocity) {
		// convert angle and velocity into a universal shooting vector
		double xVelocity = launchVelocity * Math.cos(angle);
		double yVelocity = launchVelocity * Math.sin(angle);
		System.out.println("PHYSICS - Launching projectile with v = (" + xVelocity + ", " + yVelocity + ")");
		return new Vector2(xVelocity, yVelocity);
	}

	public static double calcLowerTrajectoryAngle(Point2D releaseLoc, Point2D hitLoc, double launchVelocity) {
		// normalize x- and y-parameters
		double x = (hitLoc.getX() - releaseLoc.getX());
		double y = -(hitLoc.getY() - releaseLoc.getY());
		System.out.println("PHYSICS - Shooting " + x + " | " + y);

		// simplify the formula
		double v = launchVelocity;
		double v2 = v * v;
		double v4 = v2 * v2;
		double x2 = x * x;

		
		double formula = (v2 + Math.sqrt(Math.abs(v4 - ((x2 + 2 * y * v2))))) / (x);

		System.out.println("PHYSICS - Inner of root is: " + Math.sqrt(v4 + ((x2 + 2 * y * v2))));

		return scaleLower * Math.atan(formula);
	}

	public static double calcUpperTrajectoryAngle(Point2D releaseLoc, Point2D hitLoc, double launchVelocity) {
		// normalize x- and y-parameters
		double x = hitLoc.getX() - releaseLoc.getX();
		double y = hitLoc.getY() - releaseLoc.getY();

		// simplify the formula
		double v = launchVelocity;
		double v2 = v * v;
		double v4 = v2 * v2;
		double x2 = x * x;

		double root = v4 - ((x2 + 2 * y * v2));
		double formula = (v2 - Math.sqrt(Math.abs(root))) / (x);

		System.out.println("PHYSICS - Inner of root is: " + root);
		
		return scaleUpper * Math.atan(formula);
	}

	// angle for shooting with 0 y-velocity
	public static double calcParallelTrajectoryAngle() {
		return 0.0;
	}

	/**
	 * This method calculates the ToF of a normal RedBird
	 *
	 * @param velocity
	 *            Initial Velocity of the bird
	 * @param theta
	 *            Launching angle of the bird
	 */
	public static long calculateTimeOfFlight(double velocity, double theta) {
		// not sure if this is working properly as no information about the
		// difference of launching point and hitpoint is given.
		return (long) ((2 * velocity * Math.sin(theta)) / g);
	}

	/**
	 * Takes the bounding rectangle of
	 *
	 * @param sling
	 *            Bounding Rectangle of the slingshot Rectangle
	 * @param theta
	 *            Shooting Angle
	 * @return Release point for shooting in the actual game
	 */
	public static Point findReleasePoint(Rectangle sling, double theta) {
		double speed = sling.height * 10;
		Point refPoint = getReferencePoint(sling);
		return new Point((int) (refPoint.x - speed * Math.cos(theta)), (int) (refPoint.y + speed * Math.sin(theta)));
	}

	// find the reference point given the sling
	private static Point getReferencePoint(Rectangle sling) {
		return new Point((int) (sling.x + sling.width / 2), (int) (sling.y - sling.height * (1 / 6)));
		// x equals middle of the sling
		// y is at 5/6 (2,5cm out of 3cm height) of the sling's height
	}

	private static int calcMaxShootingRange(double velocity) {
		return (int) ((velocity * velocity) / g);
	}

	/**
	 * Get a list of points that the bird is going to pass on it's trajectory |
	 * NOT TESTED YET
	 * 
	 * @param release
	 *            initial point of the bird
	 * @param hitLocation
	 *            point to be hit
	 * @param velocity
	 *            Velocity of the birds
	 * @param calcStrat
	 *            upper or lower trajectory path
	 * @return List of points of the trajectory
	 */
	public static List<Point> calcTrajectoryPoint(Point release, Point hitLocation, double velocity,
			CalculationStrategy calcStrat) {

		List<Point> trajectory = new ArrayList<Point>();
		int x_max = calcMaxShootingRange(velocity);
		int x, y;
		x = release.x;
		y = release.y;

		double shootingAngle = 0;
		if (calcStrat == CalculationStrategy.LOWER) {
			shootingAngle = Calculation.calcLowerTrajectoryAngle(release, hitLocation, velocity);
		} else if (calcStrat == CalculationStrategy.UPPER) {
			shootingAngle = Calculation.calcUpperTrajectoryAngle(release, hitLocation, velocity);
		}

		double _ux = velocity * Math.cos(shootingAngle);
		double _uy = velocity * Math.sin(shootingAngle);
		double _a = -0.5 / (_ux * _ux);
		double _b = _uy / _ux;

		for (int i = 0; i < x_max; i++) {
			int tempY = y - (int) ((_a * x * x + _b * x));
			trajectory.add(new Point(i + x, tempY));
		}
		return trajectory;
	}
}
