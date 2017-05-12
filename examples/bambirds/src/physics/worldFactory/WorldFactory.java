package physics.worldFactory;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.geometry.decompose.Bayazit;
import org.dyn4j.geometry.decompose.Decomposer;

import ab.demo.other.Shot;
import ab.vision.ABObject;
import ab.vision.ABShape;
import ab.vision.ABType;
import ab.vision.real.shape.Poly;
import ab.vision.real.shape.Rect;
import physics.birdtypes.BirdFacade;
import physics.birdtypes.BirdTypes;
import physics.birdtypes.BlackBird;
import physics.birdtypes.BlueBird;
import physics.birdtypes.RedBird;
import physics.birdtypes.WhiteBird;
import physics.birdtypes.YellowBird;
import physics.calculation.Calculation;
import physics.calculation.CalculationStrategy;
import physics.collision.BirdCollisionListener;
import physics.collision.BlockCollisionListener;
import physics.materials.Floor;
import physics.materials.GameObject;
import physics.materials.Ice;
import physics.materials.Stone;
import physics.materials.TNT;
import physics.materials.Wood;
import physics.pigtypes.NormalPig;
import physics.pigtypes.PigFacade;
import physics.pigtypes.SmallPig;

/**
 *
 * @author Team Physics (Stefan Gruss, Leon Martin, Robert Mueller, Sascha
 *         Riechel)
 *
 *         Factory for generating worlds.
 *
 */
public class WorldFactory {

	static World world = new World();
	static double scale = 0.05;
	WorldSettings worldSettings = new WorldSettings();

	double lowestY = Integer.MAX_VALUE;

	private List<GameObject> worldPigs = new ArrayList<>();
	private List<GameObject> worldBlocks = new ArrayList<>();

	public World generateWorld(List<List<ABObject>> worldFeatures, java.awt.Rectangle rectangle) {

		world.setGravity(new Vector2(0, -worldSettings.gravity));

		if (worldFeatures == null) {
			return world;
		}

		// Extract object lists
		List<ABObject> pigsList = worldFeatures.get(0);
		List<ABObject> birdsList = worldFeatures.get(1);
		List<ABObject> blocksList = worldFeatures.get(2);
		List<ABObject> hillsList = worldFeatures.get(3);
		List<ABObject> tntList = worldFeatures.get(4);

		// Find lowest y point of the coordinate system
		for (ABObject bird : birdsList) {
			if (lowestY > -bird.getY() * scale) {
				lowestY = -bird.getY() * scale;
			}
		}
		lowestY -= lowestY * scale;

		// Insert pigs from feature extraction
		for (ABObject pig : pigsList) {
			PigFacade newPig = createPig(pig);
			worldPigs.add(newPig);
			newPig.globalID = pig.globalID;
		}

		// Insert blocks
		for (ABObject block : blocksList) {
			if (block.shape == ABShape.Poly) {
				createPoly(block);
			} else if (block.shape == ABShape.Rect) {
				Rect rect = (Rect) block;
				createRect(rect);
			} else if (block.shape == ABShape.Circle) {
				ab.vision.real.shape.Circle circ = (ab.vision.real.shape.Circle) block;
				createCircle(circ);
			}

		}

		// Insert hills
		for (ABObject hill : hillsList) {

			Poly poly = (Poly) hill;
			java.awt.Polygon polygon = poly.polygon;
			Vector2[] vertices = getVerticesFromPolygon(polygon);

			Floor newFloor;
			if (checkIfConvex(vertices)) {
				newFloor = new Floor();
				Polygon pol = new Polygon(vertices);
				newFloor.addFixture(new BodyFixture(pol));
				newFloor.setMass(MassType.INFINITE);
				world.addBody(newFloor);
				newFloor.translate(0, -lowestY);
				newFloor.globalID = hill.globalID;
			} else {
				Decomposer decomposer = new Bayazit();
				List<Convex> convexList = decomposer.decompose(vertices);
				for (Convex con : convexList) {
					newFloor = new Floor();
					newFloor.addFixture(new BodyFixture(con));
					newFloor.setMass(MassType.INFINITE);
					world.addBody(newFloor);
					newFloor.translate(0, -lowestY);
					newFloor.globalID = hill.globalID;
				}
			}

		}

		// Insert TNT
		for (ABObject tnt : tntList) {
			TNT newTNT = createTNT(tnt.getCenterX(), tnt.getCenterY(), tnt.getWidth(), tnt.getHeight());
			newTNT.globalID = tnt.globalID;
		}

		// Build floor
		createFloor(0, lowestY + 19.7, 100, 1);

		return world;
	}

	private TNT createTNT(double centerX, double centerY, double width, double height) {
		Rectangle rectShape = new Rectangle(width * scale, height * scale);
		BodyFixture fix = new BodyFixture(rectShape);
		fix.setDensity(worldSettings.densityTNT);
		fix.setFriction(worldSettings.frictionTNT);

		TNT newTNT = new TNT();
		newTNT.addFixture(fix);
		newTNT.setMass(MassType.NORMAL);
		newTNT.translate(centerX * scale, -(centerY * scale + lowestY));
		world.addBody(newTNT);
		return newTNT;
	}

	/**
	 * This adds a shot to a world with either the lower or the higher
	 * trajectory angle. Only for simulation purposes!
	 *
	 * @param world
	 *            The world where the shot is going to be added
	 * @param shot
	 *            Defines the parameters of the Shot
	 * @param birdType
	 *            Type of the bird to be shot
	 * @param calcStrat
	 *            Defines the shooting strategy (upper or lower trajectory)
	 * @param hitLoc
	 *            The Point you desire to hit
	 * @return World with added shot
	 */
	public World addShot(World world, Shot shot, BirdTypes birdType, CalculationStrategy calcStrat, Point2D hitLoc) {
		Point launchCoordinates = new Point(shot.getX(), shot.getY());
		Vector2 shootingVector = new Vector2(0, 0);

		// calculating the shooting vector
		if (calcStrat == CalculationStrategy.LOWER) {
			shootingVector = Calculation.calcLowerShootingVector(launchCoordinates, hitLoc, shot.getVelocity());
		} else if (calcStrat == CalculationStrategy.UPPER) {
			shootingVector = Calculation.calcUpperShootingVector(launchCoordinates, hitLoc, shot.getVelocity());
		} else if (calcStrat == CalculationStrategy.PARALLEL) {
			shootingVector = Calculation.calcParallelShootingVector(shot.getVelocity());
		}

		BirdFacade bird = createBird(birdType, shot.getX() * scale, shot.getY() * scale);
		// Creating the birds and adding it to the world

		if (bird != null) {
			for (GameObject pig : worldPigs) {
				BirdCollisionListener collisionListener = new BirdCollisionListener(bird, pig, world);
				world.addListener(collisionListener);

				for (GameObject block : worldBlocks) {
					BlockCollisionListener blockListener = new BlockCollisionListener(block, pig, world);
					world.addListener(blockListener);
				}
			}
			for (GameObject block : worldBlocks) {
				BirdCollisionListener collisionListener = new BirdCollisionListener(bird, block, world);
				world.addListener(collisionListener);

				for (GameObject block2 : worldBlocks) {
					if (block != block2) {
						BlockCollisionListener blockListener = new BlockCollisionListener(block, block2, world);
						world.addListener(blockListener);
					}
				}
			}
			bird.setLinearVelocity(shootingVector);
		} else {
			System.out.println("PHYSICS - Bird which has been add as shot is null");
		}
		
		bird.globalID = birdType.toString().toLowerCase();

		return world;
	}

	private BirdFacade createBird(BirdTypes birdType, double x, double y) {
		BirdFacade bird = null;
		switch (birdType) {
		case BLACKBIRD:
			bird = new BlackBird(x, y, worldSettings.densityBlackBird, worldSettings.frictionBlackBird,
					worldSettings.restitutionBlackBird);
			bird.setAngularDamping(worldSettings.angularDampingRedBird);
			break;
		case BLUEBIRD:
			bird = new BlueBird(x, y, worldSettings.densityBlueBird, worldSettings.frictionBlueBird,
					worldSettings.restitutionBlueBird);
			bird.setAngularDamping(worldSettings.angularDampingRedBird);
			break;
		case REDBIRD:
			bird = new RedBird(x, y, worldSettings.densityRedBird, worldSettings.frictionRedBird,
					worldSettings.restitutionRedBird);
			bird.setAngularDamping(worldSettings.angularDampingRedBird);
			break;
		case WHITEBIRD:
			bird = new WhiteBird(x, y, worldSettings.densityWhiteBird, worldSettings.frictionWhiteBird,
					worldSettings.restitutionWhiteBird);
			bird.setAngularDamping(worldSettings.angularDampingRedBird);
			break;
		case YELLOWBIRD:
			bird = new YellowBird(x, y, worldSettings.densityYellowBird, worldSettings.frictionYellowBird,
					worldSettings.restitutionYellowBird);
			bird.setAngularDamping(worldSettings.angularDampingRedBird);
			break;
		default:
			bird = new RedBird();
			System.out.println("PHYSICS - createBird() received unkown BirdType");
		}

		world.addBody(bird);
		return bird;
	}

	// Create a pig
	private PigFacade createPig(ABObject pig) {
		PigFacade newPig = null;
		double pigWidth = pig.getWidth();
		if (7.0 < pigWidth && pigWidth < 9.0) {
			newPig = new SmallPig(pig.getCenterX() * scale, pig.getCenterY() * scale + lowestY, pig.getWidth() * scale,
					worldSettings.densityNormalPig, worldSettings.frictionNormalPig,
					worldSettings.restitutionNormalPig);
		} else if (13.0 < pigWidth && pigWidth < 15.0) {
			newPig = new NormalPig(pig.getCenterX() * scale, pig.getCenterY() * scale + lowestY, pig.getWidth() * scale,
					worldSettings.densityBigPig, worldSettings.frictionBigPig, worldSettings.restitutionBigPig);
		} else {
			newPig = new SmallPig(pig.getCenterX() * scale, pig.getCenterY() * scale + lowestY, pig.getWidth() * scale,
					worldSettings.densityNormalPig, worldSettings.frictionNormalPig,
					worldSettings.restitutionNormalPig);
		}
		newPig.setAngularDamping(worldSettings.angularDampingNormalPig);
		world.addBody(newPig);
		return newPig;
	}

	// Create a physic rectangle
	private void createRect(Rect rect) {
		Rectangle rectShape = new Rectangle(rect.getpLength() * scale, rect.getpWidth() * scale);
		BodyFixture fix = new BodyFixture(rectShape);
		GameObject rectObject = null;
		if (rect.type == ABType.Wood) {
			rectObject = new Wood(rect.getpLength(), rect.getpWidth());
			fix.setDensity(worldSettings.densityWood);
			fix.setFriction(worldSettings.frictionWood);
		} else if (rect.type == ABType.Stone) {
			rectObject = new Stone(rect.getpLength(), rect.getpWidth());
			fix.setDensity(worldSettings.densityStone);
			fix.setFriction(worldSettings.frictionStone);
		} else if (rect.type == ABType.Ice) {
			rectObject = new Ice(rect.getpLength(), rect.getpWidth());
			fix.setDensity(worldSettings.densityIce);
			fix.setFriction(worldSettings.frictionIce);
		} else {
			return;
			//rectObject = new Wood(rect.getpLength(), rect.getpWidth());
			//fix.setDensity(worldSettings.densityWood);
			//fix.setFriction(worldSettings.frictionWood);
			//System.out.println("PHYSICS - createRect() received unknown ABType. Selecting wood");
		}

		rectObject.addFixture(fix);
		rectObject.setMass(MassType.NORMAL);
		rectObject.rotate(-rect.angle);
		rectObject.translate(rect.centerX * scale, -(rect.centerY * scale + lowestY));
		rectObject.globalID = rect.globalID;
		world.addBody(rectObject);
		worldBlocks.add(rectObject);
	}

	// Create a physic circle
	private void createCircle(ab.vision.real.shape.Circle circ) {
		Circle circShape = new Circle(circ.r * scale);
		BodyFixture fix = new BodyFixture(circShape);
		GameObject circObject = null;
		if (circ.type == ABType.Wood) {
			circObject = new Wood(circ.r, circ.r);
			fix.setDensity(worldSettings.densityWood);
			fix.setFriction(worldSettings.frictionWood);
		} else if (circ.type == ABType.Stone) {
			circObject = new Stone(circ.r, circ.r);
			fix.setDensity(worldSettings.densityStone);
			fix.setFriction(worldSettings.frictionStone);
		} else if (circ.type == ABType.Ice) {
			circObject = new Ice(circ.r, circ.r);
			fix.setDensity(worldSettings.densityIce);
			fix.setFriction(worldSettings.frictionIce);
		} else {
			circObject = new Wood(circ.r, circ.r);
			fix.setDensity(worldSettings.densityWood);
			fix.setFriction(worldSettings.frictionWood);
			System.out.println("PHYSICS - createCircle() received unknown ABType. Selecting wood");
		}

		circObject.addFixture(fix);
		circObject.setMass(MassType.NORMAL);
		circObject.translate(circ.centerX * scale, -(circ.centerY * scale + lowestY));
		circObject.globalID = circ.globalID;
		world.addBody(circObject);
		worldBlocks.add(circObject);
	}

	// create a physic polygon
	private void createPoly(ABObject block) {
		Poly poly = (Poly) block;
		java.awt.Polygon polygon = poly.polygon;
		Vector2[] vertices = getVerticesFromPolygon(polygon);
		if (!checkIfConvex(vertices)) {
			return;
		}
		Polygon pol = new Polygon(vertices);
		BodyFixture fix = new BodyFixture(pol);
		GameObject polyObject = null;
		if (block.type == ABType.Wood) {
			polyObject = new Wood();
			fix.setDensity(worldSettings.densityWood);
			fix.setFriction(worldSettings.frictionWood);
		} else if (block.type == ABType.Stone) {
			polyObject = new Stone();
			fix.setDensity(worldSettings.densityStone);
			fix.setFriction(worldSettings.frictionStone);
		} else if (block.type == ABType.Ice) {
			polyObject = new Ice();
			fix.setDensity(worldSettings.densityIce);
			fix.setFriction(worldSettings.frictionIce);
		} else {
			polyObject = new Wood();
			fix.setDensity(worldSettings.densityWood);
			fix.setFriction(worldSettings.frictionWood);
			System.out.println("PHYSICS - createPoly() received unknown ABType. Selecting wood");
		}
		polyObject.addFixture(fix);
		polyObject.setMass(MassType.NORMAL);
		polyObject.translate(0, -lowestY);
		world.addBody(polyObject);
		worldBlocks.add(polyObject);
	}

	// Create a static floor
	private static Floor createFloor(double xPosition, double yPosition, double width, double height) {
		Rectangle block = new Rectangle(width, height);
		BodyFixture floorFixture = new BodyFixture(block);
		Floor floor = new Floor();

		floor.addFixture(floorFixture);
		floor.setMass(MassType.INFINITE);
		floor.translate(xPosition, -yPosition);
		world.addBody(floor);
		
		floor.globalID = "floor";
		return floor;
	}

	// Helper methods
	private Boolean checkIfConvex(Vector2[] vertices) {
		try {
			new Polygon(vertices);
			return true;

		} catch (IllegalArgumentException e) {
			return false;
		}

	}

	// Converts java awt polygon to dyn4j polygon
	private Vector2[] getVerticesFromPolygon(java.awt.Polygon polygon) {
		Vector2[] vectors = new Vector2[polygon.npoints];
		for (int i = polygon.npoints - 1; i >= 0; i--) {
			vectors[i] = new Vector2(polygon.xpoints[i] * scale, -polygon.ypoints[i] * scale);
		}
		return vectors;
	}

	// Triggering an explosion based on a certain position and explosion force
	public void triggerExplo(double xExplo, double yExplo, double exploForce) {
		for (GameObject object : worldBlocks) {
			Vector2 exploVector = new Vector2(xExplo - object.getWorldCenter().x, yExplo - object.getWorldCenter().y);
			double diffX = Math.abs(xExplo - object.getWorldCenter().x);
			double diffY = Math.abs(yExplo - object.getWorldCenter().y);

			double d = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
			exploVector.multiply(d * exploForce);
			object.applyForce(exploVector);
		}
	}
	
	public double getLowestY(){
		return lowestY;
	}
}