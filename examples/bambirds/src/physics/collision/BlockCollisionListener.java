package physics.collision;

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.CollisionAdapter;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Vector2;

import physics.materials.GameObject;
import physics.materials.TNT;
import physics.pigtypes.PigFacade;

public class BlockCollisionListener extends CollisionAdapter {

	GameObject gameObjectOne;
	GameObject gameObjectTwo;
	World world;
	Double vectorValueOne;
	Double vectorValueTwo;
	Double damage;

	public BlockCollisionListener(GameObject blockOne, GameObject blockTwo, World world) {
		gameObjectOne = blockOne;
		gameObjectTwo = blockTwo;
		this.world = world;
	}

	@Override
	public boolean collision(Body bodyOne, BodyFixture fixture1, Body bodyTwo, BodyFixture fixture2, Penetration arg4) {

		if ((bodyOne == gameObjectOne && bodyTwo == gameObjectTwo)) {
			vectorValueOne = Math.sqrt(bodyOne.getLinearVelocity().x * bodyOne.getLinearVelocity().x
					+ bodyOne.getLinearVelocity().y * bodyOne.getLinearVelocity().y);
			vectorValueTwo = Math.sqrt(bodyTwo.getLinearVelocity().x * bodyTwo.getLinearVelocity().x
					+ bodyTwo.getLinearVelocity().y * bodyTwo.getLinearVelocity().y);
			damage = Math.abs(vectorValueOne - vectorValueTwo);

			if (damage > gameObjectTwo.getDamageThreshold()) {
				if(gameObjectTwo instanceof PigFacade){
					gameObjectTwo.setHealth(gameObjectTwo.getHealth() - damage.intValue() * 800);
				}
				gameObjectTwo.setHealth(gameObjectTwo.getHealth() - damage.intValue() * 6);
			}
			if (damage > gameObjectOne.getDamageThreshold()) {
				if(gameObjectOne instanceof PigFacade){
					gameObjectOne.setHealth(gameObjectOne.getHealth() - damage.intValue() * 800);
				}
				gameObjectOne.setHealth(gameObjectOne.getHealth() - damage.intValue() * 6);
			}
			if (gameObjectTwo.getHealth() <= 0) {
				world.removeBody(bodyTwo);

				// Triggering explosion if TNT is hit
				if (gameObjectTwo instanceof TNT) {
					List<Body> worldBlocks = world.getBodies();

					double xExplo = gameObjectTwo.getWorldCenter().x;
					double yExplo = gameObjectTwo.getWorldCenter().y;

					double exploForce = -50;

					for (Body object : worldBlocks) {
						GameObject gameO = (GameObject) object;
						Vector2 exploVector = new Vector2(xExplo - object.getWorldCenter().x,
								yExplo - object.getWorldCenter().y);
						double diffX = Math.abs(xExplo - object.getWorldCenter().x);
						double diffY = Math.abs(yExplo - object.getWorldCenter().y);

						double d = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
						exploVector.multiply(Math.sqrt(1 / d) * exploForce);
						object.applyForce(exploVector);
						gameO.setHealth(gameO.getHealth() - exploVector.getMagnitude());
					}
				}
			}

		} else if ((bodyOne == gameObjectTwo && bodyTwo == gameObjectOne)) {
			vectorValueOne = Math.sqrt(bodyOne.getLinearVelocity().x * bodyOne.getLinearVelocity().x
					+ bodyOne.getLinearVelocity().y * bodyOne.getLinearVelocity().y);
			vectorValueTwo = Math.sqrt(bodyTwo.getLinearVelocity().x * bodyTwo.getLinearVelocity().x
					+ bodyTwo.getLinearVelocity().y * bodyTwo.getLinearVelocity().y);
			damage = Math.abs(vectorValueOne - vectorValueTwo);

			if (damage > gameObjectOne.getDamageThreshold()) {
				if(gameObjectOne instanceof PigFacade){
					gameObjectOne.setHealth(gameObjectOne.getHealth() - damage.intValue() * 800);
				}
				gameObjectOne.setHealth(gameObjectOne.getHealth() - damage.intValue() * 4);
			}
			if (damage > gameObjectTwo.getDamageThreshold()) {
				if(gameObjectTwo instanceof PigFacade){
					gameObjectTwo.setHealth(gameObjectTwo.getHealth() - damage.intValue() * 500);
				}
				gameObjectTwo.setHealth(gameObjectTwo.getHealth() - damage.intValue() * 4);
			}
			if (gameObjectTwo.getHealth() <= 0) {
				world.removeBody(bodyOne);

				// Triggering explosion if TNT is hit
				if (gameObjectOne instanceof TNT) {

					List<GameObject> worldBlocks = new ArrayList<>();

					double xExplo = gameObjectOne.getWorldCenter().x;
					double yExplo = gameObjectOne.getWorldCenter().y;

					// TODO: exploForce has to be fitted
					double exploForce = 1;

					for (GameObject object : worldBlocks) {
						Vector2 exploVector = new Vector2(xExplo - object.getWorldCenter().x,
								yExplo - object.getWorldCenter().y);
						double diffX = Math.abs(xExplo - object.getWorldCenter().x);
						double diffY = Math.abs(yExplo - object.getWorldCenter().y);

						double d = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
						exploVector.multiply(d * exploForce);
						object.applyForce(exploVector);
					}
				}
			}
		}
		return true;
	}

}
