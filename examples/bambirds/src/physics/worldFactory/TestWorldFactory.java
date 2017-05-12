package physics.worldFactory;

import java.awt.geom.Point2D;
import java.util.List;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.CollisionListener;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

import ab.vision.ABObject;
import physics.birdtypes.RedBird;
import physics.calculation.Calculation;
import physics.collision.BirdCollisionListener;
import physics.collision.BlockCollisionListener;
import physics.materials.Floor;
import physics.materials.GameObject;
import physics.materials.Stone;
import physics.materials.Wood;
import physics.pigtypes.NormalPig;

/**
 * This class is used to create worlds for testing simulations.
 */
public final class TestWorldFactory {

	// // Playing god
	// static World world;
	//
	// // Parameter to set the surface-friction of all the elements
	// static double frictionAmount = 0.7;
	//
	// public static World initializeWorldOne() {
	// // create the world
	// world = new World();
	//
	// // Creating the floor
	// createFloor(75.0, 1.0);
	//
	// // Creating a red bird
	// Vector2 velVector = new Vector2(20.0, 1.0);
	// RedBird redBird = new RedBird(-5.0, 3.0, 1, frictionAmount, 1.0);
	//
	// // Creating a normal pig
	// NormalPig pigOne = createNormalPig(6.0, 0.2);
	//
	// // try a rectangle
	// Rectangle rectShape = new Rectangle(0.5, 3.0);
	// Wood rectangleOne = new Wood();
	// rectangleOne.addFixture(rectShape);
	// rectangleOne.setMass(MassType.NORMAL);
	// rectangleOne.translate(5.0, 2.2);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleOne);
	// System.out.println("ID of rectangleOne is:" + rectangleOne.getId());
	//
	// // try a rectangle
	// Wood rectangleTwo = new Wood();
	// rectangleTwo.addFixture(rectShape);
	// rectangleTwo.setMass(MassType.NORMAL);
	// rectangleTwo.translate(7.0, 2.2);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleTwo);
	// System.out.println("ID of rectangleTwo is:" + rectangleTwo.getId());
	//
	// Rectangle bridge = new Rectangle(2.5, 0.5);
	// Wood rectangleBridge = new Wood();
	// rectangleBridge.addFixture(bridge);
	// rectangleBridge.setMass(MassType.NORMAL);
	// rectangleBridge.translate(6.0, 4);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleBridge);
	// System.out.println("ID of rectangleBridge is:" +
	// rectangleBridge.getId());
	//
	// // setup the world
	// CollisionListener woodCollisionListenerOne = new
	// WoodCollisionListener(redBird, rectangleOne, world);
	// world.addListener(woodCollisionListenerOne);
	//
	// CollisionListener woodCollisionListenerTwo = new
	// WoodCollisionListener(redBird, rectangleTwo, world);
	// world.addListener(woodCollisionListenerTwo);
	//
	// CollisionListener woodCollisionListenerThree = new
	// WoodCollisionListener(redBird, rectangleTwo, world);
	// world.addListener(woodCollisionListenerThree);
	//
	// CollisionListener birdPigCollisionListenerOne = new
	// BirdPigCollisionListener(redBird, pigOne, world);
	// world.addListener(birdPigCollisionListenerOne);
	//
	// return world;
	//
	// }
	//
	// public static World initializeWorldTwo() {
	// // create the world
	// world = new World();
	//
	// // Creating the floor
	// createFloor(75.0, 1.0);
	//
	// // Creating a red bird
	// Vector2 velVector = new Vector2(20.0, 1.0);
	// RedBird redBird = new RedBird(-5.0, 3.0, 1, frictionAmount, 1);
	// world.addBody(redBird);
	//
	// // create all your bodies/joints
	//
	// NormalPig pigOne = createNormalPig(6.0, 0.2);
	//
	// // try a rectangle
	// Rectangle rectShape = new Rectangle(0.5, 3.0);
	// GameObject rectangleOne = new Wood();
	// rectangleOne.addFixture(rectShape);
	// rectangleOne.setMass(MassType.NORMAL);
	// rectangleOne.translate(5.0, -2.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleOne);
	// System.out.println("ID of rectangleOne is:" + rectangleOne.getId());
	//
	// // try a rectangle
	// GameObject rectangleTwo = new Wood();
	// rectangleTwo.addFixture(rectShape);
	// rectangleTwo.setMass(MassType.NORMAL);
	// rectangleTwo.translate(7.0, -2.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleTwo);
	// System.out.println("ID of rectangleTwo is:" + rectangleTwo.getId());
	//
	// Rectangle bridge = new Rectangle(2.5, 0.5);
	// GameObject rectangleBridge = new Wood();
	// rectangleBridge.addFixture(bridge);
	// rectangleBridge.setMass(MassType.NORMAL);
	// rectangleBridge.translate(6.0, -0.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleBridge);
	// System.out.println("ID of rectangleBridge is:" +
	// rectangleBridge.getId());
	//
	// CollisionListener birdPigCollisionListenerOne = new
	// BirdPigCollisionListener(redBird, pigOne, world);
	// world.addListener(birdPigCollisionListenerOne);
	//
	// return world;
	//
	// }
	//
	// public static World initializeWorldThree() {
	// // create the world
	// world = new World();
	//
	// // create all your bodies/joints
	//
	// // try a compound object
	// Circle c1 = new Circle(0.5);
	// BodyFixture c1Fixture = new BodyFixture(c1);
	// c1Fixture.setDensity(0.5);
	// Circle c2 = new Circle(0.5);
	// BodyFixture c2Fixture = new BodyFixture(c2);
	// c2Fixture.setDensity(0.5);
	// Rectangle rm = new Rectangle(0.2, 1.0);
	// // translate the circles in local coordinates
	// c1.translate(-0.1, 0.0);
	// c2.translate(0.1, 0.0);
	// GameObject pigOne = new NormalPig();
	// pigOne.addFixture(c1Fixture);
	// pigOne.addFixture(c2Fixture);
	// pigOne.addFixture(rm);
	// pigOne.setMass(MassType.NORMAL);
	// pigOne.translate(6.0, 0.0);
	// world.addBody(pigOne);
	// System.out.println("ID of pigOne is:" + pigOne.getId());
	//
	// // try a compound object
	// // Circle c1 = new Circle(0.5);
	// // BodyFixture c1Fixture = new BodyFixture(c1);
	// // c1Fixture.setDensity(0.5);
	// // Circle c2 = new Circle(0.5);
	// // BodyFixture c2Fixture = new BodyFixture(c2);
	// // c2Fixture.setDensity(0.5);
	// // Rectangle rm = new Rectangle(0.2, 1.0);
	// // translate the circles in local coordinates
	// c1.translate(-0.1, 0.0);
	// c2.translate(0.1, 0.0);
	// GameObject pigTwo = new NormalPig();
	// pigTwo.addFixture(c1Fixture);
	// pigTwo.addFixture(c2Fixture);
	// pigTwo.addFixture(rm);
	// pigTwo.setMass(MassType.NORMAL);
	// pigTwo.translate(6.0, -3.0);
	// world.addBody(pigTwo);
	// System.out.println("ID of pigTwo is:" + pigTwo.getId());
	//
	// // Creating the floor
	// createFloor(75.0, 1.0);
	//
	// // create a circle
	// Vector2 velVector = new Vector2(20.0, 1.0);
	// RedBird redBird = new RedBird(-5.0, 3.0, velVector, frictionAmount);
	// world.addBody(redBird);
	// System.out.println("ID of projectile is:" + redBird.getId());
	//
	// // try a rectangle
	// Rectangle rectShape = new Rectangle(0.5, 3.0);
	// GameObject rectangleOne = new Wood();
	// rectangleOne.addFixture(rectShape);
	// rectangleOne.setMass(MassType.NORMAL);
	// rectangleOne.translate(5.0, -2.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleOne);
	// System.out.println("ID of rectangleOne is:" + rectangleOne.getId());
	//
	// // try a rectangle
	// GameObject rectangleTwo = new Wood();
	// rectangleTwo.addFixture(rectShape);
	// rectangleTwo.setMass(MassType.NORMAL);
	// rectangleTwo.translate(7.0, -2.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleTwo);
	// System.out.println("ID of rectangleTwo is:" + rectangleTwo.getId());
	//
	// Rectangle bridge = new Rectangle(2.5, 0.5);
	// GameObject rectangleBridge = new Wood();
	// rectangleBridge.addFixture(bridge);
	// rectangleBridge.setMass(MassType.NORMAL);
	// rectangleBridge.translate(6.0, -0.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleBridge);
	// System.out.println("ID of rectangleBridge is:" +
	// rectangleBridge.getId());
	//
	// // upper floor
	//
	// GameObject rectangleThree = new Wood();
	// rectangleThree.addFixture(rectShape);
	// rectangleThree.setMass(MassType.NORMAL);
	// rectangleThree.translate(5.0, 1.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleThree);
	// System.out.println("ID of rectangleOne is:" + rectangleThree.getId());
	//
	// GameObject rectangleFour = new Wood();
	// rectangleFour.addFixture(rectShape);
	// rectangleFour.setMass(MassType.NORMAL);
	// rectangleFour.translate(7.0, 1.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleFour);
	// System.out.println("ID of rectangleTwo is:" + rectangleFour.getId());
	//
	// GameObject rectangleBridgeTwo = new Wood();
	// rectangleBridgeTwo.addFixture(bridge);
	// rectangleBridgeTwo.setMass(MassType.NORMAL);
	// rectangleBridgeTwo.translate(6.0, 3.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleBridgeTwo);
	// System.out.println("ID of rectangleBridge is:" +
	// rectangleBridgeTwo.getId());
	//
	// return world;
	// }
	//
	// public static World initializeWorldFour() {
	// // create the world
	// world = new World();
	//
	// // create all your bodies/joints
	//
	// // try a compound object
	// Circle c1 = new Circle(0.5);
	// BodyFixture c1Fixture = new BodyFixture(c1);
	// c1Fixture.setDensity(0.5);
	// Circle c2 = new Circle(0.5);
	// BodyFixture c2Fixture = new BodyFixture(c2);
	// c2Fixture.setDensity(0.5);
	// Rectangle rm = new Rectangle(0.2, 1.0);
	// // translate the circles in local coordinates
	// c1.translate(-0.1, 0.0);
	// c2.translate(0.1, 0.0);
	// GameObject pigOne = new NormalPig();
	// pigOne.addFixture(c1Fixture);
	// pigOne.addFixture(c2Fixture);
	// pigOne.addFixture(rm);
	// pigOne.setMass(MassType.NORMAL);
	// pigOne.translate(6.0, 0.0);
	// world.addBody(pigOne);
	// System.out.println("ID of pigOne is:" + pigOne.getId());
	//
	// // try a compound object
	// // Circle c1 = new Circle(0.5);
	// // BodyFixture c1Fixture = new BodyFixture(c1);
	// // c1Fixture.setDensity(0.5);
	// // Circle c2 = new Circle(0.5);
	// // BodyFixture c2Fixture = new BodyFixture(c2);
	// // c2Fixture.setDensity(0.5);
	// // Rectangle rm = new Rectangle(0.2, 1.0);
	// // translate the circles in local coordinates
	// c1.translate(-0.1, 0.0);
	// c2.translate(0.1, 0.0);
	// GameObject pigTwo = new NormalPig();
	// pigTwo.addFixture(c1Fixture);
	// pigTwo.addFixture(c2Fixture);
	// pigTwo.addFixture(rm);
	// pigTwo.setMass(MassType.NORMAL);
	// pigTwo.translate(6.0, -3.0);
	// world.addBody(pigTwo);
	// System.out.println("ID of pigTwo is:" + pigTwo.getId());
	//
	// // Creating the floor
	// createFloor(75.0, 1.0);
	//
	// // create a circle
	// Vector2 velVector = new Vector2(20.0, 1.0);
	// RedBird redBird = new RedBird(-5.0, 3.0, velVector, frictionAmount);
	// world.addBody(redBird);
	// System.out.println("ID of projectile is:" + redBird.getId());
	//
	// Rectangle rectShape = new Rectangle(0.5, 3.0);
	// GameObject rectangleX = new Wood();
	// rectangleX.addFixture(rectShape);
	// rectangleX.setMass(MassType.NORMAL);
	// rectangleX.translate(3.0, -2.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleX);
	// System.out.println("ID of rectangleX is:" + rectangleX.getId());
	//
	// GameObject rectangleY = new Wood();
	// rectangleY.addFixture(rectShape);
	// rectangleY.setMass(MassType.NORMAL);
	// rectangleY.translate(3.0, 1.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleY);
	// System.out.println("ID of rectangleY is:" + rectangleY.getId());
	//
	// GameObject rectangleA = new Wood();
	// rectangleA.addFixture(rectShape);
	// rectangleA.setMass(MassType.NORMAL);
	// rectangleA.translate(4.0, -2.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleA);
	// System.out.println("ID of rectangleA is:" + rectangleA.getId());
	//
	// GameObject rectangleB = new Wood();
	// rectangleB.addFixture(rectShape);
	// rectangleB.setMass(MassType.NORMAL);
	// rectangleB.translate(4.0, 1.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleB);
	// System.out.println("ID of rectangleB is:" + rectangleB.getId());
	//
	// // try a rectangle
	// GameObject rectangleOne = new Wood();
	// rectangleOne.addFixture(rectShape);
	// rectangleOne.setMass(MassType.NORMAL);
	// rectangleOne.translate(5.0, -2.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleOne);
	// System.out.println("ID of rectangleOne is:" + rectangleOne.getId());
	//
	// // try a rectangle
	// GameObject rectangleTwo = new Wood();
	// rectangleTwo.addFixture(rectShape);
	// rectangleTwo.setMass(MassType.NORMAL);
	// rectangleTwo.translate(7.0, -2.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleTwo);
	// System.out.println("ID of rectangleTwo is:" + rectangleTwo.getId());
	//
	// Rectangle bridge = new Rectangle(2.5, 0.5);
	// GameObject rectangleBridge = new Wood();
	// rectangleBridge.addFixture(bridge);
	// rectangleBridge.setMass(MassType.NORMAL);
	// rectangleBridge.translate(6.0, -0.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleBridge);
	// System.out.println("ID of rectangleBridge is:" +
	// rectangleBridge.getId());
	//
	// // upper floor
	//
	// GameObject rectangleThree = new Wood();
	// rectangleThree.addFixture(rectShape);
	// rectangleThree.setMass(MassType.NORMAL);
	// rectangleThree.translate(5.0, 1.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleThree);
	// System.out.println("ID of rectangleOne is:" + rectangleThree.getId());
	//
	// GameObject rectangleFour = new Wood();
	// rectangleFour.addFixture(rectShape);
	// rectangleFour.setMass(MassType.NORMAL);
	// rectangleFour.translate(7.0, 1.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleFour);
	// System.out.println("ID of rectangleTwo is:" + rectangleFour.getId());
	//
	// GameObject rectangleBridgeTwo = new Wood();
	// rectangleBridgeTwo.addFixture(bridge);
	// rectangleBridgeTwo.setMass(MassType.NORMAL);
	// rectangleBridgeTwo.translate(6.0, 3.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleBridgeTwo);
	// System.out.println("ID of rectangleBridge is:" +
	// rectangleBridgeTwo.getId());
	//
	// return world;
	// }
	//
	// public static World initializeRealLevelTwo() {
	// // create the world
	// world = new World();
	//
	// // create all your bodies/joints
	//
	// // try a compound object
	// Circle c1 = new Circle(0.5);
	// BodyFixture c1Fixture = new BodyFixture(c1);
	// c1Fixture.setFriction(frictionAmount);
	// c1Fixture.setDensity(0.5);
	// Circle c2 = new Circle(0.5);
	// BodyFixture c2Fixture = new BodyFixture(c2);
	// c2Fixture.setFriction(frictionAmount);
	// c2Fixture.setDensity(0.5);
	// Rectangle rm = new Rectangle(0.2, 1.0);
	// BodyFixture rmFixture = new BodyFixture(rm);
	// rmFixture.setFriction(frictionAmount);
	// rmFixture.setDensity(0.5);
	//
	// // Pigs
	// GameObject pigOne = new NormalPig();
	// pigOne.addFixture(c1Fixture);
	// pigOne.addFixture(c2Fixture);
	// pigOne.addFixture(rm);
	// pigOne.setMass(MassType.NORMAL);
	// pigOne.translate(5.0, 8.0);
	// world.addBody(pigOne);
	// System.out.println("ID of pigOne is:" + pigOne.getId());
	//
	// GameObject pigTwo = new NormalPig();
	// pigTwo.addFixture(c1Fixture);
	// pigTwo.addFixture(c2Fixture);
	// pigTwo.addFixture(rm);
	// pigTwo.setMass(MassType.NORMAL);
	// pigTwo.translate(9.0, 8.0);
	// world.addBody(pigTwo);
	// System.out.println("ID of pigOne is:" + pigTwo.getId());
	//
	// GameObject pigThree = new NormalPig();
	// pigThree.addFixture(c1Fixture);
	// pigThree.addFixture(c2Fixture);
	// pigThree.addFixture(rm);
	// pigThree.setMass(MassType.NORMAL);
	// pigThree.translate(13.0, 8.0);
	// world.addBody(pigThree);
	// System.out.println("ID of pigOne is:" + pigThree.getId());
	//
	// GameObject pigFour = new NormalPig();
	// pigFour.addFixture(c1Fixture);
	// pigFour.addFixture(c2Fixture);
	// pigFour.addFixture(rm);
	// pigFour.setMass(MassType.NORMAL);
	// pigFour.translate(19.0, 9.5);
	// world.addBody(pigFour);
	// System.out.println("ID of pigOne is:" + pigFour.getId());
	//
	// // Creating the floor
	// createFloor(75.0, 1.0);
	//
	// // create the floor
	// Rectangle floorRectTwo = new Rectangle(4.0, 2.0);
	// GameObject floorTwo = new Wood();
	// BodyFixture floorRectTwoFixture = new BodyFixture(floorRectTwo);
	// floorRectTwoFixture.setFriction(frictionAmount);
	// floorTwo.addFixture(floorRectTwoFixture);
	// floorTwo.setMass(MassType.INFINITE);
	// // move the floor down a bit
	// floorTwo.translate(-20.0, 1.5);
	// world.addBody(floorTwo);
	//
	// // create second floor that builds a little plateau
	// Rectangle secondFloorRect = new Rectangle(25.0, 3.0);
	// GameObject floorThree = new Wood();
	// BodyFixture secondFloorFixture = new BodyFixture(secondFloorRect);
	// secondFloorFixture.setFriction(frictionAmount);
	// floorThree.addFixture(secondFloorFixture);
	// floorThree.setMass(MassType.INFINITE);
	// // move the floor down a bit
	// floorThree.translate(16.5, 2);
	// world.addBody(floorThree);
	//
	// // create the floor
	// Rectangle floorRectFour = new Rectangle(5.0, 3.0);
	// GameObject floorFour = new Wood();
	// BodyFixture floorRectFourFixture = new BodyFixture(floorRectFour);
	// floorRectFourFixture.setFriction(frictionAmount);
	// floorFour.addFixture(floorRectFourFixture);
	// floorFour.setMass(MassType.INFINITE);
	// // move the floor down a bit
	// floorFour.translate(2.8, -1.5);
	// floorFour.rotate(0.7);
	// world.addBody(floorFour);
	//
	// // create the floor
	// Rectangle floorRectFive = new Rectangle(15.0, 5.0);
	// GameObject floorFive = new Wood();
	// BodyFixture floorRectFiveFixture = new BodyFixture(floorRectFive);
	// floorRectFiveFixture.setFriction(frictionAmount);
	// floorFive.addFixture(floorRectFiveFixture);
	// floorFive.setMass(MassType.INFINITE);
	// // move the floor down a bit
	// floorFive.translate(21.0, -25.0);
	// floorFive.rotate(1);
	// world.addBody(floorFive);
	//
	// // Projectile
	// double vel = 20.0;
	// Point2D.Double launchCoordinates = new Point2D.Double(-15.0, 10.0);
	// Point2D.Double hitLocation = new Point2D.Double(5.0, 8.0);
	// System.out.println("hitLocation is: " + hitLocation.getX() + ", " +
	// hitLocation.getY());
	//
	// Vector2 velVector =
	// Calculation.calcUpperShootingVector(launchCoordinates, hitLocation, vel);
	//
	// RedBird redBird = new RedBird(-15.0, 10.0, velVector, frictionAmount);
	// world.addBody(redBird);
	// System.out.println("ID of projectile is:" + redBird.getId());
	//
	// // T1
	// Rectangle rectShape = new Rectangle(0.5, 3.0);
	// GameObject rectangleOne = new Wood();
	//
	// // Bodyfixture for the following 'T's
	// BodyFixture rectangleOneFixture = new BodyFixture(rectShape);
	// rectangleOneFixture.setFriction(frictionAmount);
	// rectangleOne.addFixture(rectShape);
	// rectangleOne.setMass(MassType.NORMAL);
	// rectangleOne.translate(5.0, 5.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleOne);
	// System.out.println("ID of rectangleOne is:" + rectangleOne.getId());
	//
	// Rectangle bridge = new Rectangle(2.0, 0.5);
	// GameObject rectangleBridge = new Wood();
	// BodyFixture rectangleBridgeFixture = new BodyFixture(bridge);
	// rectangleBridgeFixture.setFriction(frictionAmount);
	// rectangleBridge.addFixture(bridge);
	// rectangleBridge.setMass(MassType.NORMAL);
	// rectangleBridge.translate(5.0, 7.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleBridge);
	// System.out.println("ID of rectangleBridge is:" +
	// rectangleBridge.getId());
	//
	// // T2
	// GameObject rectangleTwo = new Wood();
	// rectangleTwo.addFixture(rectShape);
	// rectangleTwo.setMass(MassType.NORMAL);
	// rectangleTwo.translate(9.0, 5.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleTwo);
	// System.out.println("ID of rectangleOne is:" + rectangleTwo.getId());
	//
	// GameObject rectangleBridgeTwo = new Wood();
	// rectangleBridgeTwo.addFixture(bridge);
	// rectangleBridgeTwo.setMass(MassType.NORMAL);
	// rectangleBridgeTwo.translate(9.0, 7.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleBridgeTwo);
	// System.out.println("ID of rectangleBridge is:" +
	// rectangleBridgeTwo.getId());
	//
	// // T3
	// GameObject rectangleThree = new Wood();
	// rectangleThree.addFixture(rectShape);
	// rectangleThree.setMass(MassType.NORMAL);
	// rectangleThree.translate(13.0, 5.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleThree);
	// System.out.println("ID of rectangleOne is:" + rectangleThree.getId());
	//
	// GameObject rectangleBridgeThree = new Wood();
	// rectangleBridgeThree.addFixture(bridge);
	// rectangleBridgeThree.setMass(MassType.NORMAL);
	// rectangleBridgeThree.translate(13.0, 7.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleBridgeThree);
	// System.out.println("ID of rectangleBridge is:" +
	// rectangleBridgeThree.getId());
	//
	// // T4
	// Rectangle rectShapeTwo = new Rectangle(0.5, 4.0);
	// GameObject rectangleFour = new Wood();
	// rectangleFour.addFixture(rectShapeTwo);
	// rectangleFour.setMass(MassType.NORMAL);
	// rectangleFour.translate(19.0, 5.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleFour);
	// System.out.println("ID of rectangleOne is:" + rectangleFour.getId());
	//
	// GameObject rectangleBridgeFour = new Wood();
	// rectangleBridgeFour.addFixture(bridge);
	// rectangleBridgeFour.setMass(MassType.NORMAL);
	// rectangleBridgeFour.translate(19.0, 7.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleBridgeFour);
	// System.out.println("ID of rectangleBridge is:" +
	// rectangleBridgeFour.getId());
	//
	// return world;
	// }
	//
	// public static World initializeRealLevelTwentyFour() {
	// // create the world
	// world = new World();
	//
	// // Creating the floor
	// createFloor(75.0, 1.0);
	//
	// Rectangle floorRectTwo = new Rectangle(20.0, 3.0);
	// GameObject floorTwo = new Floor();
	// floorTwo.addFixture(new BodyFixture(floorRectTwo));
	// floorTwo.setMass(MassType.INFINITE);
	// // move the floor down a bit
	// floorTwo.translate(17.5, 2.0);
	// world.addBody(floorTwo);
	//
	// // create the floor
	// Rectangle floorRectThree = new Rectangle(7.0, 3.0);
	// GameObject floorThree = new Floor();
	// floorThree.addFixture(new BodyFixture(floorRectThree));
	// floorThree.setMass(MassType.INFINITE);
	// // move the floor down a bit
	// floorThree.translate(4.75, -2.4);
	// floorThree.rotate(0.55);
	// world.addBody(floorThree);
	//
	// // create the floor
	// Rectangle floorRectFour = new Rectangle(7.0, 3.0);
	// GameObject floorFour = new Floor();
	// floorFour.addFixture(new BodyFixture(floorRectFour));
	// floorFour.setMass(MassType.INFINITE);
	// // move the floor down a bit
	// floorFour.translate(-26.75, -13.35);
	// floorFour.rotate(2.7);
	// world.addBody(floorFour);
	//
	// // Block1
	// Rectangle rectShape = new Rectangle(1.0, 1.0);
	// GameObject rectangleOne = new Wood();
	// rectangleOne.addFixture(rectShape);
	// rectangleOne.setMass(MassType.NORMAL);
	// rectangleOne.translate(8.0, 3.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleOne);
	// System.out.println("ID of rectangleOne is:" + rectangleOne.getId());
	//
	// // try a compound object
	// Circle c1 = new Circle(0.5);
	// BodyFixture c1Fixture = new BodyFixture(c1);
	// c1Fixture.setDensity(0.5);
	// Circle c2 = new Circle(0.5);
	// BodyFixture c2Fixture = new BodyFixture(c2);
	// c2Fixture.setDensity(0.5);
	// Rectangle rm = new Rectangle(0.2, 1.0);
	//
	// GameObject pigOne = new NormalPig();
	// pigOne.addFixture(c1Fixture);
	// pigOne.addFixture(c2Fixture);
	// pigOne.addFixture(rm);
	// pigOne.setMass(MassType.NORMAL);
	// pigOne.translate(10.0, 3.0);
	// world.addBody(pigOne);
	// System.out.println("ID of pigOne is:" + pigOne.getId());
	//
	// // Stange1
	// Rectangle rectShapeTwo = new Rectangle(0.5, 2.0);
	// GameObject rectangleTwo = new Wood();
	// rectangleTwo.addFixture(rectShapeTwo);
	// rectangleTwo.setMass(MassType.NORMAL);
	// rectangleTwo.translate(11.5, 3.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleTwo);
	// System.out.println("ID of rectangleOne is:" + rectangleTwo.getId());
	//
	// // Stange2
	// GameObject rectangleThree = new Wood();
	// rectangleThree.addFixture(rectShapeTwo);
	// rectangleThree.setMass(MassType.NORMAL);
	// rectangleThree.translate(12.0, 3.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleThree);
	// System.out.println("ID of rectangleOne is:" + rectangleThree.getId());
	//
	// // Block2
	//
	// GameObject rectangleFour = new Wood();
	// rectangleFour.addFixture(rectShape);
	// rectangleFour.setMass(MassType.NORMAL);
	// rectangleFour.translate(11.75, 6.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleFour);
	// System.out.println("ID of rectangleOne is:" + rectangleFour.getId());
	//
	// // second pig
	// GameObject pigTwo = new NormalPig();
	// pigTwo.addFixture(c1Fixture);
	// pigTwo.addFixture(c2Fixture);
	// pigTwo.addFixture(rm);
	// pigTwo.setMass(MassType.NORMAL);
	// pigTwo.translate(13.5, 3.0);
	// world.addBody(pigTwo);
	// System.out.println("ID of pigOne is:" + pigTwo.getId());
	//
	// // Stange3
	// Rectangle rectShapeFive = new Rectangle(0.5, 4.0);
	// GameObject rectangleFive = new Wood();
	// rectangleFive.addFixture(rectShapeFive);
	// rectangleFive.setMass(MassType.NORMAL);
	// rectangleFive.translate(15.0, 3.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleFive);
	// System.out.println("ID of rectangleOne is:" + rectangleFive.getId());
	//
	// // Stange4
	// GameObject rectangleSix = new Wood();
	// rectangleSix.addFixture(rectShapeFive);
	// rectangleSix.setMass(MassType.NORMAL);
	// rectangleSix.translate(15.50, 3.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleSix);
	// System.out.println("ID of rectangleOne is:" + rectangleSix.getId());
	//
	// // third pig
	// GameObject pigThird = new NormalPig();
	// pigThird.addFixture(c1Fixture);
	// pigThird.addFixture(c2Fixture);
	// pigThird.addFixture(rm);
	// pigThird.setMass(MassType.NORMAL);
	// pigThird.translate(17.0, 3.0);
	// world.addBody(pigThird);
	// System.out.println("ID of pigOne is:" + pigThird.getId());
	//
	// // Stange5
	// GameObject rectangleSeven = new Wood();
	// rectangleSeven.addFixture(rectShapeFive);
	// rectangleSeven.setMass(MassType.NORMAL);
	// rectangleSeven.translate(18.5, 3.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleSeven);
	// System.out.println("ID of rectangleOne is:" + rectangleSeven.getId());
	//
	// // Stange6
	// GameObject rectangleEight = new Wood();
	// rectangleEight.addFixture(rectShapeFive);
	// rectangleEight.setMass(MassType.NORMAL);
	// rectangleEight.translate(19.0, 3.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleEight);
	// System.out.println("ID of rectangleOne is:" + rectangleEight.getId());
	//
	// // fourth pig
	// GameObject pigFour = new NormalPig();
	// pigFour.addFixture(c1Fixture);
	// pigFour.addFixture(c2Fixture);
	// pigFour.addFixture(rm);
	// pigFour.setMass(MassType.NORMAL);
	// pigFour.translate(20.5, 3.0);
	// world.addBody(pigFour);
	// System.out.println("ID of pigOne is:" + pigFour.getId());
	//
	// // Stange7
	// GameObject rectangleNine = new Wood();
	// rectangleNine.addFixture(rectShapeFive);
	// rectangleNine.setMass(MassType.NORMAL);
	// rectangleNine.translate(22.0, 3.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleNine);
	// System.out.println("ID of rectangleOne is:" + rectangleNine.getId());
	//
	// // Stange8
	// GameObject rectangleTen = new Wood();
	// rectangleTen.addFixture(rectShapeFive);
	// rectangleTen.setMass(MassType.NORMAL);
	// rectangleTen.translate(22.5, 3.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleTen);
	// System.out.println("ID of rectangleOne is:" + rectangleTen.getId());
	//
	// // fifth pig
	// GameObject pigFive = new NormalPig();
	// pigFive.addFixture(c1Fixture);
	// pigFive.addFixture(c2Fixture);
	// pigFive.addFixture(rm);
	// pigFive.setMass(MassType.NORMAL);
	// pigFive.translate(24.0, 3.0);
	// world.addBody(pigFive);
	// System.out.println("ID of pigOne is:" + pigFive.getId());
	//
	// // Stange9
	// GameObject rectangleEleven = new Wood();
	// rectangleEleven.addFixture(rectShapeFive);
	// rectangleEleven.setMass(MassType.NORMAL);
	// rectangleEleven.translate(25.5, 4);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleEleven);
	// System.out.println("ID of rectangleOne is:" + rectangleEleven.getId());
	//
	// // Stange10
	// GameObject rectangleTwelve = new Wood();
	// rectangleTwelve.addFixture(rectShapeFive);
	// rectangleTwelve.setMass(MassType.NORMAL);
	// rectangleTwelve.translate(26, 4);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleTwelve);
	// System.out.println("ID of rectangleOne is:" + rectangleTwelve.getId());
	//
	// // Bridge1
	// Rectangle bridge = new Rectangle(4.5, 0.5);
	// GameObject rectangleBridge = new Stone();
	// BodyFixture rectangleBridgeFixture = new BodyFixture(bridge);
	// rectangleBridgeFixture.setFriction(frictionAmount);
	// rectangleBridge.addFixture(bridge);
	// rectangleBridge.setMass(MassType.NORMAL);
	// rectangleBridge.translate(16.75, 7.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleBridge);
	// System.out.println("ID of rectangleBridge is:" +
	// rectangleBridge.getId());
	//
	// // Bridge2
	// Rectangle bridgeTwo = new Rectangle(3.5, 0.5);
	// GameObject rectangleBridgeTwo = new Stone();
	// BodyFixture rectangleBridgeTwoFixture = new BodyFixture(bridgeTwo);
	// rectangleBridgeTwoFixture.setFriction(frictionAmount);
	// rectangleBridgeTwo.addFixture(bridgeTwo);
	// rectangleBridgeTwo.setMass(MassType.NORMAL);
	// rectangleBridgeTwo.translate(20.7, 7.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleBridgeTwo);
	// System.out.println("ID of rectangleBridge is:" +
	// rectangleBridgeTwo.getId());
	//
	// // Bridge3
	// GameObject rectangleBridgeThree = new Stone();
	// rectangleBridgeFixture.setFriction(frictionAmount);
	// rectangleBridgeThree.addFixture(bridge);
	// rectangleBridgeThree.setMass(MassType.NORMAL);
	// rectangleBridgeThree.translate(24.25, 7.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleBridgeThree);
	// System.out.println("ID of rectangleBridge is:" +
	// rectangleBridgeThree.getId());
	//
	// GameObject rectanglThriteen = new Wood();
	// rectanglThriteen.addFixture(rectShape);
	// rectanglThriteen.setMass(MassType.NORMAL);
	// rectanglThriteen.translate(15.45, 8.0);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectanglThriteen);
	// System.out.println("ID of rectangleOne is:" + rectanglThriteen.getId());
	//
	// // third pig
	// GameObject pigSix = new NormalPig();
	// pigSix.addFixture(c1Fixture);
	// pigSix.addFixture(c2Fixture);
	// pigSix.addFixture(rm);
	// pigSix.setMass(MassType.NORMAL);
	// pigSix.translate(17.5, 8.7);
	// world.addBody(pigSix);
	// System.out.println("ID of pigOne is:" + pigSix.getId());
	//
	// // Stange5
	// GameObject rectangleFourteen = new Wood();
	// rectangleFourteen.addFixture(rectShapeFive);
	// rectangleFourteen.setMass(MassType.NORMAL);
	// rectangleFourteen.translate(18.5, 9.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleFourteen);
	// System.out.println("ID of rectangleOne is:" + rectangleFourteen.getId());
	//
	// // Stange6
	// GameObject rectangleFifteen = new Wood();
	// rectangleFifteen.addFixture(rectShapeFive);
	// rectangleFifteen.setMass(MassType.NORMAL);
	// rectangleFifteen.translate(19.0, 9.5);
	// // rectangle.getLinearVelocity().set(-5.0, 0.0);
	// world.addBody(rectangleFifteen);
	// System.out.println("ID of rectangleOne is:" + rectangleFifteen.getId());
	//
	// return world;
	//
	// }
	//
	// // Outsourced methods for generating objects
	//
	// private static NormalPig createNormalPig(double xCoordinate, double
	// yCoordinate) {
	// Circle c1 = new Circle(0.5);
	// BodyFixture c1Fixture = new BodyFixture(c1);
	// c1Fixture.setFriction(frictionAmount);
	//
	// Circle c2 = new Circle(0.5);
	// BodyFixture c2Fixture = new BodyFixture(c2);
	// c2Fixture.setFriction(frictionAmount);
	//
	// Rectangle rm = new Rectangle(0.2, 1.0);
	// BodyFixture rF = new BodyFixture(rm);
	// rF.setFriction(frictionAmount);
	//
	// c1.translate(-0.1, 0.0);
	// c2.translate(0.1, 0.0);
	//
	// NormalPig pigOne = new NormalPig();
	//
	// pigOne.addFixture(c1Fixture);
	// pigOne.addFixture(c2Fixture);
	// pigOne.addFixture(rF);
	// pigOne.setMass(MassType.NORMAL);
	// pigOne.translate(xCoordinate, yCoordinate);
	//
	// world.addBody(pigOne);
	//
	// return pigOne;
	// }
	//
	// private static void createFloor(double xDimension, double yDimension) {
	//
	// Rectangle floorRect = new Rectangle(xDimension, yDimension);
	// BodyFixture floorFixture = new BodyFixture(floorRect);
	// Floor floor = new Floor();
	//
	// floor.addFixture(floorFixture);
	// floor.setMass(MassType.INFINITE);
	// // move the floor down a bit
	// floor.translate(0.0, 0.0);
	// world.addBody(floor);
	//
	// }
	//
	// private static Floor createBlock(double xPosition, double yPosition,
	// double width, double height) {
	// Rectangle block = new Rectangle(width, height);
	// BodyFixture floorFixture = new BodyFixture(block);
	// Floor floor = new Floor();
	//
	// floor.addFixture(floorFixture);
	// floor.setMass(MassType.INFINITE);
	// // move the floor down a bit
	// System.out.println(yPosition);
	// floor.translate(xPosition, yPosition);
	// world.addBody(floor);
	// return floor;
	//
	// }
	//
	// public static World initializeExtractorWorld(List<List<ABObject>>
	// worldFeatures) {
	//
	// world = new World();
	//
	// List<ABObject> pigsList = worldFeatures.get(0);
	// List<ABObject> birdsList = worldFeatures.get(1);
	// List<ABObject> blocksList = worldFeatures.get(2);
	// List<ABObject> hillsList = worldFeatures.get(3);
	// List<ABObject> tntList = worldFeatures.get(4);
	//
	// double x, y, width, height;
	//
	// for (ABObject pig : pigsList) {
	// NormalPig newPig = createNormalPig(pig.getCenterX() * 0.1,
	// pig.getCenterY() * 0.1);
	// System.out.println("Pig " + pig.getX() + pig.getCenterY());
	// }
	//
	// for (ABObject block : blocksList) {
	//
	// double scale = 0.2;
	// x = block.getX() * 0.1;
	// y = block.getY() * 0.1 - 30;
	// width = block.getWidth() * scale;
	// height = block.getHeight() * scale;
	//
	// Floor blocks = createBlock(x, y, width, height);
	// System.out.println("Block " + x + " " + y + " " + width + " " + height);
	// }
	//
	// createFloor(70, 1);
	//
	// return world;
	// }
}
