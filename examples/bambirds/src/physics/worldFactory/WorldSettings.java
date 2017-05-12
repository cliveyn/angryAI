package physics.worldFactory;

/**
 *
 * @author Team Physics (Stefan Gruss, Leon Martin, Robert Mueller, Sascha
 *         Riechel)
 *
 *         This class holds all possible settings necessary for conducting a
 *         simulation. The parameters can be adjusted by parameter fittign.
 *
 */

public class WorldSettings {

	public double gravity = 9.81;

	public double birdVelocity = 20.0;

	// Density in kg/m^2 (default is 1.0)
	// Defines the mass of an object by multiplying it with gravity.
	public double densityWood = 0.7;
	public double densityStone = 5.0;
	public double densityIce = 1.0;
	public double densityRollingStone = 4.0;
	public double densityRollingWood = 4.0;
	public double densityTNT = 1.0;

	public double densityRedBird = 1.5;
	public double densityBlueBird = 1.0;
	public double densityYellowBird = 1.0;
	public double densityWhiteBird = 1.0;
	public double densityBlackBird = 1.0;

	public double densityNormalPig = 1.0;
	public double densityPigWithHelmet = 1.0;
	public double densitySmallPig = 1.0;
	public double densityBigPig = 1.0;

	// Friction Coefficient (default is 0.2)
	// Friction is being calculated by mixing the two coefficients (of the
	// participating Body-Fixtures in the collision) in the CoefficientMixer.
	public double frictionWood = 0.5;
	public double frictionStone = 2;
	public double frictionIce = 0.7;
	public double frictionRollingStone = 2;
	public double frictionRollingWood = 2;
	public double frictionTNT = 1;

	public double frictionRedBird = 0.5;
	public double frictionBlueBird = 0.5;
	public double frictionYellowBird = 0.5;
	public double frictionWhiteBird = 0.5;
	public double frictionBlackBird = 0.5;

	public double frictionNormalPig = 0.2;
	public double frictionPigWithHelmet = 0.2;
	public double frictionSmallPig = 0.2;
	public double frictionBigPig = 0.2;

	// Restitution (default is 0.0; must be greater than zero)
	// values == 1.0 lets the object remain it's velocity after bouncing
	// values > 1.0 accelerates the object after bouncing

	// Due to floating point precision 1.0 may not produce 100% correct elastic
	// bouncing

	// It is being calculated by mixing the two coefficients of the objects
	// participating in the collision in the CoefficientMixer
	public double restitutionWood = 0.2;
	public double restitutionStone = 0.2;
	public double restitutionIce = 0.2;
	public double restitutionRollingStone = 0.2;
	public double restitutionRollingWood = 0.2;
	public double restitutionTNT = 0.2;

	public double restitutionRedBird = 0.2;
	public double restitutionBlueBird = 0.2;
	public double restitutionYellowBird = 0.2;
	public double restitutionWhiteBird = 0.2;
	public double restitutionBlackBird = 0.2;

	public double restitutionNormalPig = 0.2;
	public double restitutionPigWithHelmet = 0.2;
	public double restitutionSmallPig = 0.2;
	public double restitutionBigPig = 0.2;

	// Linear damping
	public double linearDampingWood = 0.0;
	public double linearDampingStone = 0.0;
	public double linearDampingIce = 0.0;
	public double linearDampingRollingStone = 0.0;
	public double linearDampingRollingWood = 0.0;
	public double linearDampingTNT = 0.0;

	public double linearDampingRedBird = 0.0;
	public double linearDampingBlueBird = 0.0;
	public double linearDampingYellowBird = 0.0;
	public double linearDampingWhiteBird = 0.0;
	public double linearDampingBlackBird = 0.0;

	public double linearDampingNormalPig = 0.0;
	public double linearDampingPigWithHelmet = 0.0;
	public double linearDampingBigPig = 0.0;
	public double linearDampingSmallPig = 0.0;

	// Angular damping
	public double angularDampingWood = 0.0;
	public double angularDampingStone = 0.0;
	public double angularDampingIce = 0.0;
	public double angularDampingRollingStone = 0.0;
	public double angularDampingRollingWood = 0.0;
	public double angularDampingTNT = 0.0;

	public double angularDampingRedBird = 0.5;
	public double angularDampingBlueBird = 0.5;
	public double angularDampingYellowBird = 0.5;
	public double angularDampingWhiteBird = 0.5;
	public double angularDampingBlackBird = 0.5;

	public double angularDampingNormalPig = 0.8;
	public double angularDampingPigWithHelmet = 0.8;
	public double angularDampingBigPig = 0.8;
	public double angularDampingSmallPig = 0.8;

	// Not really necessary..
	//
	// public static List<Double> getGravityScaleList() {
	// List<Double> gravityScales = new LinkedList<Double>();
	//
	// gravityScales.add(gravityScaleWood);
	// gravityScales.add(gravityScaleStone);
	// gravityScales.add(gravityScaleIce);
	// gravityScales.add(gravityScaleRollingStone);
	// gravityScales.add(gravityScaleTNT);
	//
	// gravityScales.add(gravityScaleRedBird);
	// gravityScales.add(gravityScaleBlueBird);
	// gravityScales.add(gravityScaleWhiteBird);
	// gravityScales.add(gravityScaleYellowBird);
	// gravityScales.add(gravityScaleBlackBird);
	//
	// gravityScales.add(gravityScaleNormalPig);
	// gravityScales.add(gravityScalePigWithHelmet);
	// gravityScales.add(gravityScaleBigPig);

	// return gravityScales;
	// }
	//
	// public List<Double> getLinearDampingList() {
	// List<Double> linearDamping = new LinkedList<Double>();
	//
	// linearDamping.add(linearDampingWood);
	// linearDamping.add(linearDampingStone);
	// linearDamping.add(linearDampingIce);
	// linearDamping.add(linearDampingRollingStone);
	// linearDamping.add(linearDampingTNT);
	//
	// linearDamping.add(linearDampingRedBird);
	// linearDamping.add(linearDampingBlueBird);
	// linearDamping.add(linearDampingWhiteBird);
	// linearDamping.add(linearDampingYellowBird);
	// linearDamping.add(linearDampingBlackBird);
	//
	// linearDamping.add(linearDampingNormalPig);
	// linearDamping.add(linearDampingPigWithHelmet);
	// linearDamping.add(linearDampingBigPig);

	// return linearDamping;
	// }
	//
	// public List<Double> getAngularDampingList() {
	// List<Double> angularDamping = new LinkedList<Double>();
	//
	// angularDamping.add(angularDampingWood);
	// angularDamping.add(angularDampingStone);
	// angularDamping.add(angularDampingIce);
	// angularDamping.add(angularDampingRollingStone);
	// angularDamping.add(angularDampingTNT);
	//
	// angularDamping.add(angularDampingRedBird);
	// angularDamping.add(angularDampingBlueBird);
	// angularDamping.add(angularDampingWhiteBird);
	// angularDamping.add(angularDampingYellowBird);
	// angularDamping.add(angularDampingBlackBird);
	//
	// angularDamping.add(angularDampingNormalPig);
	// angularDamping.add(angularDampingPigWithHelmet);
	// angularDamping.add(angularDampingBigPig);

	// return angularDamping;
	// }

}
