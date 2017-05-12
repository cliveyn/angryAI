package physics.collision;

import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.CollisionAdapter;
import org.dyn4j.dynamics.World;

import physics.birdtypes.BirdFacade;
import physics.materials.GameObject;
import physics.materials.Stone;
import physics.pigtypes.PigFacade;
import physics.pigtypes.SmallPig;

public class BirdCollisionListener extends CollisionAdapter {

	BirdFacade birdObject;
	GameObject gameObject;
	World world;
	Double vectorValue;

	public BirdCollisionListener(BirdFacade bird, GameObject pig, World world) {
		birdObject = bird;
		gameObject = pig;
		this.world = world;
	}

	@Override
	public boolean collision(Body bodyOne, BodyFixture fixture1, Body bodyTwo, BodyFixture fixture2, Penetration arg4) {

		if ((bodyOne == birdObject && bodyTwo == gameObject)) {
			vectorValue = Math.sqrt(bodyOne.getLinearVelocity().x * bodyOne.getLinearVelocity().x
					+ bodyOne.getLinearVelocity().y * bodyOne.getLinearVelocity().y);

			if (vectorValue > gameObject.getDamageThreshold()) {
				if (gameObject instanceof PigFacade) {
					System.out.println("PIG HIT");
					gameObject.setHealth(gameObject.getHealth() - vectorValue.intValue() * 50);
				} else {
					System.out.println("NORMAL HIT");
					gameObject.setHealth(gameObject.getHealth() - vectorValue.intValue() * 13);
				}

			}

			if (gameObject.getHealth() <= 0) {
				world.removeBody(bodyTwo);
			}

		} else if ((bodyOne == gameObject && bodyTwo == birdObject)) {
			vectorValue = Math.sqrt(bodyTwo.getLinearVelocity().x * bodyTwo.getLinearVelocity().x
					+ bodyTwo.getLinearVelocity().y * bodyTwo.getLinearVelocity().y);
			if (vectorValue > gameObject.getDamageThreshold()) {
				if (gameObject instanceof PigFacade) {
					gameObject.setHealth(gameObject.getHealth() - vectorValue.intValue() * 500);
				} else if(gameObject instanceof Stone){
					gameObject.setHealth(gameObject.getHealth() - vectorValue.intValue() * 5);
				} else {
					gameObject.setHealth(gameObject.getHealth() - vectorValue.intValue() * 13);
				}
			}
			if (gameObject.getHealth() <= 0) {
				world.removeBody(bodyOne);
			}
		}
		return true;
	}

}
